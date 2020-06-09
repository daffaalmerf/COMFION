package com.example.publictransportationapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import id.zelory.compressor.Compressor;

public class VerificationStepThreeActivity extends AppCompatActivity {

    ImageView verificationPicTwo;
    Uri picTwoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_step_three);

        final String number = getIntent().getStringExtra("number");
        final String uriOne = getIntent().getStringExtra("uriOne");

        final Uri uriOneParse = Uri.parse(uriOne);

        verificationPicTwo = findViewById(R.id.verificationPicTwo);

        verificationPicTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setMinCropResultSize(256, 256)
                        .start(VerificationStepThreeActivity.this);

            }
        });

        Button verificationSend = findViewById(R.id.verificationSend);

        verificationSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (picTwoUri == null) {

                    Toast.makeText(VerificationStepThreeActivity.this, "Select A Picture", Toast.LENGTH_SHORT).show();

                } else {

                    final CustomDialog customDialog = new CustomDialog(VerificationStepThreeActivity.this);

                    customDialog.startDialog();

                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    final String uid = mAuth.getUid();
                    final StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                    final StorageReference picPathOne = storageReference.child("verificationPictures/" + uid).child(FieldValue.serverTimestamp() + "-1.png");
                    final StorageReference picPathTwo = storageReference.child("verificationPictures/" + uid).child(FieldValue.serverTimestamp() + "-2.png");
                    picPathOne.putFile(uriOneParse).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                            if(task.isSuccessful()){

                                picPathOne.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {

                                        final String urlOne = uri.toString();


                                        picPathTwo.putFile(picTwoUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                                if(task.isSuccessful()){

                                                    picPathTwo.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                        @Override
                                                        public void onSuccess(Uri uri) {

                                                            String urlTwo = uri.toString();

                                                            FirebaseFirestore mFirestoreVerification = FirebaseFirestore.getInstance();

                                                            FirebaseDatabase mDatabaseVerification = FirebaseDatabase.getInstance();

                                                            mDatabaseVerification.getReference("Users").child(uid).child("status").setValue("pending");

                                                            final Map<String, Object> verification = new HashMap<>();
                                                            verification.put("urlOne", urlOne);
                                                            verification.put("urlTwo", urlTwo);
                                                            verification.put("number", number);
                                                            verification.put("timestamp", FieldValue.serverTimestamp());
                                                            verification.put("user", uid);

                                                            mFirestoreVerification.collection("Pending Verification/" + uid + "/Details").add(verification).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<DocumentReference> task) {

                                                                    if(task.isSuccessful()){

                                                                        customDialog.dismissDialog();

                                                                        Toast.makeText(VerificationStepThreeActivity.this, "Verification Request Has Been Sent", Toast.LENGTH_LONG).show();

                                                                        Intent successIntent = new Intent(VerificationStepThreeActivity.this, MainActivity.class);
                                                                        startActivity(successIntent);
                                                                        finish();

                                                                    }

                                                                }
                                                            });


                                                        }
                                                    });


                                                }

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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode == RESULT_OK){

                Uri uri = result.getUri();

                picTwoUri = uri;

                verificationPicTwo.setImageURI(uri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){

                Exception error = result.getError();

                Toast.makeText(VerificationStepThreeActivity.this, "Error Picking Photo", Toast.LENGTH_SHORT).show();

            }

        }
    }
}
