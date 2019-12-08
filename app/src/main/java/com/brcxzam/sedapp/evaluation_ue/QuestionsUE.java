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
import com.brcxzam.sedapp.DatePickerFragment;
import com.brcxzam.sedapp.MainActivity;
import com.brcxzam.sedapp.R;
import com.brcxzam.sedapp.ReadAllUEsQuery;
import com.brcxzam.sedapp.apollo_client.ApolloConnector;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
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

    private int section = 0;
    private int question = -1;


    public QuestionsUE() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_questions_ue, container, false);

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

        final String[] questions = Objects.requireNonNull(getContext()).getResources().getStringArray(R.array.questions_ue);

        fab = ((MainActivity) Objects.requireNonNull(getActivity())).findViewById(R.id.fab);
        
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                question++;
                if (question < 13) {
                    handleQuestions(questions);
                } else {
                    Objects.requireNonNull(getActivity()).onBackPressed();
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                question--;
                handleQuestions(questions);
            }
        });



        handleQuestions(questions);
        final DialogFragment dialogFragment = new DatePickerFragment(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String[] dates = dateFormat(year, month, dayOfMonth);

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

    private void fetchUEs() {
        ApolloConnector.setupApollo(getContext()).query(new ReadAllUEsQuery())
                .responseFetcher(ApolloResponseFetchers.NETWORK_FIRST)
                .enqueue(new ApolloCall.Callback<ReadAllUEsQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull final Response<ReadAllUEsQuery.Data> response) {

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), "looks good"+response.data().UEs(), Toast.LENGTH_SHORT).show();
                                // Create an ArrayAdapter using the string array and a default spinner layout
                                List<String> ues = new ArrayList<>();
                                for (ReadAllUEsQuery.UE ue: Objects.requireNonNull(response.data().UEs())) {
                                    ues.add(ue.razon_social());
                                }
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item, ues);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                // Specify the layout to use when the list of choices appears
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                // Apply the adapter to the spinner
                                unidadesEconomicasSpinner.setAdapter(adapter);
                            }
                        });
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        Looper.prepare();
                        Toast.makeText(getContext(), "looks bad", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                });
    }

}
