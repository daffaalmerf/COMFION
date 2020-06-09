package com.example.publictransportationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginButton = findViewById(R.id.loginButton);
        TextView loginRegister = findViewById(R.id.loginRegister);
        TextView loginForgotPassword = findViewById(R.id.loginForgot);

        final EditText emailText = findViewById(R.id.loginEmail);
        final EditText passwordText = findViewById(R.id.loginPassword);

        final FirebaseAuth mAuth = FirebaseAuth.getInstance();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String regexEmail = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+(?:\\.[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+↵\n" +
                        ")*@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$";

                String email = emailText.getText().toString();
                String password = passwordText.getText().toString();

                if(TextUtils.isEmpty(email) || email.length() < 3 || email.length() > 100 || !(email.matches(regexEmail))){

                    emailText.setError("Invalid E-mail");

                }

                if(TextUtils.isEmpty(password) || password.length() < 8 || password.length() > 16){

                    passwordText.setError("Invalid Password");

                }

                if(!TextUtils.isEmpty(email) && email.matches(regexEmail) && !TextUtils.isEmpty(password) && password.length() >= 8 && password.length() <= 16){

                    final CustomDialog customDialog = new CustomDialog(LoginActivity.this);
                    customDialog.startDialog();

                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){

                                if(ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

                                    customDialog.dismissDialog();

                                    Intent loginIntent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(loginIntent);
                                    finish();

                                } else {

                                    customDialog.dismissDialog();

                                    ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);

                                }

                            } else {

                                customDialog.dismissDialog();

                                Toast.makeText(LoginActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();

                            }

                        }
                    });

                }

            }
        });

        loginRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(registerIntent);

            }
        });

        loginForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent forgotIntent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(forgotIntent);

            }
        });

    }
}
