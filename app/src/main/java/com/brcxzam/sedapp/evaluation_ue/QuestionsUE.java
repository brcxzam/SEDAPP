package com.brcxzam.sedapp.evaluation_ue;

import android.app.DatePickerDialog;
import android.content.Context;
import android.icu.text.DateFormat;
import android.icu.text.DecimalFormat;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.apollographql.apollo.fetcher.ApolloResponseFetchers;
import com.brcxzam.sedapp.CreateAnexo_2_1Mutation;
import com.brcxzam.sedapp.DatePickerFragment;
import com.brcxzam.sedapp.R;
import com.brcxzam.sedapp.ReadAllAnexo_2_1Query;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;


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
    private RelativeLayout totalLayout;
    private TextView totalTextView;

    // Unidades Económicas
    UEsDao uesDao;
    List<UEs> uesList = new ArrayList<>();
    ArrayAdapter<UEs> adapter;

    // Anexo 2.1
    Anexo21Dao anexo21Dao;

    // Array contenedor de las respuestas a las preguntas
    private Integer[] answersArray = new Integer[13];

    // Contadores de sección y número de pregunta; -1 Datos iniciales de la evaluación
    private int section = 0;
    private int question = -1;

    // Ubicación de la notificación
    private View viewSnack;

    private String fecha;

    String[] totals;

    private TextView date, period;
    private String[] dates;
    private final String ERROR_CONNECTION = "ERROR CONNECTION GQL " + getClass().getSimpleName();

    public QuestionsUE() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_questions_ue, container, false);
        if (getActivity() == null) return view;

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.evaluation_ue);

        date = view.findViewById(R.id.date);
        period = view.findViewById(R.id.period);

        dates = dateFormat();

        date.setText(dates[0]);
        period.setText(dates[2]);

        // Conexión con la base de datos
        AppDatabase database = AppDatabase.getAppDatabase(getContext());
        uesDao = database.uesDao();
        anexo21Dao = database.anexo21Dao();

        // Spinner con de UE
        unidadesEconomicasSpinner =  view.findViewById(R.id.unidades_economicas);
        adapter = new ArrayAdapter<>(Objects.requireNonNull(getContext()), android.R.layout.simple_spinner_item, uesList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unidadesEconomicasSpinner.setAdapter(adapter);

        // Carga de registros UE
        showUEs(); fetchUEs();


//        back = view.findViewById(R.id.back);
//        sectionTextView = view.findViewById(R.id.section);
//        unidadesEconomicasTextView = view.findViewById(R.id.text_view_ue);
//        unidadesEconomicasSpinner = view.findViewById(R.id.unidades_economicas);
//        periodTextInputLayout = view.findViewById(R.id.period);
//        dateTextInputLayout = view.findViewById(R.id.date);
//        questionCard = view.findViewById(R.id.question_card);
//        questionTextView = view.findViewById(R.id.question);
//        answers = view.findViewById(R.id.answers);
//        first = view.findViewById(R.id.first);
//        second = view.findViewById(R.id.second);
//        third = view.findViewById(R.id.third);
//        totalLayout = view.findViewById(R.id.totalLayout);
//        totalTextView = view.findViewById(R.id.total);
//
//        viewSnack = Objects.requireNonNull(getActivity()).findViewById(R.id.viewSnack);
//        fab = Objects.requireNonNull(getActivity()).findViewById(R.id.fab);
//

//

//

//
//        final String[] questions = Objects.requireNonNull(getContext()).getResources().getStringArray(R.array.questions_ue);
//        totals = Objects.requireNonNull(getContext()).getResources().getStringArray(R.array.totals);
//
//        fab.hide(new FloatingActionButton.OnVisibilityChangedListener() {
//            @Override
//            public void onHidden(FloatingActionButton fab) {
//                super.onHidden(fab);
//                fab.setImageResource(R.drawable.ic_arrow_forward_black_24dp);
//                fab.show();
//            }
//        });
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (question == -1) {
//                    CharSequence period = Objects.requireNonNull(periodTextInputLayout.getEditText()).getText();
//                    CharSequence date = Objects.requireNonNull(dateTextInputLayout.getEditText()).getText();
//                    boolean perioV = isValidPeriod(period);
//                    boolean dateV = isValidDate(date);
//                    if (!perioV) {
//                        periodTextInputLayout.setError("Periodo invalido");
//                    }
//                    if (!dateV) {
//                        dateTextInputLayout.setError("Ingresa una fecha");
//                    }
//                    dateTextInputLayout.setErrorIconDrawable(null);
//                    if (perioV && dateV) {
//                        question++;
//                    }
//                } else {
//                    boolean answer = handleRadioButton();
//                    if (answer || question == 13) {
//                        question++;
//                    }
//                }
//                if (question < 14) {
//                    answers.clearCheck();
//                    handleQuestions(questions);
//                } else {
//                    createEvaluation();
//                }
//            }
//        });
//        back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                question--;
//                handleQuestions(questions);
//            }
//        });
//
//        handleQuestions(questions);
//
//        final DialogFragment dialogFragment = new DatePickerFragment(new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                String[] dates = dateFormat(year, month, dayOfMonth);
//                fecha = dates[1];
//                Objects.requireNonNull(dateTextInputLayout.getEditText()).setText(dates[0]);
//            }
//        });
//        Objects.requireNonNull(dateTextInputLayout.getEditText()).setEnabled(false);
//        dateTextInputLayout.setEndIconOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                closeKeyboard();
//                dateTextInputLayout.setError(null);
//                dialogFragment.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(),"datePicker");
//            }
//        });
//
//        clearError(periodTextInputLayout);
//
//

        return view;
    }

    private boolean isValidPeriod(CharSequence target) {
        Pattern pattern = Pattern.compile("^[0-9-]+$");
        return !TextUtils.isEmpty(target) && pattern.matcher(target).matches() && target.length() <= 6;
    }

    private boolean isValidDate(CharSequence target) {
        return !TextUtils.isEmpty(target);
    }

    private void clearError(final TextInputLayout textInputLayout) {
        Objects.requireNonNull(textInputLayout.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private boolean handleRadioButton() {
        int checkedId = answers.getCheckedRadioButtonId();
        switch (checkedId) {
//            case R.id.first:
//                answersArray[question] = 1;
//                return true;
//            case R.id.second:
//                answersArray[question] = 2;
//                return true;
//            case R.id.third:
//                answersArray[question] = 3;
//                return true;
            default:
                return false;
        }
    }

    private void closeKeyboard() {
        View view = Objects.requireNonNull(getActivity()).getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            Objects.requireNonNull(imm).hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
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

    private void handleQuestions(String[] questions) {
        if (question == -1) {
            section = 0;
            sectionTextView.setText(R.string.ue);
        } else if (question < 13) {
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
            totalLayout.setVisibility(View.GONE);
            fab.hide(new FloatingActionButton.OnVisibilityChangedListener() {
                @Override
                public void onHidden(FloatingActionButton fab) {
                    super.onHidden(fab);
                    fab.setImageResource(R.drawable.ic_arrow_forward_black_24dp);
                    fab.show();
                }
            });
        } else if (question == 13) {
            percentTotal();
            DecimalFormat format = new DecimalFormat("#.00");
            if (total <= 45) {
                totalTextView.setText(format.format(total)+"% "+totals[0]);
            } else if (total <= 66) {
                totalTextView.setText(format.format(total)+"% "+totals[1]);
            } else if (total <= 100) {
                totalTextView.setText(format.format(total)+"% "+totals[2]);
            }
            totalLayout.setVisibility(View.VISIBLE);
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
        } else {
            sectionTextView.setText("Resultado");
        }

        int visibilityBack = section > 0 ? View.VISIBLE : View.INVISIBLE;
        int visibilitySection0 = section == 0 && question < 13 ? View.VISIBLE : View.GONE;
        int visibilitySection1 = section > 0 && question < 13 ? View.VISIBLE : View.GONE;
        int visibilitySections2_3 = section > 1 && question < 13 ? View.VISIBLE : View.GONE;

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
            first.setBackgroundResource(R.drawable.radio_states_red);
            second.setBackgroundResource(R.drawable.radio_states_green);
        } else {
            first.setText(R.string.answer_3);
            second.setText(R.string.answer_4);
            third.setText(R.string.answer_5);
            first.setBackgroundResource(R.drawable.radio_states_red);
            second.setBackgroundResource(R.drawable.radio_states_yellow);
            third.setBackgroundResource(R.drawable.radio_states_green);
        }
    }

    // Mensaje de error de conexión con el servidor
    private void errorMessage() {
        Snackbar.make(viewSnack, R.string.error_connection, Snackbar.LENGTH_SHORT)
                .show();
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
                                uesList.add(new UEs(ue.RFC(),ue.razon_social()));
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

    int s1TotalNo = 0, s1TotalSi = 0;
    int s2SumaNoCumple = 0, s2SumaParcialmente = 0, s2SumaCumple = 0;
    int s3SumaNoCumple = 0, s3SumaParcialmente = 0, s3SumaCumple = 0;
    double total = 0;
    public void percentTotal() {
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

    private void createEvaluationLocal(String periodo, String UERFC, String aplicador) {
        Anexo21 res = new Anexo21();
        res.setId(UUID.randomUUID().toString());
        res.setPeriodo(periodo);
        res.setFecha(fecha);
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
        res.setRazon_social("");
        res.setAccion("CREATE");
        anexo21Dao.create(res);
        Objects.requireNonNull(getActivity()).onBackPressed();
    }

    private void createEvaluation() {
        final String periodo = Objects.requireNonNull(periodTextInputLayout.getEditText()).getText().toString();
        int positionUE = unidadesEconomicasSpinner.getSelectedItemPosition();
        final String uERFC = uesList.get(positionUE).getRFC();
        final String aplicador = new Token(Objects.requireNonNull(getContext())).getNombre();
        CreateAnexo_2_1Mutation anexo21Mutation = CreateAnexo_2_1Mutation.builder()
                .data(IAnexo_2_1.builder()
                        .periodo(periodo)
                        .fecha(fecha)
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
                            Objects.requireNonNull(getActivity()).onBackPressed();
                            Snackbar.make(viewSnack, R.string.success_save, Snackbar.LENGTH_SHORT)
                                    .show();
                        } else {
                            Snackbar.make(viewSnack, R.string.error_save, Snackbar.LENGTH_SHORT)
                                    .show();
                        }
                    }
                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        errorMessage();
                        createEvaluationLocal(periodo,uERFC,aplicador);
                    }
                });
    }
}
