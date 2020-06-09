package com.example.publictransportationapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class ProfileActivity extends AppCompatActivity {

    private static final int GALLERY_PICK = 1;
    FirebaseAuth mAuth;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final CircleImageView profilePicture = findViewById(R.id.profileImage);
        final TextView profileName = findViewById(R.id.profileName);
        final TextView profilePassword = findViewById(R.id.profilePassword);
        final TextView profileChangePicture = findViewById(R.id.profilePicture);
        final TextView profileIdentity = findViewById(R.id.profileIdentity);

        mAuth = FirebaseAuth.getInstance();

        uid = mAuth.getUid();

        final DatabaseReference mProfileDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

        mProfileDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("username").getValue().toString();
                final String email = dataSnapshot.child("e-mail").getValue().toString();
                final String status = dataSnapshot.child("status").getValue().toString();
                String picture = dataSnapshot.child("thumbnail").getValue().toString();

                if(picture == "default"){
                    Picasso.get().load(R.color.colorAccent).into(profilePicture);
                } else {
                    Picasso.get().load(picture).placeholder(R.color.colorAccent).into(profilePicture);
                }

                profileName.setText(name);

                profilePassword.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if(task.isSuccessful()){

                                    Toast.makeText(ProfileActivity.this, "Password Reset Has Been Sent To Your E-mail", Toast.LENGTH_SHORT).show();

                                } else {

                                    Toast.makeText(ProfileActivity.this, "Password Reset Failed", Toast.LENGTH_SHORT).show();

                                }

                            }
                        });

                    }
                });

                profileChangePicture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent galleryIntent = new Intent();
                        galleryIntent.setType("image/*");
                        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                        startActivityForResult(Intent.createChooser(galleryIntent, "Select Image"), GALLERY_PICK);

                    }
                });

                profileIdentity.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(status.equals("verified")){

                            Toast.makeText(ProfileActivity.this, "Your Account Is Already Verified", Toast.LENGTH_SHORT).show();

                        } else if (status.equals("pending")){

                            Toast.makeText(ProfileActivity.this, "Your Verification Request Is Pending", Toast.LENGTH_SHORT).show();

                        } else {

                            Intent verificationIntent = new Intent(ProfileActivity.this, VerificationStepOneActivity.class);
                            startActivity(verificationIntent);
                            finish();

                        }

                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        final DatabaseReference mUserProfile = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {

            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .setMinCropWindowSize(500, 500)
                    .start(ProfileActivity.this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            final CustomDialog customDialog = new CustomDialog(ProfileActivity.this);

            if (resultCode == RESULT_OK) {

                customDialog.startDialog();

                Uri resultUri = result.getUri();

                File thumb_file = new File(resultUri.getPath());

                Bitmap thumb_bitmap = null;
                try {
                    thumb_bitmap = new Compressor(ProfileActivity.this)
                            .setMaxWidth(280)
                            .setMaxHeight(280)
                            .setQuality(75)
                            .compressToBitmap(thumb_file);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                final byte[] thumb_byte = baos.toByteArray();

                StorageReference mStorageProfilePicture = FirebaseStorage.getInstance().getReference();

                StorageReference filepath = mStorageProfilePicture.child("profile_pictures").child(uid + ".png");
                final StorageReference thumb_filepath = mStorageProfilePicture.child("profile_thumbs").child(uid + ".png");
                StorageTask<UploadTask.TaskSnapshot> taskSnapshotStorageTask = filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {

                            UploadTask uploadTask = thumb_filepath.putBytes(thumb_byte);
                            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {

                                            String downloadthumbUri = uri.toString();

                                            Map<String, Object> updateHashMap = new HashMap<>();
                                            updateHashMap.put("thumbnail", downloadthumbUri);
                                            mUserProfile.updateChildren(updateHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {

                                                        customDialog.dismissDialog();

                                                        Toast.makeText(ProfileActivity.this, "Upload Successful", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                        }
                                    });
                                }
                            });

                        } else {

                            customDialog.dismissDialog();

                            Toast.makeText(ProfileActivity.this, "Error Uploading Picture", Toast.LENGTH_LONG).show();
                        }

                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }

        }
    }

}
