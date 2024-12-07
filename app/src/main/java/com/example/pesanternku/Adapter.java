package com.example.pesanternku;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class Adapter extends RecyclerView.Adapter<ViewHolder> {
    Context context;
    List<Item> pesantrenList;

    public Adapter(Context context, List<Item> pesantrenList) {
        this.context = context;
        this.pesantrenList = pesantrenList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item pesantren = pesantrenList.get(position);
        holder.nameView.setText(pesantren.getNama());  // pastikan nama getter sesuai
        holder.alamat.setText(pesantren.getAlamat());
        holder.kyai.setText(pesantren.getKyai());

        // Perbaikan pada setOnClickListener
        holder.parentLayout.setOnClickListener(v -> {
            // Pastikan method getter sesuai dengan kelas Item
            Toast.makeText(context, "Anda memilih " + pesantren.getNama(), Toast.LENGTH_SHORT).show();
            // Intent untuk berpindah ke CommentActivity
            Intent intent = new Intent(context, CommentActivity.class);
            intent.putExtra("idPesantren", pesantren.getIdPesantren()); // Mengirimkan idPesantren
            intent.putExtra("idKota", pesantren.getIdKota());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Menambahkan flag
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return pesantrenList.size();
    }
}
