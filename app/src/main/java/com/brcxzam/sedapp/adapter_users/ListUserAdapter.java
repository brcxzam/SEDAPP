package com.brcxzam.sedapp.adapter_users;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.brcxzam.sedapp.R;
import com.brcxzam.sedapp.list_users;

import java.util.ArrayList;

public class ListUserAdapter extends RecyclerView.Adapter<ListUserAdapter.MyViewHolder> {

    private ArrayList<Usuario> usuarios = new ArrayList<>();
    private list_users listUsers;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView nombre, email, cargo;
        public LinearLayout itemonly;

        public MyViewHolder(View view){
            super(view);
            nombre = (TextView) view.findViewById(R.id.nombre_user);
            email = (TextView) view.findViewById(R.id.email_user);
            cargo = (TextView) view.findViewById(R.id.cargo_user);
            itemonly = (LinearLayout) view.findViewById(R.id.itemonly);
        }
    }

    public ListUserAdapter (ArrayList<Usuario> usuarios, list_users listUsers){
        this.usuarios = usuarios;
        this.listUsers = listUsers;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Usuario user = usuarios.get(position);
        holder.nombre.setText(user.nombre);
        holder.email.setText(user.email);
        holder.cargo.setText(user.cargo);
        holder.itemonly.setOnClickListener( (view) -> { listUsers.nextElement(user); });
    }

    @Override
    public int getItemCount() {
        return usuarios.size();
    }

}