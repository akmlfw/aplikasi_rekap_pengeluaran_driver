package com.example.tes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.PKLApp.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.text.DecimalFormat;

public class NoteAdapter extends FirestoreRecyclerAdapter<Note, NoteAdapter.NoteViewHolder> {
    Context context;

    public NoteAdapter(FirestoreRecyclerOptions<Note> options, MainActivity context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull NoteViewHolder holder, int position, @NonNull Note data) {
        // Menggunakan DecimalFormat untuk menghilangkan .0
        DecimalFormat df = new DecimalFormat("#");

        holder.kilometerawal_view.setText(df.format(data.getKmawali()));
        holder.bbm_view.setText(df.format(data.getBahanbkr()));
        holder.etoll_view.setText(df.format(data.getNgetol()));
        holder.parkir_view.setText(df.format(data.getBiypark()));
        holder.servis_view.setText(df.format(data.getSerman()));
        holder.kilometerakhir_view.setText(df.format(data.getKmakhiri()));
        holder.tanggal_view.setText(data.getTanggalu());
        holder.bulan_view.setText(data.getBulanu());

        holder.itemView.setOnClickListener((v) -> {
            Intent intent = new Intent(context, DetailsInput.class);
            intent.putExtra("kmawali", df.format(data.getKmawali()));
            intent.putExtra("bahanbkr", df.format(data.getBahanbkr()));
            intent.putExtra("ngetol", df.format(data.getNgetol()));
            intent.putExtra("biypark", df.format(data.getBiypark()));
            intent.putExtra("serman", df.format(data.getSerman()));
            intent.putExtra("kmakhiri", df.format(data.getKmakhiri()));
            intent.putExtra("tanggalu", data.getTanggalu());
            intent.putExtra("bulanu", data.getBulanu());
            String docId = getSnapshots().getSnapshot(position).getId();
            intent.putExtra("docId", docId);
            context.startActivity(intent);
        });
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_note_item, parent, false);
        return new NoteViewHolder(view);
    }

    class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView kilometerawal_view, kilometerakhir_view, bbm_view, parkir_view, servis_view, etoll_view, tanggal_view, bulan_view;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            kilometerawal_view = itemView.findViewById(R.id.note_title_text_view);
            bbm_view = itemView.findViewById(R.id.bahanbakar_id);
            etoll_view = itemView.findViewById(R.id.etoll_id);
            parkir_view = itemView.findViewById(R.id.parkir_id);
            servis_view = itemView.findViewById(R.id.service_id);
            kilometerakhir_view = itemView.findViewById(R.id.kmakhir_id);
            tanggal_view = itemView.findViewById(R.id.tanggal_id);
            bulan_view = itemView.findViewById(R.id.bulan_id);
        }
    }
}