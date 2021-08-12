package com.example.ninjafixit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Path;

import java.util.ArrayList;

public class RecyclerViewActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    private DatabaseReference mRef;
    private ArrayList<Messages> messageList;
    private RecyclingAdepter recyclerAdapter;
    private RecyclingAdepter.RecycleClick listner;
    private TextView head;
    private String heading;
    private Button back;
    private Boolean isConnectd;
    private RelativeLayout progressbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);

        progressbar=findViewById(R.id.progress_layout8);

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorAccent2));

        isConnectd=ConnectivityReceiver.isConnected();
        if(!isConnectd){
            startActivity(new Intent(RecyclerViewActivity.this, OfflineActivity.class));
        }

        head=findViewById(R.id.textView78);
        back=findViewById(R.id.button19);

        Bundle extras=getIntent().getExtras();
        if(extras!=null){
            heading=extras.getString("headingName");
        }
       head.setText(heading);


        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mRef= FirebaseDatabase.getInstance().getReference("App data");
        messageList=new ArrayList<>();
        ClearAll();

        getDatafromFirebase();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getDatafromFirebase(){
        showProgress();
        final Query query=mRef.child(head.getText().toString());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ClearAll();
                for(final DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Messages messages=new Messages();
                    messages.setImageUrl(dataSnapshot.child("2").getValue().toString());
                    messages.setName(dataSnapshot.child("1").getValue().toString());
                    messages.setDescription(dataSnapshot.child("3").getValue().toString());
                    messageList.add(messages);
                }
                setOnClicklistner();
                recyclerAdapter =new RecyclingAdepter(getApplicationContext(),messageList, listner);
                recyclerView.setAdapter(recyclerAdapter);
                recyclerAdapter.notifyDataSetChanged();
                hideProgress();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                hideProgress();

            }
        });

    }

    private void setOnClicklistner() {
        listner=new RecyclingAdepter.RecycleClick() {
            @Override
            public void onClick(View v, int position) {
               /* isConnectd=ConnectivityReceiver.isConnected();
                if(isConnectd) {
                    Intent i = new Intent(getApplicationContext(), RecyclerlistListenerActivity.class);
                    i.putExtra("SubheadingName", messageList.get(position).getName());
                    i.putExtra("head", head.getText().toString());

                    startActivity(i);
                }
                else{
                    startActivity(new Intent(RecyclerViewActivity.this, OfflineActivity.class));
                }

                */
            }
        };
    }


    private void ClearAll(){
        if(messageList!=null){
            messageList.clear();
            if(recyclerAdapter!=null){
                recyclerAdapter.notifyDataSetChanged();
            }
        }
        else{
            messageList=new ArrayList<>();
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