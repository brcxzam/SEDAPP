package com.brcxzam.sedapp.database;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.brcxzam.sedapp.CreateAnexo_2_1Mutation;
import com.brcxzam.sedapp.DeleteAnexo_2_1Mutation;
import com.brcxzam.sedapp.apollo_client.ApolloConnector;
import com.brcxzam.sedapp.type.IAnexo_2_1;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class NetworkStateChecker extends BroadcastReceiver {

    Context context;
    Anexo21Dao anexo21Dao;
    DeleteOfflineDao deleteOfflineDao;
    private static final String ERROR_REQUEST = "ERROR REQUEST GQL";

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;

        NetworkInfo networkInfo = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnected();

        if (isConnected && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE) {

            AppDatabase appDatabase = AppDatabase.getAppDatabase(context);
            anexo21Dao = appDatabase.anexo21Dao();
            deleteOfflineDao = appDatabase.deleteOfflineDao();

            List<Anexo21> anexo21sUnsynced = anexo21Dao.readAllUnsynced();
            for (Anexo21 anexo21: anexo21sUnsynced) {
                CreateAnexo_2_1Mutation anexo21Mutation = CreateAnexo_2_1Mutation.builder()
                        .data(IAnexo_2_1.builder()
                                .periodo(anexo21.getPeriodo())
                                .fecha(anexo21.getFecha())
                                .s1_p1(anexo21.getS1_p1())
                                .s1_p2(anexo21.getS1_p2())
                                .s1_p3(anexo21.getS1_p3())
                                .s1_p4(anexo21.getS1_p4())
                                .s1_total_no(anexo21.getS1_total_no())
                                .s1_total_si(anexo21.getS1_total_si())
                                .s2_p1(anexo21.getS2_p1())
                                .s2_p2(anexo21.getS2_p2())
                                .s2_p3(anexo21.getS2_p3())
                                .s2_p4(anexo21.getS2_p4())
                                .s2_p5(anexo21.getS2_p5())
                                .s2_p6(anexo21.getS2_p6())
                                .s2_suma_no_cumple(anexo21.getS2_suma_no_cumple())
                                .s2_suma_parcialmente(anexo21.getS2_suma_parcialmente())
                                .s2_suma_cumple(anexo21.getS2_suma_cumple())
                                .s3_p1(anexo21.getS3_p1())
                                .s3_p2(anexo21.getS3_p2())
                                .s3_p3(anexo21.getS3_p3())
                                .s3_suma_no_cumple(anexo21.getS3_suma_no_cumple())
                                .s3_suma_parcialmente(anexo21.getS3_suma_parcialmente())
                                .s3_suma_cumple(anexo21.getS3_suma_cumple())
                                .total(anexo21.getTotal())
                                .aplicador(anexo21.getAplicador())
                                .iEId("1")
                                .uERFC(anexo21.getUERFC())
                                .build())
                        .build();
                createEvaluation(anexo21Mutation,anexo21.getId());
            }

            List<DeleteOffline> anexo21sDelete = deleteOfflineDao.readAnexo21();
            for (DeleteOffline deleteOffline: anexo21sDelete) {
                DeleteAnexo_2_1Mutation deleteAnexo21Mutation = DeleteAnexo_2_1Mutation.builder().
                        id(deleteOffline.getId_row())
                        .build();
                deleteAnexo21(deleteAnexo21Mutation, deleteOffline.getId_row(),deleteOffline.getId());
            }

        }
    }

    private void createEvaluation(CreateAnexo_2_1Mutation anexo21Mutation, final String id) {
        ApolloConnector.setupApollo(context).mutate(anexo21Mutation)
                .enqueue(new ApolloCall.Callback<CreateAnexo_2_1Mutation.Data>() {
                    @Override
                    public void onResponse(@NotNull final Response<CreateAnexo_2_1Mutation.Data> response) {
                        if (response.data() != null){
                            anexo21Dao.update(null,id);
                        }
                    }
                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        Log.d(ERROR_REQUEST, Objects.requireNonNull(e.getMessage()));
                    }
                });
    }

    private void deleteAnexo21(DeleteAnexo_2_1Mutation deleteAnexo21Mutation, final String id_row, final int id) {
        ApolloConnector.setupApollo(context).mutate(deleteAnexo21Mutation)
                .enqueue(new ApolloCall.Callback<DeleteAnexo_2_1Mutation.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<DeleteAnexo_2_1Mutation.Data> response) {
                        if (response.data() != null) {
                            DeleteOffline deleteOffline = new DeleteOffline("Anexo_2_1", id_row);
                            deleteOffline.setId(id);
                            deleteOfflineDao.delete(deleteOffline);
                        }
                    }
                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        Log.d(ERROR_REQUEST, Objects.requireNonNull(e.getMessage()));
                    }
                });
    }
}
