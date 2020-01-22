package com.brcxzam.sedapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.brcxzam.sedapp.adapter_users.ListMessagesAdapter;
import com.brcxzam.sedapp.adapter_users.MensajePojo;
import com.brcxzam.sedapp.adapter_users.Usuario;
import com.brcxzam.sedapp.apollo_client.ApolloConnector;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.github.nkzawa.emitter.Emitter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;

public class chating extends AppCompatActivity {

    private Usuario usuarioReceptor;
    private EditText messagetext;
    private Button send;
    private ListView lista;
    private ArrayList<MensajePojo> listamensajes = new ArrayList<>();
    private androidx.appcompat.widget.Toolbar nombrecontecto;

    // Propiedades para crear un socket
    private Socket mSocket = null;
    private String IPURL = "http://"+ ApolloConnector.IP+":6660/";
    private String email = null;

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chating);
        Bundle bundle = getIntent().getExtras();
        usuarioReceptor = new Usuario(0, bundle.getString("nombre"), bundle.getString("email"), bundle.getString("cargo"));
        setEmailAuto();
        lista = (ListView) findViewById(R.id.list_messages);
        messagetext = (EditText) findViewById(R.id.message_text);
        send = (Button) findViewById(R.id.send_message);
        send.setOnClickListener( v -> {
            sendMessage("{\"de\": \""+email+"\", \"para\": \""+usuarioReceptor.email+"\", \"mensaje\": \""+messagetext.getText().toString().trim()+"\"}");
            messagetext.setText("");
        });
        nombrecontecto = (androidx.appcompat.widget.Toolbar) findViewById(R.id.nombrecontecto);
        nombrecontecto.setTitle(usuarioReceptor.nombre);
        try {
            mSocket = IO.socket(IPURL);
            mSocket.on("chat message", onNewMessage);
            mSocket.connect();
            sendMessage("{\"de\": \""+email+"\", \"para\": \""+usuarioReceptor.email+"\", \"get\": true}");
        } catch (URISyntaxException e) {
            Log.e("-> Error socket instancia", e.getMessage());
        }
    }

    private void sendMessage(String msg){
        //mSocket.emit("chat message", message.getText().toString().trim());
        mSocket.emit("chat message", msg.trim());
    }

    private void fillListMessage(){
        ListMessagesAdapter messagesAdapter = new ListMessagesAdapter(listamensajes, email, getApplicationContext());
        lista.setAdapter(messagesAdapter);
        lista.setSelection(listamensajes.size()-1);
    }

    private void setEmailAuto(){
        SharedPreferences sharedPreferences = getSharedPreferences("emailreference", 0);
        email = sharedPreferences.getString("email", "");
        Log.e("-> Email", email);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mSocket.disconnect();
        mSocket.off("chat message", onNewMessage);
    }

    @SuppressLint("LongLogTag")
    private Emitter.Listener onNewMessage = args -> runOnUiThread( () -> {

        try {
            if (args != null) {
                if (args[0] != null) {
                    JSONObject object = new JSONObject(args[0].toString());
                    try {
                        JSONArray mensajes = new JSONArray(object.getString("mensajes"));
                        listamensajes.clear();
                        for (int i = 0; i < mensajes.length(); i++)
                            listamensajes.add(new MensajePojo(mensajes.getJSONObject(i).getString("de"), mensajes.getJSONObject(i).getString("para"), mensajes.getJSONObject(i).getString("mensaje")));
                        //Log.e("-> Mensaje de "+mensajes.getJSONObject(i).getString("de"), " para "+mensajes.getJSONObject(i).getString("para"));

                    } catch (JSONException e) {
                        listamensajes.add(new MensajePojo(
                                object.getString("de"),
                                object.getString("para"),
                                object.getString("mensaje")
                        ));
                    } finally {
                        fillListMessage();
                    }
                }
            }
        } catch (Exception e) {
            Log.e("Error hilo listener socket", e.getMessage());
            return;
        }

        // add the message to view
        //addMessage(username, message);
    });
}
