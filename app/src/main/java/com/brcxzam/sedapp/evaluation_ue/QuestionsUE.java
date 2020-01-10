package com.brcxzam.sedapp.evaluation_ue;

import android.icu.text.DateFormat;
import android.icu.text.DecimalFormat;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.brcxzam.sedapp.CreateAnexo_2_1Mutation;
import com.brcxzam.sedapp.R;
import com.brcxzam.sedapp.ReadAllUEsQuery;
import com.brcxzam.sedapp.apollo_client.ApolloConnector;
import com.brcxzam.sedapp.apollo_client.Token;
import com.brcxzam.sedapp.database.Anexo21;
import com.brcxzam.sedapp.database.Anexo21Dao;
import com.brcxzam.sedapp.database.AppDatabase;
import com.brcxzam.sedapp.database.UEs;
import com.brcxzam.sedapp.database.UEsDao;
import com.brcxzam.sedapp.type.IAnexo_2_1;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;


public class QuestionsUE extends Fragment implements View.OnClickListener {

    // Unidades Económicas
    private UEsDao uesDao;
    private List<UEs> uesList = new ArrayList<>();
    private ArrayAdapter<UEs> adapter;

    // Anexo 2.1
    private Anexo21Dao anexo21Dao;

    // Array contenedor de las respuestas a las preguntas
    private Integer[] answersArray = new Integer[13];

    // Contador de sección
    private int section = 1;

    private String[] dates;
    private final String ERROR_CONNECTION = "ERROR CONNECTION GQL " + getClass().getSimpleName();
    private Spinner unidadesEconomicasSpinner;
    private ImageView icon;
    private TextView sectionTextView;
    private MaterialCardView cardView2, cardView3, cardView4, cardView5, cardView6;
    private RadioGroup radioGroup1, radioGroup2, radioGroup3, radioGroup4, radioGroup5, radioGroup6;
    private TextView textView1, textView2, textView3, textView4, textView5, textView6;
    private MaterialButton previous, next;
    private ScrollView scrollView;

    private int s1TotalNo = 0, s1TotalSi = 0;
    private int s2SumaNoCumple = 0, s2SumaParcialmente = 0, s2SumaCumple = 0;
    private int s3SumaNoCumple = 0, s3SumaParcialmente = 0, s3SumaCumple = 0;
    private double total = 0;

    private NavController navController;

