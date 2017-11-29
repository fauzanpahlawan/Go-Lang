package com.example.fauza.golang;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class TempatWisataViewHolder extends RecyclerView.ViewHolder {

    public Button buttonPilih;
    public TextView textViewNamaTempat;

    public TempatWisataViewHolder(View itemView) {
        super(itemView);
        textViewNamaTempat = itemView.findViewById(R.id.tv_nama_tempat);
        buttonPilih = itemView.findViewById(R.id.bt_pilih_tempat);
    }
}
