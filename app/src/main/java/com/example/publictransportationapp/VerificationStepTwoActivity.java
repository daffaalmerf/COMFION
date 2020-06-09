package com.example.publictransportationapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class VerificationStepTwoActivity extends AppCompatActivity {

    ImageView verificationPicOne;
    Uri picOneUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_step_two);

        final String number = getIntent().getStringExtra("number");

        verificationPicOne = findViewById(R.id.verificationPicOne);

        verificationPicOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setMinCropResultSize(256, 256)
                        .start(VerificationStepTwoActivity.this);

            }
        });

        Button proceedStepTwo = findViewById(R.id.verificationTwoProceed);

        proceedStepTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(picOneUri == null){

                    Toast.makeText(VerificationStepTwoActivity.this, "Select A Picture", Toast.LENGTH_SHORT).show();

                } else {

                    final CustomDialog customDialog = new CustomDialog(VerificationStepTwoActivity.this);

                    customDialog.startDialog();

                    final String uriOne = picOneUri.toString();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            Intent proceedTwo = new Intent(VerificationStepTwoActivity.this, VerificationStepThreeActivity.class);
                            proceedTwo.putExtra("number", number);
                            proceedTwo.putExtra("uriOne", uriOne);
                            customDialog.dismissDialog();
                            startActivity(proceedTwo);
                            finish();

                        }
                    }, 3000);

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

                picOneUri = uri;

                verificationPicOne.setImageURI(uri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){

                Exception error = result.getError();

                Toast.makeText(VerificationStepTwoActivity.this, "Error Picking Photo", Toast.LENGTH_SHORT).show();

            }

        }
    }

}
