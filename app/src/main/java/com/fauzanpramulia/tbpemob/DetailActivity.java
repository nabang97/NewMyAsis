package com.fauzanpramulia.tbpemob;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fauzanpramulia.tbpemob.model.MahasiswaItems;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {
    @BindView(R.id.imageDetail) ImageView imageDetail;
    @BindView(R.id.textDetailBp) TextView textDetailBp;
    @BindView(R.id.textDetailNama) TextView textDetailNama;
    @BindView(R.id.textDetailMatkul) TextView textDetailMatkul;
    @BindView(R.id.textTanggalDetail) TextView textDetailTanggal;
    @BindView(R.id.textWaktuDetail) TextView textDetailWaktu;
    @BindView(R.id.textDetailKelas) TextView textDetailKelas;
    @BindView(R.id.buttonShare) Button buttonShare;
    MahasiswaItems absen = new MahasiswaItems();
    MahasiswaItems mItems = new MahasiswaItems();



    public static String EXTRA_DETAIL_ABSEN = "extra_detail_absen";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        MahasiswaItems mahasiswa=null;

        if (savedInstanceState != null )
        {
            mItems = savedInstanceState.getParcelable("absensi");
            if (mItems != null){
                mahasiswa =mItems;
                absen = mItems;
            }

        }else{
            mahasiswa= getIntent().getParcelableExtra(EXTRA_DETAIL_ABSEN);
            absen = mahasiswa;
        }
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy");
        SimpleDateFormat formatter2 = new SimpleDateFormat("hh:mm a");
        Date date=null;
        try {
            date= f.parse(mahasiswa.getCreated_at());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String tanggalString = formatter.format(date);
        String waktuString = formatter2.format(date);
        String url = "http://192.168.1.6:8000/image/" + mahasiswa.getFoto();
        Glide.with(this)
                .load(url)
                .into(imageDetail);
        textDetailBp.setText(mahasiswa.getBp());
        textDetailNama.setText(mahasiswa.getNama());
        textDetailMatkul.setText(mahasiswa.getMata_kuliah());
        textDetailTanggal.setText(tanggalString);
        textDetailWaktu.setText(waktuString);
        textDetailKelas.setText(mahasiswa.getKelas());

        final MahasiswaItems finalMahasiswa = mahasiswa;
        buttonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        "Bp : "+ finalMahasiswa.getBp()+
                                "\nNama : "+ finalMahasiswa.getNama()+
                                "\nKelas : "+ finalMahasiswa.getKelas()+
                                "\nMata Kuliah : "+ finalMahasiswa.getMata_kuliah()+
                                "\nTanggal/Jam : "+ finalMahasiswa.getCreated_at());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("absensi",  absen);
    }
}
