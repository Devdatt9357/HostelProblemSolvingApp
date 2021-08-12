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

public class FeedbackActivity extends AppCompatActivity {
    private Button back,submit;
    private EditText feedback;
    private Boolean isConnected;
    FirebaseAuth mAuth;
    DatabaseReference mRef,nRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.colorAccent2));

        submit=findViewById(R.id.button14);
        back=findViewById(R.id.button13);
        feedback=findViewById(R.id.editText10);

        mAuth=FirebaseAuth.getInstance();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (feedback.getText().toString().isEmpty()) {
                    Toast.makeText(FeedbackActivity.this, "Please write something in oreder to send feedback.", Toast.LENGTH_SHORT).show();
                } else {
                    isConnected = ConnectivityReceiver.isConnected();
                    if (isConnected) {
                        final FirebaseDatabase mBase = FirebaseDatabase.getInstance();
                        mRef = mBase.getReference("Data from user").child("Feedback").child(mAuth.getUid());
                        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                   String s = snapshot.getValue().toString();
                                    nRef = mBase.getReference("Data from user").child("Feedback").child(mAuth.getUid());
                                    nRef.setValue(s + "\n" + feedback.getText().toString());

                                } else {
                                    nRef = mBase.getReference("Data from user").child("Feedback").child(mAuth.getUid());
                                    nRef.setValue(feedback.getText().toString());
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        Toast.makeText(FeedbackActivity.this, "Thank you for your valuable feedback.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(FeedbackActivity.this, HomeActivity.class));
                        finish();
                    } else {
                        startActivity(new Intent(FeedbackActivity.this, OfflineActivity.class));
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
