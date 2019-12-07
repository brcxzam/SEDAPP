package com.brcxzam.sedapp.evaluation_ue;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.brcxzam.sedapp.MainActivity;
import com.brcxzam.sedapp.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import java.lang.reflect.Array;


/**
 * A simple {@link Fragment} subclass.
 */
public class QuestionsUE extends Fragment {

    MaterialButton back;
    TextView sectionTextView;
    TextView unidadesEconomicasTextView;
    Spinner unidadesEconomicasSpinner;
    TextInputLayout periodTextInputLayout;
    TextInputLayout dateTextInputLayout;
    MaterialCardView questionCard;
    TextView questionTextView;
    RadioGroup answers;
    RadioButton first;
    RadioButton second;
    RadioButton third;
    FloatingActionButton fab;

    int section = 0;
    int question = -1;


    public QuestionsUE() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_questions_ue, container, false);
        Spinner spinner = (Spinner) view.findViewById(R.id.unidades_economicas);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.planets_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

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

        final String[] questions = getContext().getResources().getStringArray(R.array.questions_ue);

        fab = ((MainActivity) getActivity()).findViewById(R.id.fab);
        
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                question++;
                if (question < 13) {
                    lol(questions);
                } else {
                    getActivity().onBackPressed();
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                question--;
                lol(questions);
            }
        });



        lol(questions);

        return view;
    }

    private void lol(String[] questions) {
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

    public int toggleVisibility(int visibility) {
        return visibility ^ 1;
    }

}
