package com.fauzanpramulia.tbpemob.db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "absensi")
public class Absensi {
    @PrimaryKey
    public int id;

    @ColumnInfo(name = "bp")
    public String bp;

    @ColumnInfo(name = "nama")
    public String nama;

    @ColumnInfo(name = "kelas")
    public String kelas;

    @ColumnInfo(name = "mata_kuliah")
    public String mata_kuliah;

    @ColumnInfo(name = "foto")
    public String foto;

    @ColumnInfo(name = "created_at")
    public String created_at;
}
