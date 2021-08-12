package com.example.ninjafixit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BugActivity extends AppCompatActivity {
    private Button back,submit;
    private EditText feedback;
    private Boolean isConnected;
    FirebaseAuth mAuth;
    DatabaseReference mRef,nRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bug);
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.colorAccent2));

        submit=findViewById(R.id.button15);
        back=findViewById(R.id.button16);
        feedback=findViewById(R.id.editText12);

        mAuth=FirebaseAuth.getInstance();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (feedback.getText().toString().isEmpty()) {
                    Toast.makeText(BugActivity.this, "Please write something in oreder to notify us bugs in this app.", Toast.LENGTH_LONG).show();
                } else {
                    isConnected = ConnectivityReceiver.isConnected();
                    if (isConnected) {
                        final FirebaseDatabase mBase = FirebaseDatabase.getInstance();
                        mRef = mBase.getReference("Data from user").child("Bugs").child(mAuth.getUid());
                        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    String s = snapshot.getValue().toString();
                                    nRef = mBase.getReference("Data from user").child("Bugs").child(mAuth.getUid());
                                    nRef.setValue(s + "\n" + feedback.getText().toString());

                                } else {
                                    nRef = mBase.getReference("Data from user").child("Bugs").child(mAuth.getUid());
                                    nRef.setValue(feedback.getText().toString());
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        Toast.makeText(BugActivity.this, "Your contribution towards improvement of this app will be always remembered.", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(BugActivity.this, HomeActivity.class));
                        finish();
                    } else {
                        startActivity(new Intent(BugActivity.this, OfflineActivity.class));
                    }
                }
            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
