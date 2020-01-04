package com.brcxzam.sedapp.charts;


import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.brcxzam.sedapp.MainActivity;
import com.brcxzam.sedapp.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class Charts extends Fragment implements SeekBar.OnSeekBarChangeListener {

    private PieChart chart;
    private BarChart chartBar;
    private SeekBar seekBarX, seekBarY;
    private TextView tvX, tvY;

    public Charts() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        FloatingActionButton fab = ((MainActivity) getActivity()).findViewById(R.id.fab);
//        if (fab.isShown()) {
//            fab.hide();
//        }

        View view = inflater.inflate(R.layout.fragment_charts, container, false);

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.charts);

        chart = view.findViewById(R.id.pieChart);

        chartBar = view.findViewById(R.id.chart1);
        tvX = view.findViewById(R.id.tvXMax);
        tvY = view.findViewById(R.id.tvYMax);
        seekBarX = view.findViewById(R.id.seekBar1);
        seekBarX.setOnSeekBarChangeListener(this);
        seekBarY = view.findViewById(R.id.seekBar2);
        seekBarY.setOnSeekBarChangeListener(this);

        pie();

        bar();

        return view;

    }

    public void pie(){
        chart.setCenterText(generateCenterSpannableText());

        chart.setUsePercentValues(true);
        chart.getDescription().setEnabled(false);

        chart.setTransparentCircleRadius(61f);
        chart.setHoleColor(Color.WHITE);
        chart.setHoleRadius(58f);

        chart.animateY(1400, Easing.EasingOption.EaseInOutQuad);

        chart.setDrawCenterText(true);

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        /*creamos una lista para los valores Y*/
        ArrayList<PieEntry> values = new ArrayList<PieEntry>();
        values.add(new PieEntry(5* 100 / 25,"No viable"));
        values.add(new PieEntry(10 * 100 / 25,"Con opción"));
        values.add(new PieEntry(5 * 100 / 25,"Apta"));

        PieDataSet dataSet = new PieDataSet(values, "Categorías");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        /*creamos una lista de colores*/
        //ArrayList<Integer> colors = new ArrayList<Integer>();
        //colors.add(getResources().getColor(android.R.color.holo_red_light));
        //colors.add(getResources().getColor(android.R.color.holo_orange_light));
        //colors.add(getResources().getColor(android.R.color.holo_green_light));
        //dataSet.setColors(colors);

        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        chart.setData(data);

        // chart.setMaxAngle(180f); // HALF CHART
        // chart.setRotationAngle(180f);
        // chart.setCenterTextOffset(0, -20);

        chart.setDrawHoleEnabled(true);
        chart.setTransparentCircleColor(Color.WHITE);
        chart.setTransparentCircleAlpha(110);
        chart.setHighlightPerTapEnabled(true);

        //chart.invalidate();
    }

    public void bar(){

        chartBar.setDrawValueAboveBar(true);

        chartBar.getDescription().setEnabled(false);

        // if more than 25 entries are displayed in the chart, no values will be
        // drawn
        chartBar.setMaxVisibleValueCount(26);

        // scaling can now only be done on x- and y-axis separately
        chartBar.setPinchZoom(false);

        chartBar.setDrawBarShadow(false);
        chartBar.setDrawGridBackground(false);

        XAxis xAxis = chartBar.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(Color.parseColor("#1B6143"));
        xAxis.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

        Legend l = chartBar.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);

        chartBar.getAxisLeft().setDrawGridLines(false);

        // setting data
        seekBarX.setProgress(5);
        seekBarY.setProgress(100);

        // add a nice and smooth animation
        chartBar.animateXY(1500, 1500);

        //chart.getLegend().setEnabled(false);
    }

    private SpannableString generateCenterSpannableText() {

        SpannableString s = new SpannableString("Consideración\nde las UE");
        s.setSpan(new RelativeSizeSpan(1.7f), 0, 13, 0);
        s.setSpan(new StyleSpan(Typeface.NORMAL), 1, s.length() - 7, 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), 14, s.length() - 2, 0);
        s.setSpan(new RelativeSizeSpan(.8f), 14, s.length() - 2, 0);
        s.setSpan(new StyleSpan(Typeface.ITALIC), s.length() - 2, s.length(), 0);
        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length() - 2, s.length(), 0);
        return s;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        tvX.setText(String.valueOf(seekBarX.getProgress()));
        tvY.setText(String.valueOf(seekBarY.getProgress()));

        ArrayList<BarEntry> values = new ArrayList<>();

        for (int i = 0; i < seekBarX.getProgress(); i++) {
            float multi = (seekBarY.getProgress() + 1);
            float val = (float) (Math.random() * multi) + multi / 100;
            values.add(new BarEntry(i, val));
        }

        BarDataSet set1;

        if (chartBar.getData() != null &&
                chartBar.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) chartBar.getData().getDataSetByIndex(0);
            set1.setValues(values);
            chartBar.getData().notifyDataChanged();
            chartBar.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(values, "Viabilidad de las UE");
            set1.setColors(ColorTemplate.VORDIPLOM_COLORS);

            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            data.setBarWidth(0.9f); // set custom bar width
            chartBar.setData(data);
            chartBar.setFitBars(true);
        }

        chartBar.invalidate();

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
