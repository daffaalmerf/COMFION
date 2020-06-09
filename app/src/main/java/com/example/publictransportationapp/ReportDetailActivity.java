package com.example.publictransportationapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class ReportDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_detail);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        String date = getIntent().getStringExtra("date");
        String location = getIntent().getStringExtra("location");
        String type = getIntent().getStringExtra("type");
        String proof = getIntent().getStringExtra("proof");
        final String proofOriginal = getIntent().getStringExtra("proofOriginal");
        String description = getIntent().getStringExtra("description");

        TextView reportType = findViewById(R.id.reportTypeDetail);
        TextView reportLocation = findViewById(R.id.reportLocationDetail);
        TextView reportDate = findViewById(R.id.reportDateDetail);
        TextView reportDescription = findViewById(R.id.reportDescriptionDetail);
        ImageView reportProof = findViewById(R.id.reportProofDetail);

        reportType.setText(type);
        reportLocation.setText(location);
        reportDate.setText(date);
        reportDescription.setText(description);
        Picasso.get().load(proof).into(reportProof);

        reportProof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent viewIntent = new Intent(ReportDetailActivity.this, ViewImageActivity.class);
                viewIntent.putExtra("proofOriginal", proofOriginal);
                startActivity(viewIntent);
                finish();

            }
        });

    }
}
