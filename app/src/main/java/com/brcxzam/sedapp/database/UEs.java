package com.brcxzam.sedapp.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "UE")
public class UEs {
    @PrimaryKey
    @NonNull private String RFC;
    private String razon_social;

    public UEs(String RFC, String razon_social) {
        this.RFC = RFC;
        this.razon_social = razon_social;
    }

    public String getRFC() {
        return RFC;
    }

    public String getRazon_social() {
        return razon_social;
    }

    @NonNull
    @Override
    public String toString() {
        return razon_social;
    }
}
