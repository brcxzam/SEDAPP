package com.brcxzam.sedapp.evaluation_ue;

import android.app.DatePickerDialog;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.TimeZone;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.apollographql.apollo.fetcher.ApolloResponseFetchers;
import com.brcxzam.sedapp.CreateAnexo_2_1Mutation;
import com.brcxzam.sedapp.DatePickerFragment;
import com.brcxzam.sedapp.MainActivity;
import com.brcxzam.sedapp.R;
import com.brcxzam.sedapp.ReadAllUEsQuery;
import com.brcxzam.sedapp.apollo_client.ApolloConnector;
import com.brcxzam.sedapp.type.IAnexo_2_1;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class QuestionsUE extends Fragment {

    private MaterialButton back;
    private TextView sectionTextView;
    private TextView unidadesEconomicasTextView;
    private Spinner unidadesEconomicasSpinner;
    private TextInputLayout periodTextInputLayout;
    private TextInputLayout dateTextInputLayout;
    private MaterialCardView questionCard;
    private TextView questionTextView;
    private RadioGroup answers;
    private RadioButton first;
    private RadioButton second;
    private RadioButton third;
    private FloatingActionButton fab;

    // Array contenedor de las respuestas a las preguntas
    private Integer[] answersArray = new Integer[13];

    // Contadores de sección y número de pregunta; -1 Datos iniciales de la evaluación
    private int section = 0;
    private int question = -1;

    // ArrayList contenedor de la razón social de las Unidades Económicas
    List<String> ues = new ArrayList<>();
    // ArrayList contenedor de los datos de Unidades Económicas
    List<ReadAllUEsQuery.UE> ueArrayList = new ArrayList<>();


    // Ubicación de la notificación
    View viewSnack;

    String fecha;

    public QuestionsUE() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_questions_ue, container, false);

        //Obtención de Unidades Económicas a travez de cache y network
        fetchUEs();

        back = view.findViewById(R.id.back);
        sectionTextView = view.findViewById(R.id.section);
        unidadesEconomicasTextView = view.findViewById(R.id.text_view_ue);
        unidadesEconomicasSpinner = view.findViewById(R.id.unidades_economicas);
        periodTextInputLayout = view.findViewById(R.id.period);
        dateTextInputLayout = view.findViewById(R.id.date);
        questionCard = view.findViewById(R.id.question_card);
        questionTextView = view.findViewById(R.id.question);
        answers = view.findViewById(R.id.answers);
        first = view.findViewById(R.id.first);
        second = view.findViewById(R.id.second);
        third = view.findViewById(R.id.third);
        viewSnack = ((MainActivity) Objects.requireNonNull(getActivity())).findViewById(R.id.viewSnack);

        final String[] questions = Objects.requireNonNull(getContext()).getResources().getStringArray(R.array.questions_ue);

        fab = ((MainActivity) Objects.requireNonNull(getActivity())).findViewById(R.id.fab);
        
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleRadioButton();
                question++;
                if (question < 13) {
                    answers.clearCheck();
                    handleQuestions(questions);
                } else {
                    createEvaluation();
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                question--;
                handleQuestions(questions);
                if (question == 11) {
                    fab.hide(new FloatingActionButton.OnVisibilityChangedListener() {
                        @Override
                        public void onHidden(FloatingActionButton fab) {
                            super.onHidden(fab);
                            fab.setImageResource(R.drawable.ic_arrow_forward_black_24dp);
                            fab.show();
                        }
                    });
                }
            }
        });

        handleQuestions(questions);

        final DialogFragment dialogFragment = new DatePickerFragment(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String[] dates = dateFormat(year, month, dayOfMonth);
                fecha = dates[0];
                dateTextInputLayout.getEditText().setText(dates[1]);
            }
        });
        dateTextInputLayout.getEditText().setEnabled(false);
        dateTextInputLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogFragment.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(),"datePicker");
            }
        });

        return view;
    }

    /**
     * Metodo para dar formato a las fechas
     * @param year
     * @param month
     * @param dayOfMonth
     * @return String Array que contiene como primer posición el formato de visualización
 *              y como segundo el de almacenamiento
     */
    public String[] dateFormat(int year, int month, int dayOfMonth) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(0);
        cal.set(year, month, dayOfMonth, 0, 0, 0);

        Date chosenDate = cal.getTime();

        DateFormat dateLong = DateFormat.getDateInstance(DateFormat.LONG, Locale.forLanguageTag("spa"));
        String dateLongString = dateLong.format(chosenDate);

        SimpleDateFormat sqlFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.forLanguageTag("spa"));
        String dateSqlFormat = sqlFormat.format(chosenDate);

        return new String[]{dateLongString,dateSqlFormat};
    }

    private void handleQuestions(String[] questions) {
        if (question == -1) {
            section = 0;
            sectionTextView.setText(R.string.ue);
        } else {
            if (answersArray[question] != null){
                int answer = answersArray[question];
                switch (answer) {
                    case 1:
                        first.toggle();
                        break;
                    case 2:
                        second.toggle();
                        break;
                    case 3:
                        third.toggle();
                        break;
                }
            }
        }

        if (question >= 0 && question < 4){
            section = 1;
        } else if (question >= 4 && question < 10){
            section = 2;
        } else if (question >= 10 && question < 13){
            section = 3;
        }

        if (question != -1 && question < 13) {
            questionTextView.setText(questions[question]);
        }

        if (question == 12) {
            fab.hide(new FloatingActionButton.OnVisibilityChangedListener() {
                @Override
                public void onHidden(FloatingActionButton fab) {
                    super.onHidden(fab);
                    fab.setImageResource(R.drawable.ic_save_black_24dp);
                    fab.show();
                }
            });
        }

        if (section == 1 && question < 4) {
            sectionTextView.setText(R.string.section1);
        } else if (section == 2 && question < 10) {
            sectionTextView.setText(R.string.section2);
        } else if (section == 3 && question < 13) {
            sectionTextView.setText(R.string.section3);
        }

        int visibilityBack = section > 0 ? View.VISIBLE : View.INVISIBLE;
        int visibilitySection0 = section == 0 ? View.VISIBLE : View.GONE;
        int visibilitySection1 = section > 0 ? View.VISIBLE : View.GONE;
        int visibilitySections2_3 = section > 1 ? View.VISIBLE : View.GONE;

        back.setVisibility(visibilityBack);

        unidadesEconomicasTextView.setVisibility(visibilitySection0);
        unidadesEconomicasSpinner.setVisibility(visibilitySection0);
        periodTextInputLayout.setVisibility(visibilitySection0);
        dateTextInputLayout.setVisibility(visibilitySection0);

        questionCard.setVisibility(visibilitySection1);
        answers.setVisibility(visibilitySection1);

        third.setVisibility(visibilitySections2_3);

        if (section <= 1) {
            first.setText(R.string.answer_1);
            second.setText(R.string.answer_2);
        } else {
            first.setText(R.string.answer_3);
            second.setText(R.string.answer_4);
            third.setText(R.string.answer_5);
        }
    }

    private void errorMessage() {
        Snackbar.make(viewSnack, R.string.error_connection, Snackbar.LENGTH_SHORT)
                .show();
    }

    private void fetchUEs() {
        ApolloConnector.setupApollo(getContext()).query(new ReadAllUEsQuery())
                .responseFetcher(ApolloResponseFetchers.CACHE_AND_NETWORK)
                .enqueue(new ApolloCall.Callback<ReadAllUEsQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull final Response<ReadAllUEsQuery.Data> response) {
                        if (response.data() !=  null) {
                            Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    ueArrayList = Objects.requireNonNull(response.data().UEs());

                                    for (ReadAllUEsQuery.UE ue: Objects.requireNonNull(response.data().UEs())) {
                                        ues.add(ue.razon_social());
                                    }

                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(Objects.requireNonNull(getContext()),
                                            android.R.layout.simple_spinner_item, ues);
                                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    unidadesEconomicasSpinner.setAdapter(adapter);
                                }
                            });
                        }
                    }
                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        errorMessage();
                    }
                });
    }

    private void createEvaluation() {
        String periodo = Objects.requireNonNull(periodTextInputLayout.getEditText()).getText().toString();
        int positionUE = unidadesEconomicasSpinner.getSelectedItemPosition();
        String uERFC = ueArrayList.get(positionUE).RFC();
        CreateAnexo_2_1Mutation anexo21Mutation = CreateAnexo_2_1Mutation.builder()
                .data(IAnexo_2_1.builder()
                        .periodo(periodo)
                        .fecha(fecha)
                        .s1_p1(answersArray[0])
                        .s1_p2(answersArray[1])
                        .s1_p3(answersArray[2])
                        .s1_p4(answersArray[3])
                        .s1_total_no(3)
                        .s1_total_si(0)
                        .s2_p1(answersArray[4])
                        .s2_p2(answersArray[5])
                        .s2_p3(answersArray[6])
                        .s2_p4(answersArray[7])
                        .s2_p5(answersArray[8])
                        .s2_p6(answersArray[9])
                        .s2_suma_no_cumple(6)
                        .s2_suma_parcialmente(0)
                        .s2_suma_cumple(0)
                        .s3_p1(answersArray[10])
                        .s3_p2(answersArray[11])
                        .s3_p3(answersArray[12])
                        .s3_suma_no_cumple(3)
                        .s3_suma_parcialmente(0)
                        .s3_suma_cumple(0)
                        .total(13)
                        .aplicador("Don Pedro")
                        .iEId("1")
                        .uERFC(uERFC)
                        .build())
                .build();
        ApolloConnector.setupApollo(getContext()).mutate(anexo21Mutation)
                .enqueue(new ApolloCall.Callback<CreateAnexo_2_1Mutation.Data>() {
                    @Override
                    public void onResponse(@NotNull final Response<CreateAnexo_2_1Mutation.Data> response) {
                        Objects.requireNonNull(getActivity()).onBackPressed();
                        Snackbar.make(viewSnack, R.string.success_save, Snackbar.LENGTH_SHORT)
                                .show();
                    }
                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        errorMessage();
                    }
                });
    }

    private void handleRadioButton() {
        if (question != -1) {
            int checkedId = answers.getCheckedRadioButtonId();
            switch (checkedId) {
                case R.id.first:
                    answersArray[question] = 1;
                    break;
                case R.id.second:
                    answersArray[question] = 2;
                    break;
                case R.id.third:
                    answersArray[question] = 3;
                    break;
            }
        }
    }
}
