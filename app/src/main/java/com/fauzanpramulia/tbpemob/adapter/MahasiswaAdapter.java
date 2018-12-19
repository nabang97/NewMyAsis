package com.fauzanpramulia.tbpemob.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fauzanpramulia.tbpemob.DetailActivity;
import com.fauzanpramulia.tbpemob.R;
import com.fauzanpramulia.tbpemob.model.MahasiswaItems;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MahasiswaAdapter extends RecyclerView.Adapter<MahasiswaAdapter.MahasiswaHolder> {
    ArrayList<MahasiswaItems> dataMahasiswa;
    Context context;
    public void setDataMahasiswa(ArrayList<MahasiswaItems> films) {
        this.dataMahasiswa = films;
        notifyDataSetChanged();
    }

    public MahasiswaAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public MahasiswaHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mahasiswa, parent, false);
        return new MahasiswaHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MahasiswaHolder holder, final int position) {

                MahasiswaItems mahasiswa = dataMahasiswa.get(position);
               // holder.textBp.setText(mahasiswa.getBp());
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

                holder.textNama.setText(mahasiswa.getNama());
                holder.textTanggal.setText(tanggalString);
                holder.textMataKuliah.setText(mahasiswa.getMata_kuliah());
                holder.textWaktu.setText(waktuString);

                if(mahasiswa.getStatus() == 1){
                    holder.rvIcon.setImageResource(R.drawable.like_checked);
                }else{
                    holder.rvIcon.setImageResource(R.drawable.like_unchecked);
                }

                String url = "http://10.44.7.157:8000/image/" + mahasiswa.getFoto();
                Glide.with(holder.itemView)
                        .load(url)
                        .into(holder.imageFoto);

            holder.view_container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // sending data process
                    Intent i = new Intent(context, DetailActivity.class);
                    i.putExtra(DetailActivity.EXTRA_DETAIL_ABSEN, dataMahasiswa.get(position));
                    context.startActivity(i);
                }
            });

    }

    @Override
    public int getItemCount() {
        if (dataMahasiswa != null) {
            return dataMahasiswa.size();
        }
        return 0;
    }

    public class MahasiswaHolder extends RecyclerView.ViewHolder {
        ImageView imageFoto;
        TextView textBp;
        TextView textNama;
        TextView textTanggal;
        TextView textMataKuliah;
        TextView textWaktu;
        RelativeLayout view_container;
        ImageView rvIcon;

        public MahasiswaHolder(View itemView) {
            super(itemView);

          //  textBp = itemView.findViewById(R.id.textBp);
            textNama = itemView.findViewById(R.id.textNama);
            textTanggal = itemView.findViewById(R.id.textTanggal);
            textMataKuliah = itemView.findViewById(R.id.textMataKuliah);
            textWaktu = itemView.findViewById(R.id.textWaktu);
            imageFoto = itemView.findViewById(R.id.imageSelfie);
            rvIcon = itemView.findViewById(R.id.rv_icon);
            view_container = (RelativeLayout) itemView.findViewById(R.id.container);
        }
    }

}
