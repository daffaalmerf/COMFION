package com.example.publictransportationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.rpc.Code;
import com.google.zxing.Result;

import java.util.HashMap;
import java.util.Map;

public class ScanActivity extends AppCompatActivity {

    CodeScanner codeScanner;
    CodeScannerView codeScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        codeScannerView = findViewById(R.id.scannerView);
        codeScanner = new CodeScanner(ScanActivity.this, codeScannerView);

        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        final CustomDialog customDialog = new CustomDialog(ScanActivity.this);

                        customDialog.startDialog();

                        final String code = result.getText();

                        FirebaseAuth mAuth = FirebaseAuth.getInstance();

                        final String uid = mAuth.getUid();

                        final FirebaseFirestore mScanFirestore = FirebaseFirestore.getInstance();

                        DatabaseReference scanRef = FirebaseDatabase.getInstance().getReference("QR Code");

                        final Query scanQuery = scanRef.orderByChild("code").equalTo(code);

                        scanQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if(dataSnapshot.exists()){

                                    final Map<String, Object> scan = new HashMap<>();
                                    scan.put("code", code);
                                    scan.put("timestamp", FieldValue.serverTimestamp());
                                    scan.put("user", uid);

                                    mScanFirestore.collection("Users History/" + uid + "/Trip History").add(scan).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentReference> task) {

                                            if(task.isSuccessful()){

                                                customDialog.dismissDialog();

                                                Intent transactionIntent = new Intent(ScanActivity.this, TransactionCodeActivity.class);
                                                transactionIntent.putExtra("code", code);
                                                startActivity(transactionIntent);
                                                finish();


                                            } else {

                                                customDialog.dismissDialog();

                                                Toast.makeText(ScanActivity.this, "Failed To Scan QR Code", Toast.LENGTH_SHORT).show();

                                            }

                                        }
                                    });

                                } else {

                                    customDialog.dismissDialog();

                                    Toast.makeText(ScanActivity.this, "Invalid Code", Toast.LENGTH_SHORT).show();

                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });



                    }
                });

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        requestCamera();
    }

    private void requestCamera(){

        if(ContextCompat.checkSelfPermission(ScanActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){

            Toast.makeText(ScanActivity.this, "Ready To Scan", Toast.LENGTH_SHORT).show();
            codeScanner.startPreview();


        } else {

            ActivityCompat.requestPermissions(ScanActivity.this, new String[]{Manifest.permission.CAMERA}, 1888);

        }


    }

}