    public QuestionsUE() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_questions_ue, container, false);
        if (getActivity() == null) return view;

        navController = Navigation.findNavController(getActivity(),R.id.nav_host_fragment);

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.evaluation_ue);

        TextView date = view.findViewById(R.id.date);
        TextView period = view.findViewById(R.id.period);
        unidadesEconomicasSpinner =  view.findViewById(R.id.unidades_economicas);
        icon = view.findViewById(R.id.icon);
        sectionTextView = view.findViewById(R.id.section);
        cardView2 = view.findViewById(R.id.card2);
        cardView3 = view.findViewById(R.id.card3);
        cardView4 = view.findViewById(R.id.card4);
        cardView5 = view.findViewById(R.id.card5);
        cardView6 = view.findViewById(R.id.card6);
        radioGroup1 = view.findViewById(R.id.answers1);
        radioGroup2 = view.findViewById(R.id.answers2);
        radioGroup3 = view.findViewById(R.id.answers3);
        radioGroup4 = view.findViewById(R.id.answers4);
        radioGroup5 = view.findViewById(R.id.answers5);
        radioGroup6 = view.findViewById(R.id.answers6);
        textView1 = view.findViewById(R.id.question1);
        textView2 = view.findViewById(R.id.question2);
        textView3 = view.findViewById(R.id.question3);
        textView4 = view.findViewById(R.id.question4);
        textView5 = view.findViewById(R.id.question5);
        textView6 = view.findViewById(R.id.question6);
        previous = view.findViewById(R.id.previous);
        next = view.findViewById(R.id.next);
        scrollView = view.findViewById(R.id.scrollView);

        dates = dateFormat();

        date.setText(dates[0]);
        period.setText(dates[2]);

        // Conexión con la base de datos
        AppDatabase database = AppDatabase.getAppDatabase(getContext());
        uesDao = database.uesDao();
        anexo21Dao = database.anexo21Dao();

        // Spinner con registros de UE
        adapter = new ArrayAdapter<>(Objects.requireNonNull(getContext()), android.R.layout.simple_spinner_item, uesList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unidadesEconomicasSpinner.setAdapter(adapter);

        // Carga de registros UE
        showUEs(); fetchUEs();

        previous.setOnClickListener(this);
        next.setOnClickListener(this);

        if (getArguments() != null) {
            toolbar.setTitle(getArguments().getString("razon_social"));
            period.setText(getArguments().getString("periodo"));
            date.setText(stringLongDate(getArguments().getString("fecha")));
            int find = findUE(getArguments().getString("UERFC"));
            if (find != -1) {
                unidadesEconomicasSpinner.setSelection(find);
            }
            answersArray[0] = getArguments().getInt("s1_p1");
            answersArray[1] = getArguments().getInt("s1_p2");
            answersArray[2] = getArguments().getInt("s1_p3");
            answersArray[3] = getArguments().getInt("s1_p4");
            answersArray[4] = getArguments().getInt("s2_p5");
            answersArray[5] = getArguments().getInt("s2_p6");
            answersArray[6] = getArguments().getInt("s2_p7");
            answersArray[7] = getArguments().getInt("s2_p8");
            answersArray[8] = getArguments().getInt("s2_p9");
            answersArray[9] = getArguments().getInt("s2_p10");
            answersArray[10] = getArguments().getInt("s3_p11");
            answersArray[11] = getArguments().getInt("s3_p12");
            answersArray[12] = getArguments().getInt("s3_p13");
            disableRadioGroup(radioGroup1);
            disableRadioGroup(radioGroup2);
            disableRadioGroup(radioGroup3);
            disableRadioGroup(radioGroup4);
            disableRadioGroup(radioGroup5);
            disableRadioGroup(radioGroup6);
        }

        viewVisibility();

        return view;
    }

    private int findUE(String UERFC) {
        for (UEs ue: uesList) {
            if (ue.getRFC().equals(UERFC)) {
                return uesList.indexOf(ue);
            }
        }
        return -1;
    }

    private String stringLongDate(String dateString) {
        SimpleDateFormat sqlFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.forLanguageTag("spa"));
        String dateLongFormat = null;
        try {
            Date date1 = sqlFormat.parse(dateString);
            DateFormat dateLong = DateFormat.getDateInstance(DateFormat.LONG, Locale.forLanguageTag("spa"));
            dateLongFormat = dateLong.format(date1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateLongFormat;
    }

    private void disableRadioGroup(RadioGroup group) {
        for(int i = 0; i < group.getChildCount(); i++){
            group.getChildAt(i).setEnabled(false);
        }
    }

    private void section1() {
        icon.setImageDrawable(getActivity().getDrawable(R.drawable.ic_content_paste_black_24dp));
        sectionTextView.setText(R.string.section1);
        textView1.setText(R.string.question1);
        textView2.setText(R.string.question2);
        textView3.setText(R.string.question3);
        textView4.setText(R.string.question4);
        if (answersArray[0] != null) {
            checkedSave(radioGroup1, answersArray[0]);
        }
        if (answersArray[1] != null) {
            checkedSave(radioGroup2, answersArray[1]);
        }
        if (answersArray[2] != null) {
            checkedSave(radioGroup3, answersArray[2]);
        }
        if (answersArray[3] != null) {
            checkedSave(radioGroup4, answersArray[3]);
        }
    }

    private void section2() {
        icon.setImageDrawable(getActivity().getDrawable(R.drawable.ic_school_black_24dp));
        sectionTextView.setText(R.string.section2);
        textView1.setText(R.string.question5);
        textView2.setText(R.string.question6);
        textView3.setText(R.string.question7);
        textView4.setText(R.string.question8);
        textView5.setText(R.string.question9);
        textView6.setText(R.string.question10);
        if (answersArray[4] != null) {
            checkedSave(radioGroup1, answersArray[4]);
        }
        if (answersArray[5] != null) {
            checkedSave(radioGroup2, answersArray[5]);
        }
        if (answersArray[6] != null) {
            checkedSave(radioGroup3, answersArray[6]);
        }
        if (answersArray[7] != null) {
            checkedSave(radioGroup4, answersArray[7]);
        }
        if (answersArray[8] != null) {
            checkedSave(radioGroup5, answersArray[8]);
        }
        if (answersArray[9] != null) {
            checkedSave(radioGroup6, answersArray[9]);
        }
    }

    private void section3() {
        icon.setImageDrawable(getActivity().getDrawable(R.drawable.ic_attach_money_black_24dp));
        sectionTextView.setText(R.string.section3);
        textView1.setText(R.string.question11);
        textView2.setText(R.string.question12);
        textView3.setText(R.string.question13);
        if (answersArray[10] != null) {
            checkedSave(radioGroup1, answersArray[10]);
        }
        if (answersArray[11] != null) {
            checkedSave(radioGroup2, answersArray[11]);
        }
        if (answersArray[12] != null) {
            checkedSave(radioGroup3, answersArray[12]);
        }
    }

    private void section4() {
        icon.setImageDrawable(getActivity().getDrawable(R.drawable.ic_playlist_add_check_black_24dp));
        sectionTextView.setText(R.string.section4);
        String[] totals = getActivity().getResources().getStringArray(R.array.totals);
        percentTotal();
        DecimalFormat format = new DecimalFormat("#.00");
        String totalString1 = format.format(total)+"% "+totals[0];
        String totalString2 = format.format(total)+"% "+totals[1];
        String totalString3 = format.format(total)+"% "+totals[2];
        if (total <= 45) {
            textView1.setText(totalString1);
        } else if (total <= 66) {
            textView1.setText(totalString2);
        } else if (total <= 100) {
            textView1.setText(totalString3);
        }
        if (getArguments() != null) {
            next.setText(R.string.close);
        } else {
            next.setText(R.string.save);
        }
    }

    private void checkedSave(RadioGroup group, int value) {
        if (value == 1) {
            group.check(group.getChildAt(0).getId());
        } else if (value == 2 && section != 1) {
            group.check(group.getChildAt(1).getId());
        } else if (value == 3 || value == 2) {
            group.check(group.getChildAt(2).getId());
        }
    }

    private void viewVisibility() {
        int section3 = section != 4 ? View.VISIBLE : View.GONE;
        int section1 = section <= 2 ? View.VISIBLE : View.GONE;
        int section2 = section == 2 ? View.VISIBLE : View.GONE;
        int section4 = section >= 2 ? View.VISIBLE : View.INVISIBLE;
        cardView2.setVisibility(section3);
        cardView3.setVisibility(section3);
        cardView4.setVisibility(section1);
        cardView5.setVisibility(section2);
        cardView6.setVisibility(section2);
        radioGroup1.setVisibility(section3);
        radioGroup2.setVisibility(section3);
        radioGroup3.setVisibility(section3);
        radioGroup4.setVisibility(section1);
        radioGroup5.setVisibility(section2);
        radioGroup6.setVisibility(section2);
        previous.setVisibility(section4);
        textRadioButton(radioGroup1);
        textRadioButton(radioGroup2);
        textRadioButton(radioGroup3);
        textRadioButton(radioGroup4);
        radioGroup1.clearCheck();
        radioGroup2.clearCheck();
        radioGroup3.clearCheck();
        radioGroup4.clearCheck();
        radioGroup5.clearCheck();
        radioGroup6.clearCheck();
        next.setText(R.string.next);
        unidadesEconomicasSpinner.setEnabled(section == 1);
        sections();
    }

    private void textRadioButton(RadioGroup group) {
        RadioButton radioButton1 = (RadioButton) group.getChildAt(0);
        RadioButton radioButton2 = (RadioButton) group.getChildAt(1);
        RadioButton radioButton3 = (RadioButton) group.getChildAt(2);
        if (section == 1) {
            radioButton1.setText(R.string.answer_1);
            radioButton2.setVisibility(View.GONE);
            radioButton3.setText(R.string.answer_2);
        } else {
            radioButton1.setText(R.string.answer_3);
            radioButton2.setVisibility(View.VISIBLE);
            radioButton3.setText(R.string.answer_5);
        }
    }

    private void sections() {
        switch (section) {
            case 1:
                section1();
                break;
            case 2:
                section2();
                break;
            case 3:
                section3();
                break;
            case 4:
                section4();
                break;
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.previous:
                if (section > 1) {
                    section--;
                    viewVisibility();
                }
                break;
            case R.id.next:
                if (section < 4) {
                    if (beforeNext()) {
                        section++;
                        viewVisibility();
                    } else {
                        Snackbar.make(next, R.string.anwersEmpty, Snackbar.LENGTH_SHORT)
                                .show();
                    }
                } else {
                    if (getArguments() != null) {
                        navController.navigate(R.id.action_global_evaluations);
                    } else {
                        createEvaluation();
                    }
                }
                break;
        }
        scrollView.scrollTo(0,unidadesEconomicasSpinner.getBottom());
    }

    private boolean beforeNext() {
        int position;
        if (section == 1) {
            position = 0;
        } else if (section == 2) {
            position = 4;
        } else if (section == 3) {
            position = 10;
        } else {
            return true;
        }
        boolean answer1 = handleRadioButton(radioGroup1,position++);
        boolean answer2 = handleRadioButton(radioGroup2,position++);
        boolean answer3 = handleRadioButton(radioGroup3,position++);
        boolean answer4 = false;
        boolean answer5 = false;
        boolean answer6 = false;
        if (section <= 2) {
            answer4 = handleRadioButton(radioGroup4,position++);
        }
        if (section == 2) {
            answer5 = handleRadioButton(radioGroup5,position++);
            answer6 = handleRadioButton(radioGroup6,position);
        }
        switch (section) {
            case 1:
                return answer1 && answer2 && answer3 && answer4;
            case 2:
                return answer1 && answer2 && answer3 && answer4 && answer5 && answer6;
            case 3:
                return answer1 && answer2 && answer3;
        }
        return true;
    }

    private boolean handleRadioButton(RadioGroup group, int position) {
        int checkedId = group.getCheckedRadioButtonId();
        int childId1 = group.getChildAt(0).getId();
        int childId2 = group.getChildAt(1).getId();
        int childId3 = group.getChildAt(2).getId();
        if (checkedId == childId1) {
            answersArray[position] = 1;
        } else if (checkedId == childId2) {
            answersArray[position] = 2;
        } else if (checkedId == childId3) {
            answersArray[position] = section == 1 ? 2 : 3;
        } else {
            return false;
        }
        return true;
    }

    // Formato de fecha
    private String[] dateFormat() {
        Date date = new Date();

        DateFormat dateLong = DateFormat.getDateInstance(DateFormat.LONG, Locale.forLanguageTag("spa"));
        String dateLongFormat = dateLong.format(date);

        SimpleDateFormat sqlFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.forLanguageTag("spa"));
        String dateSqlFormat = sqlFormat.format(date);

        SimpleDateFormat monthFormat = new SimpleDateFormat("MM", Locale.forLanguageTag("spa"));
        int dateMonthFormat = Integer.valueOf(monthFormat.format(date));

        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.forLanguageTag("spa"));
        String dateYearFormat = yearFormat.format(date);

        if (dateMonthFormat >= 3 && dateMonthFormat <= 8) {
            dateYearFormat = dateYearFormat + "-1";
        } else {
            dateYearFormat = dateYearFormat + "-2";
        }

        return new String[]{dateLongFormat,dateSqlFormat, dateYearFormat};
    }

    // Actualización de la lista con las UEs
    private void showUEs() {
        uesList.clear();
        uesList.addAll(uesDao.readAll());
        adapter.notifyDataSetChanged();
    }

    // Sincronización de las UEs con el servidor
    private void fetchUEs() {
        ApolloConnector
                .setupApollo(getContext())
                .query(ReadAllUEsQuery.builder().build())
                .enqueue(new ApolloCall.Callback<ReadAllUEsQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull final Response<ReadAllUEsQuery.Data> response) {
                        if (response.data() !=  null) {
                            List<UEs> uesList = new ArrayList<>();

                            for (ReadAllUEsQuery.UE ue:
                                    Objects.requireNonNull(response.data().UEs())) {
                                uesList.add(new UEs(ue.RFC(),ue.razon_social(), ue.domicilio()));
                            }

                            uesDao.insertAll(uesList);
                            Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showUEs();
                                }
                            });
                        }
                    }
                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        Log.d(ERROR_CONNECTION, e.getMessage());
                    }
                });
    }

    private void percentTotal() {
        s1TotalNo = 0; s1TotalSi = 0;
        s2SumaNoCumple = 0; s2SumaParcialmente = 0; s2SumaCumple = 0;
        s3SumaNoCumple = 0; s3SumaParcialmente = 0; s3SumaCumple = 0;
        for (int i = 0; i < answersArray.length; i++) {
            if (i < 4) {
                if (answersArray[i] == 1) {
                    s1TotalNo++;
                } else {
                    s1TotalSi++;
                }
            } else if (i < 10){
                if (answersArray[i] == 1) {
                    s2SumaNoCumple++;
                } else if (answersArray[i] == 2){
                    s2SumaParcialmente++;
                } else {
                    s2SumaCumple++;
                }
            } else if (i < 13){
                if (answersArray[i] == 1) {
                    s3SumaNoCumple++;
                } else if (answersArray[i] == 2){
                    s3SumaParcialmente++;
                } else {
                    s3SumaCumple++;
                }
            }
        }
        double section1 = ( (double) ( s1TotalNo + ( s1TotalSi * 2 ) ) / 8 ) * 100;
        double section2 = ( (double) ( s2SumaNoCumple + ( s2SumaParcialmente * 2 ) + ( s2SumaCumple * 3 ) ) / 18 ) * 100;
        double section3 = ( (double) ( s3SumaNoCumple + ( s3SumaParcialmente * 2 ) + ( s3SumaCumple * 3 ) ) / 9) * 100;
        total = ( section1 + section2 + section3) / 3;
    }

    private void createEvaluationLocal(String periodo, String UERFC, String aplicador, String razon_social) {
        Anexo21 res = new Anexo21();
        res.setId(UUID.randomUUID().toString());
        res.setPeriodo(periodo);
        res.setFecha(dates[1]);
        res.setS1_p1(answersArray[0]);
        res.setS1_p2(answersArray[1]);
        res.setS1_p3(answersArray[2]);
        res.setS1_p4(answersArray[3]);
        res.setS1_total_no(s1TotalNo);
        res.setS1_total_si(s1TotalSi);
        res.setS2_p1(answersArray[4]);
        res.setS2_p2(answersArray[5]);
        res.setS2_p3(answersArray[6]);
        res.setS2_p4(answersArray[7]);
        res.setS2_p5(answersArray[8]);
        res.setS2_p6(answersArray[9]);
        res.setS2_suma_no_cumple(s2SumaNoCumple);
        res.setS2_suma_parcialmente(s2SumaParcialmente);
        res.setS2_suma_cumple(s2SumaCumple);
        res.setS3_p1(answersArray[10]);
        res.setS3_p2(answersArray[11]);
        res.setS3_p3(answersArray[12]);
        res.setS3_suma_no_cumple(s3SumaNoCumple);
        res.setS3_suma_parcialmente(s3SumaParcialmente);
        res.setS3_suma_cumple(s3SumaCumple);
        res.setTotal(total);
        res.setAplicador(aplicador);
        res.setIEId("1");
        res.setInstitucion_educativa("");
        res.setUERFC(UERFC);
        res.setRazon_social(razon_social);
        res.setAccion("CREATE");
        anexo21Dao.create(res);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                navController.navigate(R.id.action_global_evaluations);
            }
        });
    }

    private void createEvaluation() {
        final String periodo = dates[2];
        int positionUE = unidadesEconomicasSpinner.getSelectedItemPosition();
        final String uERFC = uesList.get(positionUE).getRFC();
        final String razon_social = uesList.get(positionUE).getRazon_social();
        final String aplicador = new Token(Objects.requireNonNull(getContext())).getNombre();
        CreateAnexo_2_1Mutation anexo21Mutation = CreateAnexo_2_1Mutation.builder()
                .data(IAnexo_2_1.builder()
                        .periodo(periodo)
                        .fecha(dates[1])
                        .s1_p1(answersArray[0])
                        .s1_p2(answersArray[1])
                        .s1_p3(answersArray[2])
                        .s1_p4(answersArray[3])
                        .s1_total_no(s1TotalNo)
                        .s1_total_si(s1TotalSi)
                        .s2_p1(answersArray[4])
                        .s2_p2(answersArray[5])
                        .s2_p3(answersArray[6])
                        .s2_p4(answersArray[7])
                        .s2_p5(answersArray[8])
                        .s2_p6(answersArray[9])
                        .s2_suma_no_cumple(s2SumaNoCumple)
                        .s2_suma_parcialmente(s2SumaParcialmente)
                        .s2_suma_cumple(s2SumaCumple)
                        .s3_p1(answersArray[10])
                        .s3_p2(answersArray[11])
                        .s3_p3(answersArray[12])
                        .s3_suma_no_cumple(s3SumaNoCumple)
                        .s3_suma_parcialmente(s3SumaParcialmente)
                        .s3_suma_cumple(s3SumaCumple)
                        .total(total)
                        .aplicador(aplicador)
                        .iEId("1")
                        .uERFC(uERFC)
                        .build())
                .build();
        ApolloConnector.setupApollo(getContext()).mutate(anexo21Mutation)
                .enqueue(new ApolloCall.Callback<CreateAnexo_2_1Mutation.Data>() {
                    @Override
                    public void onResponse(@NotNull final Response<CreateAnexo_2_1Mutation.Data> response) {

                        if (response.data() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    navController.navigate(R.id.action_global_evaluations);
                                }
                            });
                            Snackbar.make(next, R.string.success_save, Snackbar.LENGTH_SHORT)
                                    .show();
                        } else {
                            Snackbar.make(next, R.string.error_save, Snackbar.LENGTH_SHORT)
                                    .show();
                        }
                    }
                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        Log.d(ERROR_CONNECTION,e.getMessage());
                        createEvaluationLocal(periodo,uERFC,aplicador,razon_social);
                    }
                });
    }
}
