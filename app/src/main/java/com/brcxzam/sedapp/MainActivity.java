package com.brcxzam.sedapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

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

import com.brcxzam.sedapp.apollo_client.Token;
import com.brcxzam.sedapp.database.NetworkStateChecker;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton fab;

    NetworkStateChecker checker = new NetworkStateChecker();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setEnterTransition(new ChangeBounds());
        getWindow().setSharedElementEnterTransition(new ChangeBounds().setDuration(400));
        setContentView(R.layout.activity_main2);
        final BottomAppBar bar = findViewById(R.id.bottom_app_bar);
        setSupportActionBar(bar);
        bar.replaceMenu(R.menu.bottomappbar_menu);

        registerReceiver(checker, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        fab = findViewById(R.id.fab);
        fab.hide();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(checker);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(checker, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(checker);
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
            case android.R.id.home:
                BottomNavigationDrawerFragment bottomNavigationDrawerFragment = new BottomNavigationDrawerFragment();
                bottomNavigationDrawerFragment.show(getSupportFragmentManager(),
                        bottomNavigationDrawerFragment.getTag());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void snackMessage(String message) {
        Snackbar.make(findViewById(R.id.viewSnack), message, Snackbar.LENGTH_SHORT)
                .show();
    }

    public void toggleFab(boolean show) {
        if (fab.isShown() && !show) {
            fab.hide();
        } else {
            fab.show();
        }
    }

}
