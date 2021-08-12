package com.example.ninjafixit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

public class WelcomeActivity extends AppCompatActivity {

    private Button submit;
    private EditText regNo,roomNo;
    private Spinner spinner;
    private View view1,view2,view3;
    private  DatabaseReference mRef;
    private FirebaseDatabase mBase;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> spinnerDataList;
    FirebaseAuth mAuth;
    boolean isConnected;
    private RelativeLayout progressbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        submit=findViewById(R.id.button11);
        regNo=findViewById(R.id.editText7);
        roomNo=findViewById(R.id.editText8);
        spinner=findViewById(R.id.spinner);
        view1=findViewById(R.id.view7);
        view2=findViewById(R.id.view8);
        view3=findViewById(R.id.view9);
        progressbar=findViewById(R.id.progress_layout4);


        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.colorBackground2));
        if(Build.VERSION.SDK_INT>=23)
        {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        regNo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Resources res = getApplicationContext().getResources();
                    final TransitionDrawable transition = (TransitionDrawable) res.getDrawable(R.drawable.transition, null);
                    view1.setBackgroundDrawable(transition);
                    transition.startTransition(400);
                } else {
                    Resources res = getApplicationContext().getResources();
                    final TransitionDrawable transition2 = (TransitionDrawable) res.getDrawable(R.drawable.transition2, null);

                    view1.setBackgroundDrawable(transition2);
                    transition2.startTransition(300);

                }
            }
        });

        roomNo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Resources res = getApplicationContext().getResources();
                    final TransitionDrawable transition = (TransitionDrawable) res.getDrawable(R.drawable.transition, null);
                    view3.setBackgroundDrawable(transition);
                    transition.startTransition(400);
                } else {
                    Resources res = getApplicationContext().getResources();
                    final TransitionDrawable transition2 = (TransitionDrawable) res.getDrawable(R.drawable.transition2, null);

                    view3.setBackgroundDrawable(transition2);
                    transition2.startTransition(300);

                }
            }
        });

        spinner.setFocusableInTouchMode(true);
        spinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm=(InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(regNo.getWindowToken(),0);
                spinner.performClick();
                return false;
            }
        });

        spinner.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Resources res = getApplicationContext().getResources();
                    final TransitionDrawable transition = (TransitionDrawable) res.getDrawable(R.drawable.transition, null);
                    view2.setBackgroundDrawable(transition);
                    transition.startTransition(400);
                } else {
                    Resources res = getApplicationContext().getResources();
                    final TransitionDrawable transition2 = (TransitionDrawable) res.getDrawable(R.drawable.transition2, null);

                    view2.setBackgroundDrawable(transition2);
                    transition2.startTransition(300);

                }
            }
        });

        spinnerDataList=new ArrayList<>();
        adapter=new ArrayAdapter<String>(WelcomeActivity.this,R.layout.spinner_item,spinnerDataList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        showProgress();
        mBase=FirebaseDatabase.getInstance();
        mRef=mBase.getReference("Hostels");
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot item:snapshot.getChildren()){
                        spinnerDataList.add(item.getValue().toString());
                    }
                    adapter.notifyDataSetChanged();
                    hideProgress();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                hideProgress();
            }
        });



        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isConnected=ConnectivityReceiver.isConnected();
                if (!isConnected) {
                    startActivity(new Intent(WelcomeActivity.this, OfflineActivity.class));
                } else {
                    if (Validate()) {
                        showProgress();
                        SendUserDatabase();
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        hideProgress();
                        finish();
                    }
                }
            }
        });

    }
    private Boolean Validate(){
        Boolean result=false;
        String reg=regNo.getText().toString();
        String room=roomNo.getText().toString();
        if(!reg.isEmpty()&&!room.isEmpty()) {
            result = true;
        }
        else {
            Toast.makeText(this, "Please enter your registration number and room number", Toast.LENGTH_LONG).show();
        }
        return result;
    }

    private void SendUserDatabase()
    {
        mAuth=FirebaseAuth.getInstance();
        FirebaseDatabase mbase=FirebaseDatabase.getInstance();
        DatabaseReference mRef=mbase.getReference("Users").child(mAuth.getUid()).child("Registration number");
        DatabaseReference nRef=mbase.getReference("Users").child(mAuth.getUid()).child("Hostel");
        DatabaseReference pRef=mbase.getReference("Users").child(mAuth.getUid()).child("Room number");
        String reg = regNo.getText().toString();
        String room = roomNo.getText().toString();
        String host=spinner.getSelectedItem().toString();
        mRef.setValue(reg);
        nRef.setValue(host);
        pRef.setValue(room);

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
