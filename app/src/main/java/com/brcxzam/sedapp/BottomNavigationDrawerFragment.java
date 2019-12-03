package com.brcxzam.sedapp;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
                NavController navController = Navigation.findNavController(getActivity(),R.id.nav_host_fragment);
                NavOptions navOptions = new NavOptions.Builder()
                        .setEnterAnim(R.anim.slide_in_right)
                        .setExitAnim(R.anim.slide_out_left)
                        .setPopEnterAnim(R.anim.slide_in_left)
                        .setPopExitAnim(R.anim.slide_out_right)
                        .build();
                switch (item.getItemId()) {
                    case R.id.nav1:
                        if (navController.getCurrentDestination().getId() != R.id.charts){
                            navController.navigate(R.id.charts,null,navOptions);
                        }
                        dismiss();
                        break;
                    case R.id.nav2:
                        if (navController.getCurrentDestination().getId() != R.id.action_charts_to_placesUE){
                            navController.navigate(R.id.placesUE,null,navOptions);
                        }
                        dismiss();
                        break;
                    case R.id.nav3:
                        if (navController.getCurrentDestination().getId() != R.id.evaluationED){
                            navController.navigate(R.id.evaluationED,null,navOptions);
                        }
                        dismiss();
                        break;
                    case R.id.nav4:
                        if (navController.getCurrentDestination().getId() != R.id.evaluationUE){
                            navController.navigate(R.id.evaluationUE,null,navOptions);
                        }
                        dismiss();
                        break;
                }
                return true;
            }
        });
        return view;
    }


}
