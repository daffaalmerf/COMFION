package com.example.publictransportationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ForgotPasswordActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        final EditText forgotEmail = findViewById(R.id.forgotPasswordEmail);

        Button forgotButton = findViewById(R.id.forgotPasswordSend);

        forgotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email = forgotEmail.getText().toString();

                String regexEmail = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+(?:\\.[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+↵\n" +
                        ")*@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$";

                if(TextUtils.isEmpty(email) || !(email.matches(regexEmail)) || email.length() < 3 || email.length() > 100){

                    forgotEmail.setError("Invalid E-mail");

                } else {

                    final CustomDialog customDialog = new CustomDialog(ForgotPasswordActivity.this);

                    customDialog.startDialog();

                    DatabaseReference emailRef = FirebaseDatabase.getInstance().getReference("Users");
                    Query emailQuery = emailRef.orderByChild("e-mail").equalTo(email);

                    emailQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if(dataSnapshot.exists()){

                                FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if(task.isSuccessful()){

                                            customDialog.dismissDialog();

                                            Toast.makeText(ForgotPasswordActivity.this, "Password Reset Has Been Sent to Your E-mail", Toast.LENGTH_SHORT).show();
                                            Intent returnLogin = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                                            startActivity(returnLogin);
                                            finish();

                                        } else {

                                            customDialog.dismissDialog();

                                            Toast.makeText(ForgotPasswordActivity.this, "Password Reset Failed", Toast.LENGTH_SHORT).show();

                                        }

                                    }
                                });

                            } else {

                                customDialog.dismissDialog();

                                forgotEmail.setError("E-mail is Not Registered");

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });



                }

            }
        });

    }
}
