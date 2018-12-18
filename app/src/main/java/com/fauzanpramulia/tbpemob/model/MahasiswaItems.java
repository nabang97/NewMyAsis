package com.fauzanpramulia.tbpemob.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

public class MahasiswaItems implements Parcelable {

        public int id;
        public String bp;
        public String nama;
        public String kelas;
        public String mata_kuliah;
        public String foto;
        public String created_at;

    public MahasiswaItems(int id, String bp, String nama, String kelas, String mata_kuliah, String foto, String created_at) {
        this.id = id;
        this.bp = bp;
        this.nama = nama;
        this.kelas = kelas;
        this.mata_kuliah = mata_kuliah;
        this.foto = foto;
        this.created_at = created_at;
    }

    public MahasiswaItems() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBp() {
        return bp;
    }

    public void setBp(String bp) {
        this.bp = bp;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getKelas() {
        return kelas;
    }

    public void setKelas(String kelas) {
        this.kelas = kelas;
    }

    public String getMata_kuliah() {
        return mata_kuliah;
    }

    public void setMata_kuliah(String mata_kuliah) {
        this.mata_kuliah = mata_kuliah;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.bp);
        dest.writeString(this.nama);
        dest.writeString(this.kelas);
        dest.writeString(this.mata_kuliah);
        dest.writeString(this.foto);
        dest.writeString(this.created_at);
    }

    protected MahasiswaItems(Parcel in) {
        this.id = in.readInt();
        this.bp = in.readString();
        this.nama = in.readString();
        this.kelas = in.readString();
        this.mata_kuliah = in.readString();
        this.foto = in.readString();
        this.created_at = in.readString();
    }

    public static final Creator<MahasiswaItems> CREATOR = new Creator<MahasiswaItems>() {
        @Override
        public MahasiswaItems createFromParcel(Parcel source) {
            return new MahasiswaItems(source);
        }

        @Override
        public MahasiswaItems[] newArray(int size) {
            return new MahasiswaItems[size];
        }
    };
}
