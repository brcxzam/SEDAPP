package com.brcxzam.sedapp.charts;


import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.chart.common.listener.Event;
import com.anychart.chart.common.listener.ListenersInterface;
import com.anychart.charts.Pie;
import com.anychart.enums.Align;
import com.anychart.enums.LegendLayout;
import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.brcxzam.sedapp.R;
import com.brcxzam.sedapp.ReadAllAnexo_2_1Query;
import com.brcxzam.sedapp.apollo_client.ApolloConnector;
import com.brcxzam.sedapp.database.Anexo21;
import com.brcxzam.sedapp.database.Anexo21Dao;
import com.brcxzam.sedapp.database.AppDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class Charts extends Fragment {

    private final String ERROR_CONNECTION = "ERROR CONNECTION GQL " + getClass().getSimpleName();
    private Anexo21Dao anexo21Dao;
    private List<Anexo21> list = new ArrayList<>();
    private AnyChartView anyChartView;
    private Pie pie;

    public Charts() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_charts, container, false);

        if (getActivity() == null) return view;

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.charts);

        // Database
        AppDatabase appDatabase = AppDatabase.getAppDatabase(getContext());
        anexo21Dao = appDatabase.anexo21Dao();

        anyChartView = view.findViewById(R.id.any_chart_view);
        anyChartView.setProgressBar(view.findViewById(R.id.progress_bar));

        pie = AnyChart.pie();

        pie.setOnClickListener(new ListenersInterface.OnClickListener(new String[]{"x", "value"}) {
            @Override
            public void onClick(Event event) {
                Toast.makeText(getContext(), event.getData().get("x") + ":" + event.getData().get("value"), Toast.LENGTH_SHORT).show();
            }
        });

        getLocalData();
        fetchAnexo21();

        return view;
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
//                                refreshLayout.setRefreshing(false);
                            }
                        });
                    }
                });
    }

    private void getLocalData() {
        list.clear();
        list.addAll(anexo21Dao.readALL());
        List<DataEntry> data = new ArrayList<>();
        data.add(new ValueDataEntry("Alta Vulnerabilidad", anexo21Dao.readCountAltaVulnerabilidad()));
        data.add(new ValueDataEntry("Mediana Vulnerabilidad", anexo21Dao.readCountMedianaVulnerabilidad()));
        data.add(new ValueDataEntry("Baja Vulnerabilidad", anexo21Dao.readCountBajaVulnerabilidad()));
        pie.data(data);

        pie.title("Unidades Económicas Evaluadas");

        pie.labels().position("outside");

        pie.legend().title().enabled(true);
        pie.legend().title()
                .text("Descripción")
                .padding(0d, 0d, 10d, 0d);

        pie.legend()
                .position("center-bottom")
                .itemsLayout(LegendLayout.VERTICAL_EXPANDABLE)
                .align(Align.CENTER);

        anyChartView.setChart(pie);
    }

}
