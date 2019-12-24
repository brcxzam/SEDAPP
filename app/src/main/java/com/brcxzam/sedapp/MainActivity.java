package com.brcxzam.sedapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.transition.ChangeBounds;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.brcxzam.sedapp.apollo_client.Token;
import com.brcxzam.sedapp.database.NetworkStateChecker;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.roacult.backdrop.BackdropLayout;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    NetworkStateChecker checker = new NetworkStateChecker();

    Toolbar toolbar;
    BackdropLayout backdropLayout;
    View back_layout;
    NavHostFragment hostFragment;
    NavController navController;
    NavOptions navOptions;
    MaterialButton charts, places, evaluations, evaluation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setEnterTransition(new ChangeBounds());
        getWindow().setSharedElementEnterTransition(new ChangeBounds().setDuration(400));
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        registerReceiver(checker, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        backdropLayout = findViewById(R.id.container);
        back_layout = backdropLayout.getBackLayout();

        charts = back_layout.findViewById(R.id.charts);
        places = back_layout.findViewById(R.id.places);
        evaluation = back_layout.findViewById(R.id.evaluation);
        evaluations = back_layout.findViewById(R.id.evaluations);

        charts.setOnClickListener(this);
        places.setOnClickListener(this);
        evaluation.setOnClickListener(this);
        evaluations.setOnClickListener(this);

        hostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = hostFragment.getNavController();
        navOptions = new NavOptions.Builder()
                .setEnterAnim(R.anim.fragment_fade_enter)
                .setExitAnim(R.anim.fragment_fade_exit)
                .setPopEnterAnim(R.anim.fragment_fade_enter)
                .setPopExitAnim(R.anim.fragment_fade_exit)
                .build();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(checker);
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(checker, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(checker, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.bottomappbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("PrivateResource")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.app_bar_sign_off:
                new MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog_Centered)
                        .setTitle(R.string.sign_off)
                        .setMessage(R.string.sign_off_message)
                        .setPositiveButton(R.string.positive_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new Token(getApplicationContext()).clearToken();
                                Intent intent = new Intent(getApplicationContext(), SplashScreen.class);
                                startActivity(intent);
                                finishAfterTransition();
                            }
                        })
                        .show();
                break;
            case R.id.app_bar_biometric_auth:
                final Token token = new Token(getApplicationContext());
                new MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog_Centered)
                        .setTitle(R.string.biometric_auth)
                        .setMessage(R.string.biometric_auth_message)
                        .setPositiveButton(R.string.biometric_true, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                token.setBiometricAuth(true);
                                snackMessage(getString(R.string.biometric_on));
                            }
                        })
                        .setNegativeButton(R.string.biometric_false, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                token.setBiometricAuth(false);
                                snackMessage(getString(R.string.biometric_off));
                            }
                        })
                        .show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void snackMessage(String message) {
        Snackbar.make(back_layout, message, Snackbar.LENGTH_SHORT)
                .show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.charts:
                if (navController.getCurrentDestination().getId() != R.id.charts){
                    navController.navigate(R.id.action_global_charts,null,navOptions);
                }
                break;
            case R.id.places:
                if (navController.getCurrentDestination().getId() != R.id.places){
                    navController.navigate(R.id.action_global_places,null,navOptions);
                }
                break;
            case R.id.evaluation:
                navController.navigate(R.id.action_global_evaluation,null,navOptions);
                break;
            case R.id.evaluations:
                if (navController.getCurrentDestination().getId() != R.id.evaluations){
                    navController.navigate(R.id.action_global_evaluations,null,navOptions);
                }
                break;
        }
        backdropLayout.close();
    }
}
