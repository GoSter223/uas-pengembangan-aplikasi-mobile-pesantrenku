package com.example.pesanternku;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ViewHolder extends RecyclerView.ViewHolder {
    TextView nameView,alamat, kyai, comment;
    RelativeLayout parentLayout;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);
        nameView = itemView.findViewById(R.id.nama_pesantren);
        alamat = itemView.findViewById(R.id.alamat_pesantren);
        kyai = itemView.findViewById(R.id.nama_kyai);
        comment = itemView.findViewById(R.id.comment);
        parentLayout = itemView.findViewById(R.id.list_view);

        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = itemView.getContext();
            }
        });
    }

}
