package com.brcxzam.sedapp.evaluation_ue;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.apollographql.apollo.fetcher.ApolloResponseFetchers;
import com.brcxzam.sedapp.MainActivity;
import com.brcxzam.sedapp.R;
import com.brcxzam.sedapp.ReadAllAnexo_2_1Query;
import com.brcxzam.sedapp.ReadAllUEsQuery;
import com.brcxzam.sedapp.apollo_client.ApolloConnector;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class EvaluationUE extends Fragment {

    private RecyclerView recyclerView;
    private EvaluationUEAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private View viewSnack;
    // ArrayList contenedor de los datos de los Anexos 2.1
    List<ReadAllAnexo_2_1Query.Anexo_2_1> anexo21List = new ArrayList<>();

    public EvaluationUE() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fetchAnexo21();
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
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewUE);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        return view;
    }

    private void fetchAnexo21() {
        ApolloConnector.setupApollo(getContext()).query(new ReadAllAnexo_2_1Query())
                .responseFetcher(ApolloResponseFetchers.CACHE_AND_NETWORK)
                .enqueue(new ApolloCall.Callback<ReadAllAnexo_2_1Query.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<ReadAllAnexo_2_1Query.Data> response) {
                        if (response.data() != null) {
                            anexo21List = response.data().Anexo_2_1s();
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mAdapter = new EvaluationUEAdapter(anexo21List);
                                    recyclerView.setAdapter(mAdapter);
                                    mAdapter.setOnItemClickListener(new EvaluationUEAdapter.OnItemClickListener() {
                                        @Override
                                        public void onDeleteClick(int position) {
                                            ReadAllAnexo_2_1Query.Anexo_2_1 data = anexo21List.get(position);
                                            Snackbar.make(viewSnack, "¿Quieres eliminar la evaluación de \""+data.UE().razon_social()+"\"?",
                                                    Snackbar.LENGTH_LONG)
                                                    .setAction(getString(R.string.positive_button), new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {

                                                        }
                                                    })
                                                    .show();
                                        }

                                        @Override
                                        public void onEditClick(int position) {
                                            Toast.makeText(getContext(), ""+position, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {

                    }
                });
    }
}
