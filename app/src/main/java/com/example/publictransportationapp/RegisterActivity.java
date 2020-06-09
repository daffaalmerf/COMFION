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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText registerName = findViewById(R.id.registerName);
        final EditText registerEmail = findViewById(R.id.registerEmail);
        final EditText registerPassword = findViewById(R.id.registerPassword);
        final EditText registerConfirm = findViewById(R.id.registerConfirmPassword);

        final FirebaseAuth mAuth = FirebaseAuth.getInstance();

        Button registerButton = findViewById(R.id.registerButton);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final CustomDialog customDialog = new CustomDialog(RegisterActivity.this);

                final String name = registerName.getText().toString();
                final String email = registerEmail.getText().toString();
                final String password = registerPassword.getText().toString();
                final String confirm = registerConfirm.getText().toString();

                DatabaseReference emailRef = FirebaseDatabase.getInstance().getReference("Users");
                Query emailQuery = emailRef.orderByChild("e-mail").equalTo(email);

                final String regexName = "^[a-zA-z ]+$";

                final String regexEmail = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+(?:\\.[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+↵\n" +
                        ")*@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$";

                if(TextUtils.isEmpty(name) || name.length() > 100){

                    registerName.setError("Invalid Name");

                } else if(!(name.matches(regexName))){


                    registerName.setError("Only Accepts Alphabetic Characters");

                }

                if(TextUtils.isEmpty(email) || email.length() < 3 || email.length() > 100 || !(email.matches(regexEmail))){

                    registerEmail.setError("Invalid E-mail");

                }

                if(TextUtils.isEmpty(password) || password.length() < 8 || password.length() > 16){

                    registerPassword.setError("Invalid Password");

                }

                if(TextUtils.isEmpty(confirm) || !(confirm.equals(password)) || confirm.length() < 8 || confirm.length() > 16){

                    registerConfirm.setError("Invalid Password Confirmation");

                }

                if(!TextUtils.isEmpty(name) && name.length() <= 100 && name.matches(regexName) && !TextUtils.isEmpty(email) && email.length() >= 3 &&
                        email.length() <= 100 && email.matches(regexEmail) && !TextUtils.isEmpty(password) && password.length() >= 8 &&
                        password.length() <= 16 && !TextUtils.isEmpty(confirm) && confirm.equals(password) && confirm.length() >= 8 && confirm.length() <= 16){

                    customDialog.startDialog();

                    emailQuery.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.exists()){

                                customDialog.dismissDialog();

                                registerEmail.setError("E-mail is Already Registered");

                            } else {


                                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {

                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                        final String uid = user.getUid();

                                        DatabaseReference mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                                        HashMap<String, String> userMap = new HashMap<>();
                                        userMap.put("username", name);
                                        userMap.put("thumbnail", "default");
                                        userMap.put("e-mail", email);
                                        userMap.put("status", "unverified");

                                        mUserDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if(task.isSuccessful()){

                                                    customDialog.dismissDialog();

                                                    Objects.requireNonNull(mAuth.getCurrentUser()).sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {

                                                        }
                                                    });

                                                    Toast.makeText(RegisterActivity.this, "Account is Successfully Registered", Toast.LENGTH_SHORT).show();

                                                    Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                                                    startActivity(loginIntent);
                                                    finish();

                                                } else {

                                                    customDialog.dismissDialog();

                                                    Toast.makeText(RegisterActivity.this, "Failed To Register Account", Toast.LENGTH_SHORT).show();

                                                }

                                            }
                                        });

                                    }
                                });

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
