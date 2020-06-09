package com.example.publictransportationapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class VerificationStepOneActivity extends AppCompatActivity {

    private Uri picOne, picTwo;
    private ImageView verificationPicOne, verificationPicTwo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_step_one);

        final EditText verificationNumber = findViewById(R.id.verificationNumber);

        Button proceedStepOne = findViewById(R.id.verificationOneProceed);

        proceedStepOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String number = verificationNumber.getText().toString();

                String regex = "^[0-9]+$";

                if(TextUtils.isEmpty(number) || number.length() != 16){

                    Toast.makeText(VerificationStepOneActivity.this, "Input a Valid Identity Number", Toast.LENGTH_SHORT).show();

                } else if(!(number.matches(regex))){

                    Toast.makeText(VerificationStepOneActivity.this, "Only Accepts Numeric Characters", Toast.LENGTH_SHORT).show();

                }

                if(!TextUtils.isEmpty(number) && number.length() == 16 && number.matches(regex)){

                    final CustomDialog customDialog = new CustomDialog(VerificationStepOneActivity.this);

                    customDialog.startDialog();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            Intent proceedOne = new Intent(VerificationStepOneActivity.this, VerificationStepTwoActivity.class);
                            proceedOne.putExtra("number", number);
                            customDialog.dismissDialog();
                            startActivity(proceedOne);
                            finish();

                        }
                    }, 3000);

                }

            }
        });

    }

}