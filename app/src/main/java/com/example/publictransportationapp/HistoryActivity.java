package com.example.publictransportationapp;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.publictransportationapp.adapter.ReportAdapter;
import com.example.publictransportationapp.adapter.ScanAdapter;
import com.example.publictransportationapp.model.ReportModel;
import com.example.publictransportationapp.model.ScanModel;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class HistoryActivity extends AppCompatActivity {

    private String[] history;
    private ReportAdapter reportAdapter;
    private ScanAdapter scanAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        String uid = mAuth.getUid();

        FirebaseFirestore historyFirestore = FirebaseFirestore.getInstance();

        final RecyclerView historyList = findViewById(R.id.historyList);

        final Query report = historyFirestore.collection("Users History").document(uid).collection("Report History");
        final Query trip = historyFirestore.collection("Users History").document(uid).collection("Trip History");

        FirestoreRecyclerOptions<ReportModel> options = new FirestoreRecyclerOptions.Builder<ReportModel>().setQuery(report, ReportModel.class).build();

        reportAdapter = new ReportAdapter(options, HistoryActivity.this);

        FirestoreRecyclerOptions<ScanModel> option = new FirestoreRecyclerOptions.Builder<ScanModel>().setQuery(trip, ScanModel.class).build();

        scanAdapter = new ScanAdapter(option);

        final Spinner historySpinner = findViewById(R.id.historyType);

        history = getResources().getStringArray(R.array.historyList);

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, history);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        historySpinner.setAdapter(adapter);

        historySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                final String selectedHistory = history[position];

                if(selectedHistory.equals("Report History")){

                    historyList.setLayoutManager(new LinearLayoutManager(HistoryActivity.this));
                    historyList.setAdapter(reportAdapter);

                } else {

                    historyList.setLayoutManager(new LinearLayoutManager(HistoryActivity.this));
                    historyList.setAdapter(scanAdapter);

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        reportAdapter.startListening();
        scanAdapter.startListening();

    }

    @Override
    public void onStop() {
        super.onStop();
        reportAdapter.stopListening();
        scanAdapter.stopListening();
    }

}
