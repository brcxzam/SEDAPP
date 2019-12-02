package com.brcxzam.sedapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.transition.ChangeBounds;
import android.transition.Explode;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Toolbar;

import com.google.android.material.bottomappbar.BottomAppBar;

import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setEnterTransition(new ChangeBounds());
        getWindow().setSharedElementEnterTransition(new ChangeBounds().setDuration(400));
        setContentView(R.layout.activity_main2);
        BottomAppBar bar = (BottomAppBar) findViewById(R.id.bottom_app_bar);
        setSupportActionBar(bar);
        bar.replaceMenu(R.menu.bottomappbar_menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.bottomappbar_menu, menu);
        return true;
    }
}
