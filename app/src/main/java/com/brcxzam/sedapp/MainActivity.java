package com.brcxzam.sedapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.transition.ChangeBounds;
import android.transition.Explode;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setEnterTransition(new ChangeBounds());
        getWindow().setSharedElementEnterTransition(new ChangeBounds().setDuration(400));
        setContentView(R.layout.activity_main2);
    }
}
