package com.example.ninjafixit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RecyclerlistListenerActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    private StringBuffer sb = null;
    private TextView head, headInv;
    private Button back, submit;
    private String heading, headingInv;
    private CheckAdapter adapter;
    private ArrayList<Messages> mArrayList;
    private DatabaseReference mRef,nRef;
    private Boolean isConnected;
    private RelativeLayout progressbar;
    private EditText description;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyclerlist_listener);

        progressbar=findViewById(R.id.progress_layout9);

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorAccent2));

        isConnected = ConnectivityReceiver.isConnected();
        if (!isConnected) {
            startActivity(new Intent(RecyclerlistListenerActivity.this, OfflineActivity.class));
        }

        head = findViewById(R.id.textView82);
        back = findViewById(R.id.button21);
        headInv = findViewById(R.id.textView83);
        submit = findViewById(R.id.button22);
        description=findViewById(R.id.editTextTextMultiLine);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            heading = extras.getString("SubheadingName");
            headingInv = extras.getString("head");
        }
        head.setText(heading);
        headInv.setText(headingInv);

        mAuth=FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference("App data").child("Sub topics");
        nRef = FirebaseDatabase.getInstance().getReference("Data from user").child("Service requests").child(headInv.getText().toString()).child(head.getText().toString()).child(mAuth.getUid());


        recyclerView = findViewById(R.id.recyclerView2);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isConnected = ConnectivityReceiver.isConnected();
                if (isConnected) {
                    sb = new StringBuffer();
                    for (Messages m : adapter.checkedList) {
                        sb.append(m.getCheckItem());
                        sb.append("\n");
                    }
                    if(description.getText().toString().isEmpty()&&adapter.checkedList.size()==0){
                        Toast.makeText(RecyclerlistListenerActivity.this, "Please help us describing the situation briefly.", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        nRef.setValue(sb.toString()+description.getText().toString());
                        Toast.makeText(RecyclerlistListenerActivity.this, "The issue has been submitted successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RecyclerlistListenerActivity.this, HomeActivity.class));
                        finish();
                    }

                } else {
                    startActivity(new Intent(RecyclerlistListenerActivity.this, OfflineActivity.class));
                }
            }

        });


        mArrayList = new ArrayList<>();
        ClearAll();
        getDatafromFirebase();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getDatafromFirebase() {
        showProgress();
        Query query = mRef.child(headInv.getText().toString()).child(head.getText().toString());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ClearAll();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Messages messages = new Messages();
                        messages.setCheckItem(dataSnapshot.child("1").getValue().toString());
                        mArrayList.add(messages);
                    }
                    adapter = new CheckAdapter(getApplicationContext(), mArrayList);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    hideProgress();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                hideProgress();
            }
        });

    }

    private void ClearAll() {
        if (mArrayList != null) {
            mArrayList.clear();
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        } else {
            mArrayList = new ArrayList<>();
        }
    }

    private void showProgress(){
        progressbar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
    private void hideProgress(){
        progressbar.setVisibility(View.INVISIBLE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

}