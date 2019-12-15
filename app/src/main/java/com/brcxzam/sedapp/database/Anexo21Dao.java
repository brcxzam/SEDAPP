package com.brcxzam.sedapp.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface Anexo21Dao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void create(Anexo21 anexo21);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void createALL(List<Anexo21> anexo21List);

    @Query("SELECT * FROM Anexo_2_1 ORDER BY id DESC")
    public List<Anexo21> readALL();

    @Query("SELECT * FROM Anexo_2_1 WHERE accion == 'CREATE'")
    public List<Anexo21> readAllUnsynced();

    @Query("SELECT * FROM ANEXO_2_1 WHERE accion == 'DELETE'")
    public List<Anexo21> readAllDelete();

    @Query("UPDATE Anexo_2_1 SET accion = :accion WHERE id = :id")
    public void update(String accion, String id);

    @Delete
    public void delete(Anexo21 anexo21);

    @Query("DELETE FROM Anexo_2_1 WHERE accion IS NULL")
    public void deleteAll();
}
