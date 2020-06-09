package com.example.publictransportationapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TransactionCodeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_code);

        String code = getIntent().getStringExtra("code");

        TextView transactionCode = findViewById(R.id.transactionCode);
        Button transactionReturn = findViewById(R.id.transactionReturn);

        transactionCode.setText(code);

        transactionReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent returnIntent = new Intent(TransactionCodeActivity.this, MainActivity.class);
                startActivity(returnIntent);
                finish();

            }
        });

    }
}
