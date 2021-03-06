package com.brcxzam.sedapp.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UEsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<UEs> uEs);

    @Query("SELECT * FROM UE")
    List<UEs> readAll();
}
