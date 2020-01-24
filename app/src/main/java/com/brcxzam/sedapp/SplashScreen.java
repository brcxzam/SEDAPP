package com.brcxzam.sedapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.widget.ImageView;

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
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        handler.postDelayed(runnable,1500);
        bundle = ActivityOptionsCompat.makeCustomAnimation(getApplicationContext(),
                android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
        ImageView view = findViewById(R.id.title);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        float height = ( size.y - convertDpToPixel(100,getApplicationContext()) ) / 2;
        Log.e("Width", "" + width);
        Log.e("height", "" + height);
        ObjectAnimator animation = ObjectAnimator.ofFloat(view, "translationY", height);
        animation.setDuration(1500);
        animation.start();
    }

    public void startSignIn() {
        Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
        startActivity(intent, bundle);
        finish();
    }

    public void inside() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent, bundle);
        finish();
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float convertDpToPixel(float dp, Context context){
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
}
