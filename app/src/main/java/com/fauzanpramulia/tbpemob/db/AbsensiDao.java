package com.fauzanpramulia.tbpemob.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface AbsensiDao {
  @Query("SELECT * FROM absensi")
    List<Absensi> getAllAbsen();

  @Query("SELECT * FROM absensi WHERE id = :id")
    Absensi getById(int id);

  @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAbsen(Absensi absensi);

  @Delete
    void delete(Absensi absensi);
}
