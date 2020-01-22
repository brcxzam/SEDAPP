package com.brcxzam.sedapp.adapter_users;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.brcxzam.sedapp.R;

import java.util.ArrayList;

public class ListMessagesAdapter extends BaseAdapter {

    private ArrayList<MensajePojo> mensajes;
    private String myemail;
    private Context context;

    public ListMessagesAdapter(ArrayList<MensajePojo> mensajes, String myemail, Context context){
        this.mensajes = mensajes;
        this.myemail = myemail;
        this.context = context;
    }

    @Override
    public int getCount() {
        return mensajes.size();
    }

    @Override
    public Object getItem(int position) {
        return mensajes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MensajePojo mensajePojo = mensajes.get(position);
        if (convertView == null){
            if (mensajePojo.de.equals(myemail)) convertView = LayoutInflater.from(context).inflate(R.layout.me_message, parent, false);
            else convertView = LayoutInflater.from(context).inflate(R.layout.other_message, parent, false);
        }
        TextView msg = null;
        if (mensajePojo.de.equals(myemail)) msg = (TextView) convertView.findViewById(R.id.mensaje_user);
        else msg = (TextView) convertView.findViewById(R.id.mensaje_user);
        msg.setText(mensajePojo.mensaje);
        return convertView;
    }

}
