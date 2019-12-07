package com.brcxzam.sedapp.evaluation_ue;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brcxzam.sedapp.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class QuestionsUE extends Fragment {


    public QuestionsUE() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_questions_ue, container, false);
    }

}
