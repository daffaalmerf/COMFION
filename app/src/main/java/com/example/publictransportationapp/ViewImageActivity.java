package com.example.publictransportationapp;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

public class ViewImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        ImageView viewImage = findViewById(R.id.view_image);

        String proof = getIntent().getStringExtra("proofOriginal");

        Picasso.get().load(proof).into(viewImage);

    }
}
