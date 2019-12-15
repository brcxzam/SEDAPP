package com.brcxzam.sedapp.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UEsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertAll(List<UEs> uEs);

    @Query("SELECT * FROM UE")
    public List<UEs> readAll();
}
