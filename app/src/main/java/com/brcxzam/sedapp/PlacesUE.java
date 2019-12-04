package com.brcxzam.sedapp;


import android.os.Bundle;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlacesUE extends Fragment {


    public PlacesUE() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FloatingActionButton fab = ((MainActivity) getActivity()).findViewById(R.id.fab);
        if (fab.isShown()) {
            fab.hide();
        }
        return inflater.inflate(R.layout.fragment_places_ue, container, false);
    }

}
