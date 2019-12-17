package com.brcxzam.sedapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.brcxzam.sedapp.apollo_client.Token;

public class SplashScreen extends AppCompatActivity {

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Token token = new  Token(getApplicationContext());
            boolean status = token.getStatus();
            boolean auth = token.getBiometricAuth();
            if (status && !auth) {
                inside();
            } else {
                startSignIn();
            }
        }
    };
    View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        handler.postDelayed(runnable,1500);
        view = findViewById(R.id.title);
    }

    public void startSignIn() {
        Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, view, "logo_tescha");
        startActivity(intent, options.toBundle());
        finishAfterTransition();
    }

    public void inside() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finishAfterTransition();
    }
}
