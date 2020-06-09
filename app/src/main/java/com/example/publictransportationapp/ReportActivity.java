package com.example.publictransportationapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageActivity;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import id.zelory.compressor.Compressor;

public class ReportActivity extends AppCompatActivity {

    private ImageView photoReport;
    private Uri photoReportUri;
    private Bitmap compressedProofFile;

    private String[] crimes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        final FirebaseFirestore mReportFirestore = FirebaseFirestore.getInstance();

        final EditText locationReport = findViewById(R.id.reportLocation);
        final EditText descReport = findViewById(R.id.reportDescription);

        final String getLocation = getIntent().getStringExtra("address");
        final String latitude = getIntent().getStringExtra("latitude");
        final String longitude = getIntent().getStringExtra("longitude");
        final String descriptionReceived = getIntent().getStringExtra("description");

        locationReport.setText(getLocation);

        photoReport = findViewById(R.id.reportProof);

        photoReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CropImage.activity()
                        .setMinCropResultSize(100,100)
                        .setMaxCropResultSize(1000,1000)
                        .start(ReportActivity.this);

            }
        });

        final Button sendReport = findViewById(R.id.reportSend);

        final Spinner crime = findViewById(R.id.reportType);

        crimes = getResources().getStringArray(R.array.crimeList);

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, crimes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        crime.setAdapter(adapter);

        crime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                final String selectedCrime = crimes[position];

                locationReport.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String descriptionTemp = descReport.getText().toString();

                        Intent mapIntent = new Intent(ReportActivity.this, MapActivity.class);
                        mapIntent.putExtra("crime", selectedCrime);
                        mapIntent.putExtra("description", descriptionTemp);
                        startActivity(mapIntent);

                    }
                });

                descReport.setText(descriptionReceived);

                sendReport.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final String description = descReport.getText().toString();

                        if(selectedCrime == null){

                            Toast.makeText(ReportActivity.this, "Pick A Report Type", Toast.LENGTH_SHORT).show();

                        }

                        if(TextUtils.isEmpty(getLocation) || getLocation.length() < 10 || getLocation.length() > 150 || !(getLocation.contains("Indonesia"))){

                            locationReport.setError("Invalid Location");

                        }

                        if(photoReportUri == null){

                            Toast.makeText(ReportActivity.this, "Invalid Proof", Toast.LENGTH_SHORT).show();

                        }

                        if(TextUtils.isEmpty(description) || description.length() < 10 || description.length() > 150){

                            descReport.setError("Invalid Description");

                        }

                        if(selectedCrime != null && !TextUtils.isEmpty(getLocation) && getLocation.length() >= 10 && getLocation.length() <= 100 && getLocation.contains("Indonesia") && photoReportUri != null && !TextUtils.isEmpty(description) && description.length() >= 10 && description.length() <= 150){

                            final CustomDialog customDialog = new CustomDialog(ReportActivity.this);

                            customDialog.startDialog();

                            FirebaseAuth mAuth = FirebaseAuth.getInstance();
                            final String uid = mAuth.getUid();
                            final StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                            final StorageReference proofPath = storageReference.child("proofImages").child(FieldValue.serverTimestamp() + ".png");
                            proofPath.putFile(photoReportUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                    if(task.isSuccessful()){

                                        proofPath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {

                                                final String proofOriginal = uri.toString();

                                                File newProofFile = new File(Objects.requireNonNull(photoReportUri.getPath()));

                                                try{
                                                    compressedProofFile = new Compressor(ReportActivity.this)
                                                            .setMaxHeight(100)
                                                            .setMaxWidth(100)
                                                            .setQuality(1)
                                                            .compressToBitmap(newProofFile);

                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }

                                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                                compressedProofFile.compress(Bitmap.CompressFormat.PNG, 85, baos);
                                                final byte[] thumb_data = baos.toByteArray();

                                                UploadTask uploadTask = storageReference.child("proofImages/thumbs").child(FieldValue.serverTimestamp() + ".png")
                                                        .putBytes(thumb_data);


                                                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                            @Override
                                                            public void onSuccess(Uri uri) {

                                                                String downloadProofThumb = uri.toString();

                                                                final Map<String, Object> report = new HashMap<>();
                                                                report.put("location", getLocation);
                                                                report.put("latitude", latitude);
                                                                report.put("longitude", longitude);
                                                                report.put("type", selectedCrime);
                                                                report.put("proof", downloadProofThumb);
                                                                report.put("proofOriginal", proofOriginal);
                                                                report.put("description", description);
                                                                report.put("timestamp", FieldValue.serverTimestamp());
                                                                report.put("user", uid);

                                                                mReportFirestore.collection("Report/" + selectedCrime + "/History").add(report).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<DocumentReference> task) {

                                                                        if(task.isSuccessful()) {

                                                                            Toast.makeText(ReportActivity.this, "Processing", Toast.LENGTH_SHORT).show();

                                                                            mReportFirestore.collection("Users History/" + uid + "/Report History").add(report).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<DocumentReference> task) {

                                                                                    if(task.isSuccessful()){

                                                                                        customDialog.dismissDialog();

                                                                                        Toast.makeText(ReportActivity.this, "Report Has Been Sent", Toast.LENGTH_SHORT).show();
                                                                                        Intent success = new Intent(ReportActivity.this, MainActivity.class);
                                                                                        startActivity(success);
                                                                                        finish();

                                                                                    } else {

                                                                                        customDialog.dismissDialog();

                                                                                        Toast.makeText(ReportActivity.this, "Failed To Send Report", Toast.LENGTH_SHORT).show();

                                                                                    }

                                                                                }
                                                                            });

                                                                        } else {

                                                                            customDialog.dismissDialog();

                                                                            Toast.makeText(ReportActivity.this, "Failed To Send Report", Toast.LENGTH_SHORT).show();

                                                                        }
                                                                    }
                                                                });


                                                            }
                                                        });

                                                    }
                                                });

                                            }
                                        });

                                        }

                                    }

                            });

                        }

                    }
                });

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                photoReportUri = result.getUri();

                photoReport.setImageURI(photoReportUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

                Toast.makeText(ReportActivity.this, "Error Picking Photo", Toast.LENGTH_SHORT).show();

            }

        }
    }

}
