package com.brcxzam.sedapp.charts;


import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.anychart.APIlib;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.charts.Pie;
import com.anychart.core.cartesian.series.Bar;
import com.anychart.data.Mapping;
import com.anychart.data.Set;
import com.anychart.enums.Align;
import com.anychart.enums.HoverMode;
import com.anychart.enums.LegendLayout;
import com.anychart.enums.TooltipDisplayMode;
import com.anychart.enums.TooltipPositionMode;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class Charts extends Fragment {

    private final String ERROR_CONNECTION = "ERROR CONNECTION GQL " + getClass().getSimpleName();
    private Anexo21Dao anexo21Dao;
    private AnyChartView anyChartView1, anyChartView2;
    private DecimalFormat formater = new DecimalFormat("0.00");
    private String[] palette = new String[] { "#80deea", "#00acc1", "#00838f", "#29b6f6", "#0277bd", "#0277bd", "#8c9eff", "#9575cd", "#ce93d8", "#8e24aa"};
    private ProgressBar progressBar;

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

        anyChartView1 = view.findViewById(R.id.any_chart_view);
        anyChartView2 = view.findViewById(R.id.any_chart_view1);
        progressBar = view.findViewById(R.id.progress_bar);

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
        firstChart();
        secondChart();
    }

    private void firstChart() {
        APIlib.getInstance().setActiveAnyChartView(anyChartView1);
        anyChartView1.setProgressBar(progressBar);

        Pie pie = AnyChart.pie();

        int alta = anexo21Dao.readCountAltaVulnerabilidad();
        int mediana = anexo21Dao.readCountMedianaVulnerabilidad();
        int baja = anexo21Dao.readCountBajaVulnerabilidad();

        List<DataEntry> data = new ArrayList<>();
        data.add(new ValueDataEntry("Alta", alta));
        data.add(new ValueDataEntry("Mediana", mediana));
        data.add(new ValueDataEntry("Baja", baja));
        pie.data(data);

        pie.title("Unidades Económicas Evaluadas");

        pie.labels().position("outside");

        pie.legend().title().enabled(true);
        pie.legend().title()
                .text("Vulnerabilidad")
                .padding(0d, 0d, 10d, 0d);

        pie.legend()
                .position("center-bottom")
                .itemsLayout(LegendLayout.HORIZONTAL_EXPANDABLE)
                .align(Align.CENTER);

        pie.animation(true);

        pie.palette(palette);

        anyChartView1.setChart(pie);
    }

    private void secondChart() {
        APIlib.getInstance().setActiveAnyChartView(anyChartView2);
        Cartesian vertical = AnyChart.vertical();

        vertical.animation(true)
                .title("Porcentaje por Unidad Económica");

        List<DataEntry> data = new ArrayList<>();
        List<Anexo21> anexo21List = anexo21Dao.readALL();


        for (Anexo21 anexo21 :  anexo21List) {
            double total = Double.parseDouble(formater.format(anexo21.getTotal()));
            data.add(new ValueDataEntry(anexo21.getRazon_social(),total));
        }

        Set set = Set.instantiate();
        set.data(data);
        Mapping barData = set.mapAs("{ x: 'x', value: 'value' }");

        Bar bar = vertical.bar(barData);
        bar.labels()
                .position("center")
                .fontColor("#FFFFFF")
                .format("{%X}, {%Value} %");

        bar.color("#38A3A5");

        vertical.yScale().minimum(0d);

        vertical.labels(true);

        vertical.tooltip()
                .displayMode(TooltipDisplayMode.UNION)
                .positionMode(TooltipPositionMode.POINT)
                .unionFormat(
                        "function() {\n" +
                                "      return 'Plain: $' + this.points[1].value + ' mln' +\n" +
                                "        '\\n' + 'Fact: $' + this.points[0].value + ' mln';\n" +
                                "    }");

        vertical.interactivity().hoverMode(HoverMode.BY_X);

        vertical.xAxis(true);
        vertical.xAxis(0).labels().format(" ");
        vertical.yAxis(true);
        vertical.yAxis(0).labels().format("{%Value} %");

        anyChartView2.setChart(vertical);
    }
}
