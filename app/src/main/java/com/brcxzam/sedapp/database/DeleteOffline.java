package com.brcxzam.sedapp.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "delete_offline")
public class DeleteOffline {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String table_name;
    private String id_row;

    public DeleteOffline(String table_name, String id_row) {
        this.table_name = table_name;
        this.id_row = id_row;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getTable_name() {
        return table_name;
    }

    public String getId_row() {
        return id_row;
    }
}
