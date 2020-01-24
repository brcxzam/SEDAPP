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
    void create(Anexo21 anexo21);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void createALL(List<Anexo21> anexo21List);

    @Query("SELECT * FROM Anexo_2_1 ORDER BY fecha DESC")
    List<Anexo21> readALL();

    @Query("SELECT * FROM Anexo_2_1 WHERE accion == 'CREATE'")
    List<Anexo21> readAllUnsynced();

    @Query("SELECT * FROM Anexo_2_1 WHERE accion == 'DELETE'")
    List<Anexo21> readAllDelete();

    @Query("SELECT COUNT(*) FROM Anexo_2_1 WHERE total <= 45")
    int readCountAltaVulnerabilidad();

    @Query("SELECT COUNT(*) FROM Anexo_2_1 WHERE total <= 66 AND total >= 46")
    int readCountMedianaVulnerabilidad();

    @Query("SELECT COUNT(*) FROM Anexo_2_1 WHERE total <= 100 AND total >= 67")
    int readCountBajaVulnerabilidad();

    @Query("UPDATE Anexo_2_1 SET accion = :accion WHERE id = :id")
    void update(String accion, String id);

    @Delete
    void delete(Anexo21 anexo21);

    @Query("DELETE FROM Anexo_2_1 WHERE accion IS NULL")
    void deleteAll();
}
