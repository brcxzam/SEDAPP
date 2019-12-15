package com.brcxzam.sedapp.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DeleteOfflineDao {
    @Insert
    public void create(DeleteOffline deleteOffline);
    @Query("SELECT * FROM delete_offline WHERE table_name = 'Anexo_2_1'")
    public List<DeleteOffline> readAnexo21();
    @Delete
    public void delete(DeleteOffline deleteOffline);
}
