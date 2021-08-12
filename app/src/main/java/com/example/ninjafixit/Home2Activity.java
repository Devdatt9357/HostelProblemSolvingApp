package com.example.ninjafixit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Home2Activity extends AppCompatActivity {

    private static final String TAG ="data check" ;
    FirebaseAuth mAuth;
    boolean isConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home2);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        isConnected = ConnectivityReceiver.isConnected();
        if (!isConnected) {
            startActivity(new Intent(Home2Activity.this, OfflineActivity.class));
        } else {
            mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();
            if (user == null) {
                startActivity(new Intent(Home2Activity.this, MainActivity.class));
            } else {
                FirebaseDatabase mbase = FirebaseDatabase.getInstance();
                DatabaseReference mRef = mbase.getReference("Users").child(mAuth.getUid());
                Log.d(TAG,"before retrieving data");
                mRef.addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.d(TAG,"during retrieving data");
                        if (!snapshot.child("Name").exists()) {
                            Toast.makeText(Home2Activity.this, "We haven't got your name. Please enter your name",
                                    Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Home2Activity.this, SignUp2Activity.class));
                        } else if (!snapshot.child("Registration number").exists()) {
                            Toast.makeText(Home2Activity.this, "You need to add your registration number",
                                    Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Home2Activity.this, WelcomeActivity.class));
                        } else {
                            startActivity(new Intent(Home2Activity.this, HomeActivity.class));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
                Log.d(TAG,"after retrieving data");
            }
        }
    }


}
