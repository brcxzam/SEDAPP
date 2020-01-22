package com.brcxzam.sedapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.brcxzam.sedapp.adapter_users.ListUserAdapter;
import com.brcxzam.sedapp.adapter_users.Usuario;
import com.brcxzam.sedapp.apollo_client.ApolloConnector;
import com.brcxzam.sedapp.apollo_client.Token;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class list_users extends AppCompatActivity {

    View back_layout;
    private ListUserAdapter adaptador;
    private RecyclerView recyclerView;
    private RequestQueue queue = null;
    private String email = null;
    private ArrayList<Usuario> usuarios = new ArrayList<>();
    private final String IPURL = "http://"+ ApolloConnector.IP+":6660/users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_users);

        // Inicializando cliente Volley
        this.queue = Volley.newRequestQueue(this);
        this.recyclerView = (RecyclerView) findViewById(R.id.listusers_recycle);
        // Llamando funcion para mostrar registros
        setEmailAuto();
        showUsers();
    }

    private void setEmailAuto(){
        SharedPreferences sharedPreferences = getSharedPreferences("emailreference", 0);
        email = sharedPreferences.getString("email", "");
        Log.e("-> Email", email);
    }

    private void showUsers(){
        StringRequest postRequest = new StringRequest(Request.Method.POST, IPURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Dentro de este metodo recibo los tados en json de nuestros usuarios disponibles para mostrar
                        try {
                            JSONObject object = new JSONObject(response);
                            JSONArray users = object.getJSONArray("usuarios");
                            if (users.length() > 0) usuarios.clear();
                            for (int i = 0; i < users.length(); i++)
                                if (!users.getJSONObject(i).getString("email").equals(email) )
                                    usuarios.add(new Usuario(
                                        users.getJSONObject(i).getInt("id"),
                                        users.getJSONObject(i).getString("nombre"),
                                        users.getJSONObject(i).getString("email"),
                                        users.getJSONObject(i).getString("cargo")
                                    ));
                            if (users.length() > 0) fillRecycler();
                        } catch (JSONException e) {
                            Log.e("-> Error al convertir en JSON", e.getMessage());
                        }
                        Log.d("-> Response: elementos: "+usuarios.size(), response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error192.168.43.131
                        Log.d("-> Error.Response: ", error.getMessage());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String>  params = new HashMap<>();
                params.put("email", email);
                Log.e("-> Enviado", email);
                return params;
            }
        };
        this.queue.add(postRequest);
    }

    private void fillRecycler(){
        adaptador = new ListUserAdapter(usuarios, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adaptador);
    }

    public void nextElement(Usuario usuario){
        //Toast.makeText(getApplicationContext(), "Correo: "+email, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getApplicationContext(), chating.class);
        intent.putExtra("cargo", usuario.cargo);
        intent.putExtra("nombre", usuario.nombre);
        intent.putExtra("email", usuario.email);
        startActivity(intent);
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
            case R.id.app_bar_chat:
                new MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog_Centered)
                        .setTitle("Ingresar al chat")
                        .setMessage("¿Estas seguro de ingresar al chat?")
                        .setPositiveButton(R.string.positive_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new Token(getApplicationContext()).clearToken();
                                Intent intent = new Intent(getApplicationContext(), list_users.class);
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
}
