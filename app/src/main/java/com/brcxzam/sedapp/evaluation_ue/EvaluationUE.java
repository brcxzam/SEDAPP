package com.brcxzam.sedapp.evaluation_ue;


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
public class EvaluationUE extends Fragment {


    public EvaluationUE() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_evaluation_ed, container, false);
        final FloatingActionButton fab = ((MainActivity) Objects.requireNonNull(getActivity())).findViewById(R.id.fab);
        final NavController navController = Navigation.findNavController(getActivity(),R.id.nav_host_fragment);
        final NavOptions navOptions = new NavOptions.Builder()
                .setEnterAnim(R.anim.slide_in_right)
                .setExitAnim(R.anim.slide_out_left)
                .setPopEnterAnim(R.anim.slide_in_left)
                .setPopExitAnim(R.anim.slide_out_right)
                .build();
        if (!fab.isShown()) {
            fab.show();
        }
        fab.hide(new FloatingActionButton.OnVisibilityChangedListener() {
            @Override
            public void onHidden(FloatingActionButton fab) {
                super.onHidden(fab);
                fab.setImageResource(R.drawable.ic_add_black_24dp);
                fab.show();
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (navController.getCurrentDestination().getId() == R.id.evaluationUE) {
                    navController.navigate(R.id.action_evaluationUE_to_questionsUE,null,navOptions);
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
        return view;

    }

}
