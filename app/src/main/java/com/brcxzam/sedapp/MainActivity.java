package com.brcxzam.sedapp;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    AnimationDrawable animationDrawable;
    TextInputLayout email, password;
    MaterialButton signIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView relativeLayout = findViewById(R.id.title);

        animationDrawable = (AnimationDrawable) relativeLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2500);
        animationDrawable.setExitFadeDuration(5000);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        signIn = findViewById(R.id.signIn);
        signIn.setOnClickListener(this);

        clearError(email);
        clearError(password);
    }

    @Override
    protected void onStart() {
        super.onStart();
        animationDrawable.start();
    }

    public static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static boolean isValidPassword(CharSequence target) {
        return !TextUtils.isEmpty(target) && target.length() > 8;
    }

    public void clearError(final TextInputLayout textInputLayout) {
        Objects.requireNonNull(textInputLayout.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.signIn) {
            boolean emailV = isValidEmail(Objects.requireNonNull(email.getEditText()).getText());
            boolean passwordV = isValidPassword(Objects.requireNonNull(password.getEditText()).getText());
            if (emailV && passwordV) {
                Snackbar.make(v, "looks good", Snackbar.LENGTH_SHORT)
                        .show();
            } else {
                String message = "Datos Incorrectos";
                email.setError(message);
                password.setError(message);
            }
        }
    }
}
