package com.brcxzam.sedapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.os.Bundle;
import android.transition.ChangeBounds;
import android.transition.Explode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.app_bar_fav:
                Snackbar.make(findViewById(R.id.viewSnack),
                        "Favorito",
                        Snackbar.LENGTH_SHORT)
                        .show();
                break;
            case R.id.app_bar_search:
                Snackbar.make(findViewById(R.id.viewSnack),
                        "Busqueda",
                        Snackbar.LENGTH_SHORT)
                        .show();
                break;
            case R.id.app_bar_settings:
                Snackbar.make(findViewById(R.id.viewSnack),
                        "Configuración",
                        Snackbar.LENGTH_SHORT)
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
}
