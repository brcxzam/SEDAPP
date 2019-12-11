package com.brcxzam.sedapp.evaluation_ed;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.brcxzam.sedapp.MainActivity;
import com.brcxzam.sedapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class EvaluationED extends Fragment {


    public EvaluationED() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        FloatingActionButton fab = ((MainActivity) getActivity()).findViewById(R.id.fab);
        if (!fab.isShown()) {
            fab.show();
        }
        return inflater.inflate(R.layout.fragment_evaluation_ue, container, false);
    }

}
