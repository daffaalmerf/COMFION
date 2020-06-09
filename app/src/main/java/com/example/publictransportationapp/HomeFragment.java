package com.example.publictransportationapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.concurrent.Delayed;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View homeView = inflater.inflate(R.layout.fragment_home, container, false);

        final FirebaseAuth mAuth = FirebaseAuth.getInstance();

        final String uid = mAuth.getUid();

        if (uid == null) {

            Intent loginIntent = new Intent(getContext(), LoginActivity.class);
            startActivity(loginIntent);
            getActivity().finish();

        } else {

            final FloatingActionButton fabReport = homeView.findViewById(R.id.fab_report);
            final FloatingActionButton fabHistory = homeView.findViewById(R.id.fab_history);
            final TextView mName = homeView.findViewById(R.id.profileName);
            final CircleImageView mPicture = homeView.findViewById(R.id.profileHomeImage);
            TextView mScan = homeView.findViewById(R.id.profileScan);

            mScan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    boolean emailVerified = mAuth.getCurrentUser().isEmailVerified();

                    if(emailVerified) {

                        Intent scanIntent = new Intent(getContext(), ScanActivity.class);
                        startActivity(scanIntent);

                    } else {

                        Toast.makeText(getContext(), "Verify Your E-mail Address Through E-mail COMFION Sent. Re-login After Verification.", Toast.LENGTH_LONG).show();

                    }

                }
            });

            fabReport.setTitle("Send Report");
            fabHistory.setTitle("History");

            DatabaseReference mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

            mUserDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    final String name = dataSnapshot.child("username").getValue().toString();
                    final String greetings = "Hey, " + name + "!";
                    final String picture = dataSnapshot.child("thumbnail").getValue().toString();
                    final String status = dataSnapshot.child("status").getValue().toString();

                    if (picture == "default") {
                        Picasso.get().load(R.color.colorAccent).into(mPicture);
                    } else {
                        Picasso.get().load(picture).placeholder(R.color.colorAccent).into(mPicture);
                    }

                    mName.setText(greetings);

                    fabReport.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (status.equals("verified")) {

                                Intent reportIntent = new Intent(getContext(), ReportActivity.class);
                                startActivity(reportIntent);

                            } else {

                                Toast.makeText(getContext(), "Account Is Not Verified", Toast.LENGTH_SHORT).show();

                            }

                        }
                    });

                    fabHistory.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent historyIntent = new Intent(getContext(), HistoryActivity.class);
                            startActivity(historyIntent);

                        }
                    });


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }
            return homeView;
        }
}
