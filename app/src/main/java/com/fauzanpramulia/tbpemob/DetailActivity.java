package com.fauzanpramulia.tbpemob;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.fauzanpramulia.tbpemob.db.Absensi;
import com.fauzanpramulia.tbpemob.model.MahasiswaItems;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
    Integer id;
    public static String EXTRA_DETAIL_ABSEN = "extra_detail_absen";
    ProgressBar progressBar;
//    CheckBox checkBox;
    ImageView favoriteIcon;
    Integer statusNow;
    AbsensiClient mApiService;

    @SuppressLint("WrongViewCast")
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
        String url = "http://10.44.7.157:8000/image/" + mahasiswa.getFoto();
        Glide.with(this)
                .load(url)
                .into(imageDetail);
        textDetailBp.setText(mahasiswa.getBp());
        textDetailNama.setText(mahasiswa.getNama());
        textDetailMatkul.setText(mahasiswa.getMata_kuliah());
        textDetailTanggal.setText(tanggalString);
        textDetailWaktu.setText(waktuString);
        textDetailKelas.setText(mahasiswa.getKelas());
        id = mahasiswa.getId();
        statusNow = mahasiswa.getStatus();

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

        if(statusNow.equals(1)){
            favoriteIcon = findViewById(R.id.favorite);
            favoriteIcon.setImageResource(R.drawable.like_two_checked);
        }else{
            favoriteIcon = findViewById(R.id.favorite);
            favoriteIcon.setImageResource(R.drawable.like_two_unchecked);
        }

        favoriteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateStatus();
            }
        });

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("absensi",  absen);
    }

    public void updateStatus(){

        String API_BASE_URL = "http://10.44.7.157:8000/api/";

        Retrofit adapter = new Retrofit.Builder()
                .baseUrl(API_BASE_URL) //Setting the Root URL
                .addConverterFactory(GsonConverterFactory.create())
                .build(); //Finally building the adapter

        mApiService = adapter.create(AbsensiClient.class);

        mApiService.updateStatus(id).enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    try {
                        JSONObject jsonRESULTS = new JSONObject(response.body().string());
                        if (jsonRESULTS.getString("status").equals("success")){

                            if(jsonRESULTS.getString("data").equals("1")){
                                favoriteIcon = findViewById(R.id.favorite);
                                favoriteIcon.setImageResource(R.drawable.like_two_checked);
                                Toast.makeText(DetailActivity.this, "Loved it!", Toast.LENGTH_SHORT).show();
                            }else{
                                favoriteIcon = findViewById(R.id.favorite);
                                favoriteIcon.setImageResource(R.drawable.like_two_unchecked);
                                Toast.makeText(DetailActivity.this, "UnLoved it! :(", Toast.LENGTH_SHORT).show();
                            }

//                                Toast.makeText(DetailActivity.this, "Loved it!", Toast.LENGTH_SHORT).show();




                        } else {

                            String error_message = jsonRESULTS.getString("error_msg");
                            Toast.makeText(DetailActivity.this, error_message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(DetailActivity.this, "Ada masalah server!", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    } catch (IOException e) {
                        Toast.makeText(DetailActivity.this, "Masalah Koneksi Jaringan!", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(DetailActivity.this, "Server Sedang Maintenance!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(DetailActivity.this, "Masalah Koneksi Jaringan!", Toast.LENGTH_SHORT).show();
                Log.e("debug", "onFailure: ERROR > " + t.toString());
            }
        });
    }
}
