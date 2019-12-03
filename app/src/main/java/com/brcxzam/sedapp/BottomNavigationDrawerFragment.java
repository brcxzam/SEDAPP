package com.brcxzam.sedapp;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class BottomNavigationDrawerFragment extends BottomSheetDialogFragment {


    public BottomNavigationDrawerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_bottomsheet, container, false);
        NavigationView navigationView = view.findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav1:
                        Snackbar.make(((MainActivity) Objects.requireNonNull(getActivity())).findViewById(R.id.viewSnack),
                                "nav1",
                                Snackbar.LENGTH_SHORT)
                                .show();
                        dismiss();
                        break;
                    case R.id.nav2:
                        Snackbar.make(((MainActivity) Objects.requireNonNull(getActivity())).findViewById(R.id.viewSnack),
                                "nav2",
                                Snackbar.LENGTH_SHORT)
                                .show();
                        dismiss();
                        break;
                    case R.id.nav3:
                        Snackbar.make(((MainActivity) Objects.requireNonNull(getActivity())).findViewById(R.id.viewSnack),
                                "nav3",
                                Snackbar.LENGTH_SHORT)
                                .show();
                        dismiss();
                        break;
                }
                return true;
            }
        });
        return view;
    }


}
