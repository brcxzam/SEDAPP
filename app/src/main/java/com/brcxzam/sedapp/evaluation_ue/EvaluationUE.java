package com.brcxzam.sedapp.evaluation_ue;


import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.brcxzam.sedapp.DeleteAnexo_2_1Mutation;
import com.brcxzam.sedapp.R;
import com.brcxzam.sedapp.ReadAllAnexo_2_1Query;
import com.brcxzam.sedapp.apollo_client.ApolloConnector;
import com.brcxzam.sedapp.database.Anexo21;
import com.brcxzam.sedapp.database.Anexo21Dao;
import com.brcxzam.sedapp.database.AppDatabase;
import com.brcxzam.sedapp.database.DeleteOffline;
import com.brcxzam.sedapp.database.DeleteOfflineDao;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EvaluationUE extends Fragment {

    private EvaluationUEAdapter mAdapter = new EvaluationUEAdapter();
    private final String ERROR_CONNECTION = "ERROR CONNECTION GQL " + getClass().getSimpleName();
    private Anexo21Dao anexo21Dao;
    private DeleteOfflineDao deleteOfflineDao;
    private List<Anexo21> list = new ArrayList<>();
    private SwipeRefreshLayout refreshLayout;

    public EvaluationUE() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_evaluation_ue, container, false);
        if (getActivity() == null) return view;

        final NavController navController = Navigation.findNavController(getActivity(),R.id.nav_host_fragment);

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.unidades_economicas);

        // Search
        TextInputLayout inputLayoutSearch = view.findViewById(R.id.action_search);
        EditText editTextSearch = inputLayoutSearch.getEditText();
        assert editTextSearch != null;
        editTextSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    closeKeyboard();
                }
                return false;
            }
        });
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Database
        AppDatabase appDatabase = AppDatabase.getAppDatabase(getContext());
        anexo21Dao = appDatabase.anexo21Dao();
        deleteOfflineDao = appDatabase.deleteOfflineDao();

        // RecyclerView
        final RecyclerView recyclerView = view.findViewById(R.id.recyclerViewUE);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration itemDecor = new DividerItemDecoration(Objects.requireNonNull(getContext()),DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecor);
        mAdapter.setAnexo21List(list);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new EvaluationUEAdapter.OnItemClickListener() {
            @Override
            public void onDeleteClick(int position) {
                final Anexo21 data = list.get(position);
                Snackbar.make(recyclerView, "¿Quieres eliminar la evaluación de \""+data.getRazon_social()+"\"?",
                        Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.positive_button), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                deleteAnexo21(data.getId());
                            }
                        })
                        .show();
            }

            @Override
            public void onItemClick(int position) {
                Anexo21 data = list.get(position);
                Bundle bundle = new Bundle();
                bundle.putString("razon_social",data.getRazon_social());
                bundle.putString("periodo",data.getPeriodo());
                bundle.putString("fecha",data.getFecha());
                bundle.putString("UERFC",data.getUERFC());
                bundle.putInt("s1_p1",data.getS1_p1());
                bundle.putInt("s1_p2",data.getS1_p2());
                bundle.putInt("s1_p3",data.getS1_p3());
                bundle.putInt("s1_p4",data.getS1_p4());
                bundle.putInt("s2_p5",data.getS2_p1());
                bundle.putInt("s2_p6",data.getS2_p2());
                bundle.putInt("s2_p7",data.getS2_p3());
                bundle.putInt("s2_p8",data.getS2_p4());
                bundle.putInt("s2_p9",data.getS2_p5());
                bundle.putInt("s2_p10",data.getS2_p6());
                bundle.putInt("s3_p11",data.getS3_p1());
                bundle.putInt("s3_p12",data.getS3_p2());
                bundle.putInt("s3_p13",data.getS3_p3());
                navController.navigate(R.id.action_global_evaluation, bundle);
            }
        });

        // Refresh
        refreshLayout = view.findViewById(R.id.refresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchAnexo21();
            }
        });

        // Get Data Local and Remote
        getLocalData();
        fetchAnexo21();

        return view;
    }

    private void closeKeyboard() {
        View view = Objects.requireNonNull(getActivity()).getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            Objects.requireNonNull(imm).hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void fetchAnexo21() {
        ApolloConnector
                .setupApollo(getContext())
                .query(ReadAllAnexo_2_1Query.builder().build())
                .enqueue(new ApolloCall.Callback<ReadAllAnexo_2_1Query.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<ReadAllAnexo_2_1Query.Data> response) {
                        if (response.data() != null) {
                            List<ReadAllAnexo_2_1Query.Anexo_2_1> anexo21List = response.data().Anexo_2_1s();
                            List<Anexo21> anexo21s = new ArrayList<>();
                            if (anexo21List != null) {
                                for (ReadAllAnexo_2_1Query.Anexo_2_1 anexo21: anexo21List) {
                                    Anexo21 res = new Anexo21();
                                    res.setId(Objects.requireNonNull(anexo21.id()));
                                    res.setPeriodo(anexo21.periodo());
                                    res.setFecha(anexo21.fecha());
                                    res.setS1_p1(anexo21.s1_p1());
                                    res.setS1_p2(anexo21.s1_p2());
                                    res.setS1_p3(anexo21.s1_p3());
                                    res.setS1_p4(anexo21.s1_p4());
                                    res.setS1_total_no(anexo21.s1_total_no());
                                    res.setS1_total_si(anexo21.s1_total_si());
                                    res.setS2_p1(anexo21.s2_p1());
                                    res.setS2_p2(anexo21.s2_p2());
                                    res.setS2_p3(anexo21.s2_p3());
                                    res.setS2_p4(anexo21.s2_p4());
                                    res.setS2_p5(anexo21.s2_p5());
                                    res.setS2_p6(anexo21.s2_p6());
                                    res.setS2_suma_no_cumple(anexo21.s2_suma_no_cumple());
                                    res.setS2_suma_parcialmente(anexo21.s2_suma_parcialmente());
                                    res.setS2_suma_cumple(anexo21.s2_suma_cumple());
                                    res.setS3_p1(anexo21.s3_p1());
                                    res.setS3_p2(anexo21.s3_p2());
                                    res.setS3_p3(anexo21.s3_p3());
                                    res.setS3_suma_no_cumple(anexo21.s3_suma_no_cumple());
                                    res.setS3_suma_parcialmente(anexo21.s3_suma_parcialmente());
                                    res.setS3_suma_cumple(anexo21.s3_suma_cumple());
                                    res.setTotal(anexo21.total());
                                    res.setAplicador(anexo21.aplicador());
                                    res.setIEId(anexo21.IEId());
                                    res.setInstitucion_educativa(anexo21.IE().institucion_educativa());
                                    res.setUERFC(anexo21.UERFC());
                                    res.setRazon_social(anexo21.UE().razon_social());
                                    res.setAccion(null);
                                    anexo21s.add(res);
                                }
                            }
                            anexo21Dao.deleteAll();
                            anexo21Dao.createALL(anexo21s);
                            Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    getLocalData();
                                }
                            });
                        }
                    }
                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        Log.d(ERROR_CONNECTION, Objects.requireNonNull(e.getMessage()));
                        Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                refreshLayout.setRefreshing(false);
                            }
                        });
                    }
                });
    }

    private void deleteAnexo21(final String id) {
        DeleteAnexo_2_1Mutation deleteAnexo21Mutation = DeleteAnexo_2_1Mutation.builder()
                .id(id)
                .build();
        ApolloConnector
                .setupApollo(getContext())
                .mutate(deleteAnexo21Mutation)
                .refetchQueries(ReadAllAnexo_2_1Query.builder().build())
                .enqueue(new ApolloCall.Callback<DeleteAnexo_2_1Mutation.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<DeleteAnexo_2_1Mutation.Data> response) {
                        if (response.data() != null) {
                            Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    deleteLocalAnexo21(id);
                                }
                            });
                        }
                    }
                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        Log.d(ERROR_CONNECTION, Objects.requireNonNull(e.getMessage()));
                        deleteOfflineDao.create(new DeleteOffline("Anexo_2_1",id));
                        Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                deleteLocalAnexo21(id);
                            }
                        });
                    }
                });
    }

    private void deleteLocalAnexo21(String id) {
        Anexo21 anexo21 = new Anexo21();
        anexo21.setId(id);
        anexo21Dao.delete(anexo21);
        getLocalData();
    }

    private void getLocalData() {
        list.clear();
        list.addAll(anexo21Dao.readALL());
        mAdapter.setAnexo21List(list);
        mAdapter.notifyDataSetChanged();
        refreshLayout.setRefreshing(false);
    }
}
