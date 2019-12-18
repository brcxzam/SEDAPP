package com.brcxzam.sedapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.app.ActivityOptionsCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.brcxzam.sedapp.apollo_client.ApolloConnector;
import com.brcxzam.sedapp.apollo_client.Token;
import com.brcxzam.sedapp.type.ISignIn;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.Executor;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    TextInputLayout email, password;
    MaterialButton signIn;

    private Handler handler = new Handler();

    private Executor executor = new Executor() {
        @Override
        public void execute(@NotNull Runnable command) {
            handler.post(command);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        signIn = findViewById(R.id.signIn);
        signIn.setOnClickListener(this);

        clearError(email);
        clearError(password);

        Token token = new  Token(getApplicationContext());
        boolean status = token.getStatus();
        boolean auth = token.getBiometricAuth();
        if (auth && status) {
            showBiometricPrompt();
        }
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
                String email = this.email.getEditText().getText().toString();
                String password = this.password.getEditText().getText().toString();
                signIn(email,password,v);
            } else {
                String message = getString(R.string.errorData);
                email.setError(message);
                password.setError(message);
            }
        }
    }

    public void signIn(final String email, final String password, final View view) {
        ApolloConnector.setupApollo().mutate(
                SignInMutation
                        .builder()
                        .data(ISignIn.builder().email(email).password(password).build())
                        .build())
                .enqueue(new ApolloCall.Callback<SignInMutation.Data>() {

                    @Override
                    public void onResponse(@NotNull Response<SignInMutation.Data> response) {
                        if (response.data() != null) {
                            assert response.data().signIn != null;
                            boolean success = Boolean.valueOf(response.data().signIn.success);
                            if (success) {
                                assert response.data().signIn != null;
                                String cargo = response.data().signIn.cargo;
                                String token = response.data().signIn.token;
                                String nombre = response.data().signIn.nombre;
                                Token tokenSP = new Token(getApplicationContext());
                                tokenSP.setStatus(true);
                                tokenSP.setCargo(cargo);
                                tokenSP.setToken(token);
                                tokenSP.setNombre(nombre);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        inside();
                                    }
                                });
                            } else {
                                Snackbar.make(view,
                                        Objects.requireNonNull(Objects.requireNonNull(response.data().signIn()).message),
                                        Snackbar.LENGTH_SHORT)
                                        .show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        Snackbar.make(view, R.string.error_connection, Snackbar.LENGTH_SHORT)
                                .show();
                    }

                });
    }

    public void inside() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(getApplicationContext(),
                android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
        startActivity(intent, bundle);
        finish();
    }

    public void showBiometricPrompt() {
        BiometricPrompt.PromptInfo promptInfo =
                new BiometricPrompt.PromptInfo.Builder()
                .setTitle(getString(R.string.biometric_title))
                .setSubtitle(getString(R.string.biometric_message))
                .setNegativeButtonText(getString(R.string.negative_button))
                .setConfirmationRequired(false)
                .build();

        BiometricPrompt biometricPrompt = new BiometricPrompt(this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(),
                        "Authentication error", Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                inside();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(),
                        "Authentication failed", Toast.LENGTH_SHORT)
                        .show();
            }
        });

        biometricPrompt.authenticate(promptInfo);
    }
}
