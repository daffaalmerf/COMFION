package com.example.publictransportationapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.publictransportationapp.R;
import com.example.publictransportationapp.ReportDetailActivity;
import com.example.publictransportationapp.model.ReportModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class ReportAdapter extends FirestoreRecyclerAdapter<ReportModel, ReportAdapter.ReportHolder> {

    private Context context;

    public ReportAdapter(@NonNull FirestoreRecyclerOptions<ReportModel> option, Context context) {

        super(option);
        this.context = context;

    }

    @Override
    protected void onBindViewHolder(@NonNull ReportAdapter.ReportHolder holder, int position, @NonNull ReportModel model) {

        DateFormat dateFormat = SimpleDateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US);
        final String date = dateFormat.format(model.getTimestamp());
        final String location = model.getLocation() + " (" + model.getLatitude() + ", " + model.getLongitude() + ")";
        final String type = model.getType();
        final String proof = model.getProof();
        final String proofOriginal = model.getProofOriginal();
        final String description = model.getDescription();

        holder.reportDate.setText(date);
        holder.reportLocation.setText(location);

        holder.reportDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent detailDateIntent = new Intent(context, ReportDetailActivity.class);
                detailDateIntent.putExtra("date", date);
                detailDateIntent.putExtra("location", location);
                detailDateIntent.putExtra("type", type);
                detailDateIntent.putExtra("proof", proof);
                detailDateIntent.putExtra("proofOriginal", proofOriginal);
                detailDateIntent.putExtra("description", description);
                context.startActivity(detailDateIntent);

            }
        });

        holder.reportLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent detailLocationIntent = new Intent(context, ReportDetailActivity.class);
                detailLocationIntent.putExtra("date", date);
                detailLocationIntent.putExtra("location", location);
                detailLocationIntent.putExtra("type", type);
                detailLocationIntent.putExtra("proof", proof);
                detailLocationIntent.putExtra("proofOriginal", proofOriginal);
                detailLocationIntent.putExtra("description", description);
                context.startActivity(detailLocationIntent);

            }
        });

    }

    @NonNull
    @Override
    public ReportAdapter.ReportHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_report, parent, false);

        return new ReportAdapter.ReportHolder(view);

    }

    class ReportHolder extends RecyclerView.ViewHolder {

        TextView reportDate, reportLocation;

        public ReportHolder(@NonNull View itemView) {

            super(itemView);

            reportDate = itemView.findViewById(R.id.singleReportDate);
            reportLocation = itemView.findViewById(R.id.singleReportLocation);

        }

    }

}
