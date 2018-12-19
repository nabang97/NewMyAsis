package com.fauzanpramulia.tbpemob;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.fauzanpramulia.tbpemob.adapter.MahasiswaAdapter;
import com.fauzanpramulia.tbpemob.db.Absensi;
import com.fauzanpramulia.tbpemob.db.AppDatabase;
import com.fauzanpramulia.tbpemob.model.MahasiswaItems;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    MahasiswaAdapter adapter;
    List<MahasiswaItems> daftarAbsen = new ArrayList<>();
    List<MahasiswaItems> mItems = new ArrayList<>();
    AppDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        db = Room.databaseBuilder(this, AppDatabase.class, "asis.db")
                .allowMainThreadQueries()
                .build();

        adapter = new MahasiswaAdapter(this);
        int orientation= getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT){
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }else{
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        }
        if (savedInstanceState != null )
        {
            mItems = savedInstanceState.getParcelableArrayList("absensi");
            if (mItems != null){
                adapter.setDataMahasiswa(new ArrayList<MahasiswaItems>(mItems));
                daftarAbsen =mItems;
            }

        }else{
            getAbsensi();
        }
        recyclerView.setAdapter(adapter);
    }

    public void getFavoriteAbsensi() {
//        String API_BASE_URL = "http://10.44.7.170:8000/api/";
        progressBar.setVisibility(View.VISIBLE);
        if (isConnected()) {
            String API_BASE_URL = "http://10.44.7.157:8000/api/";
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            AbsensiClient client = retrofit.create(AbsensiClient.class);

            Call<List<MahasiswaItems>> call = client.getFavoriteAbsen();
            call.enqueue(new Callback<List<MahasiswaItems>>() {
                @Override
                public void onResponse(Call<List<MahasiswaItems>> call, Response<List<MahasiswaItems>> response) {
                    List<MahasiswaItems> absenList = response.body();
                    daftarAbsen = absenList;
                    saveMovieToDb(absenList);
                    adapter.setDataMahasiswa((ArrayList<MahasiswaItems>) absenList);
                    progressBar.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onFailure(Call<List<MahasiswaItems>> call, Throwable t) {
                    //Disini kode kalau error
                    Toast.makeText(MainActivity.this, "Gagal Load Data", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            //ambil data ke db
            List<Absensi> absensis = db.absensiDao().getAllAbsen();
            ArrayList<MahasiswaItems> absensi = new ArrayList<>();
            for (Absensi n : absensis) {
                MahasiswaItems m = new MahasiswaItems(
                        n.id,
                        n.bp,
                        n.nama,
                        n.kelas,
                        n.mata_kuliah,
                        n.foto,
                        n.created_at,
                        n.status
                );
                absensi.add(m);
            }

            daftarAbsen = absensi;
            adapter.setDataMahasiswa(absensi);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    public void getAbsensi() {
//        String API_BASE_URL = "http://10.44.7.170:8000/api/";
        progressBar.setVisibility(View.VISIBLE);
        if (isConnected()) {
            String API_BASE_URL = "http://10.44.7.157:8000/api/";
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            AbsensiClient client = retrofit.create(AbsensiClient.class);

            Call<List<MahasiswaItems>> call = client.getAbsensi();
            call.enqueue(new Callback<List<MahasiswaItems>>() {
                @Override
                public void onResponse(Call<List<MahasiswaItems>> call, Response<List<MahasiswaItems>> response) {
                    List<MahasiswaItems> absenList = response.body();
                    daftarAbsen = absenList;
                    saveMovieToDb(absenList);
                    adapter.setDataMahasiswa((ArrayList<MahasiswaItems>) absenList);
                    progressBar.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onFailure(Call<List<MahasiswaItems>> call, Throwable t) {
                    //Disini kode kalau error
                    Toast.makeText(MainActivity.this, "Gagal Load Data", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            //ambil data ke db
            List<Absensi> absensis = db.absensiDao().getAllAbsen();
            ArrayList<MahasiswaItems> absensi = new ArrayList<>();
            for (Absensi n : absensis) {
                MahasiswaItems m = new MahasiswaItems(
                        n.id,
                        n.bp,
                        n.nama,
                        n.kelas,
                        n.mata_kuliah,
                        n.foto,
                        n.created_at,
                        n.status
                );
                absensi.add(m);
            }

            daftarAbsen = absensi;
            adapter.setDataMahasiswa(absensi);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    public boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("absensi", (ArrayList<? extends Parcelable>) daftarAbsen);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addAbsen:
                Intent addDataActivityIntent = new Intent(MainActivity.this, InsertActivity.class);
                startActivity(addDataActivityIntent);
                break;
            case R.id.favorite_menu:
                getFavoriteAbsensi();
                break;

            case R.id.refreshAbsen:
                    getAbsensi();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void saveMovieToDb(final List<MahasiswaItems> listMovieItem) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(MahasiswaItems m:listMovieItem){
                    Absensi absensi = new Absensi();
                    absensi.id = m.id;
                    absensi.bp = m.bp;
                    absensi.nama = m.nama;
                    absensi.kelas = m.kelas;
                    absensi.mata_kuliah = m.mata_kuliah;
                    absensi.foto = m.foto;
                    absensi.created_at = m.created_at;

                    db.absensiDao().insertAbsen(absensi);
                }
            }
        }).start();

    }
}
