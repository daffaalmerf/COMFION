package com.example.publictransportationapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.publictransportationapp.R;
import com.example.publictransportationapp.model.ScanModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class ScanAdapter extends FirestoreRecyclerAdapter<ScanModel, ScanAdapter.ScanHolder> {

    public ScanAdapter(@NonNull FirestoreRecyclerOptions<ScanModel> option) {

        super(option);

    }

    @Override
    protected void onBindViewHolder(@NonNull ScanAdapter.ScanHolder holder, int position, @NonNull ScanModel model) {

        DateFormat dateFormat = SimpleDateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US);
        String date = dateFormat.format(model.getTimestamp());

        holder.scanDate.setText(date);
        holder.scanCode.setText(model.getCode());

    }

    @NonNull
    @Override
    public ScanAdapter.ScanHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_scan, parent, false);

        return new ScanAdapter.ScanHolder(view);

    }

    class ScanHolder extends RecyclerView.ViewHolder {

        TextView scanCode, scanDate;

        public ScanHolder(@NonNull View itemView) {

            super(itemView);

            scanCode = itemView.findViewById(R.id.singleScanCode);
            scanDate = itemView.findViewById(R.id.singleScanDate);


        }

    }

}
