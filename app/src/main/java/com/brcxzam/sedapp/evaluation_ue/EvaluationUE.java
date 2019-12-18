package com.brcxzam.sedapp.evaluation_ue;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.brcxzam.sedapp.DeleteAnexo_2_1Mutation;
import com.brcxzam.sedapp.MainActivity;
import com.brcxzam.sedapp.R;
import com.brcxzam.sedapp.ReadAllAnexo_2_1Query;
import com.brcxzam.sedapp.apollo_client.ApolloConnector;
import com.brcxzam.sedapp.apollo_client.Token;
import com.brcxzam.sedapp.database.Anexo21;
import com.brcxzam.sedapp.database.Anexo21Dao;
import com.brcxzam.sedapp.database.AppDatabase;
import com.brcxzam.sedapp.database.DeleteOffline;
import com.brcxzam.sedapp.database.DeleteOfflineDao;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;



public class EvaluationUE extends Fragment {

    private EvaluationUEAdapter mAdapter = new EvaluationUEAdapter();;
    private View viewSnack;

    private final String ERROR_CONNECTION = "ERROR CONNECTION GQL";

    private Anexo21Dao anexo21Dao;
    private DeleteOfflineDao deleteOfflineDao;

    private List<Anexo21> list = new ArrayList<>();

    private Handler handler = new Handler();
    private final int TIME = 1000 * 5;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            fetchAnexo21();
            handler.postDelayed(runnable,TIME);
        }
    };

    public EvaluationUE() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AppDatabase appDatabase = AppDatabase.getAppDatabase(getContext());
        anexo21Dao = appDatabase.anexo21Dao();
        deleteOfflineDao = appDatabase.deleteOfflineDao();

        fetchAnexo21();

        handler.postDelayed(runnable,TIME);
        View view = inflater.inflate(R.layout.fragment_evaluation_ue, container, false);
        final FloatingActionButton fab = ((MainActivity) Objects.requireNonNull(getActivity())).findViewById(R.id.fab);
        final NavController navController = Navigation.findNavController(getActivity(),R.id.nav_host_fragment);
        final NavOptions navOptions = new NavOptions.Builder()
                .setEnterAnim(R.anim.slide_in_right)
                .setExitAnim(R.anim.slide_out_left)
                .setPopEnterAnim(R.anim.slide_in_left)
                .setPopExitAnim(R.anim.slide_out_right)
                .build();
        if (!fab.isShown()) {
            fab.show();
        }
        fab.hide(new FloatingActionButton.OnVisibilityChangedListener() {
            @Override
            public void onHidden(FloatingActionButton fab) {
                super.onHidden(fab);
                fab.setImageResource(R.drawable.ic_add_black_24dp);
                fab.show();
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (navController.getCurrentDestination().getId() == R.id.evaluationUE) {
                    navController.navigate(R.id.action_evaluationUE_to_questionsUE,null,navOptions);
                }
            }
        });
        viewSnack = ((MainActivity) Objects.requireNonNull(getActivity())).findViewById(R.id.viewSnack);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewUE);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration itemDecor = new DividerItemDecoration(Objects.requireNonNull(getContext()),DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecor);

        mAdapter.setAnexo21ArrayList(list);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new EvaluationUEAdapter.OnItemClickListener() {
            @Override
            public void onDeleteClick(int position) {
                final Anexo21 data = list.get(position);
                Snackbar.make(viewSnack, "¿Quieres eliminar la evaluación de \""+data.getRazon_social()+"\"?",
                        Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.positive_button), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                deleteAnexo21(data.getId());
                            }
                        })
                        .show();
            }
        });

        getLocalData();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(runnable);
    }

    private void errorMessage() {
        Snackbar.make(viewSnack, R.string.error_connection, Snackbar.LENGTH_SHORT)
                .show();
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
                            for (ReadAllAnexo_2_1Query.Anexo_2_1 anexo21: anexo21List) {
                                Anexo21 res = new Anexo21();
                                res.setId(anexo21.id());
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
        mAdapter.setAnexo21ArrayList(list);
        mAdapter.notifyDataSetChanged();
    }
}
