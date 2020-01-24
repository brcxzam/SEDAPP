package com.brcxzam.sedapp.adapter_users;

public class Usuario {

    public int id;
    public String nombre;
    public String email;
    public String cargo;

    public Usuario(int id, String nombre, String email, String cargo){
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.cargo = cargo;
    }

    @Override
    public String toString(){
        return "Nombre "+nombre+", correo: "+email+", cargo "+cargo;
    }
}
