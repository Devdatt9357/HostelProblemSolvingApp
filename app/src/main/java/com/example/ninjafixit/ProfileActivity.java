package com.example.ninjafixit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    private TextView name, phone, reg, room, hostel;
    FirebaseDatabase mBase;
    DatabaseReference mRef, nRef;
    FirebaseAuth mAuth;
    private RelativeLayout progressbar, progressbar2;
    private boolean isConnected;
    private Dialog EditProfile;
    private Button edit, back;
    private ArrayList<String> spinnerDataList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorAccent2));

        name = findViewById(R.id.textView90);
        phone = findViewById(R.id.textView91);
        reg = findViewById(R.id.textView92);
        hostel = findViewById(R.id.textView93);
        room = findViewById(R.id.textView94);
        edit = findViewById(R.id.button24);
        back = findViewById(R.id.button23);
        progressbar = findViewById(R.id.progress_layout10);

        isConnected = ConnectivityReceiver.isConnected();
        if (isConnected) {
            retrieveDatabase();
        } else {
            startActivity(new Intent(ProfileActivity.this, OfflineActivity.class));
        }

        EditProfile = new Dialog(this);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isConnected = ConnectivityReceiver.isConnected();
                if (isConnected) {
                    showEditprofilePop();
                } else {
                    startActivity(new Intent(ProfileActivity.this, OfflineActivity.class));
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

    private void retrieveDatabase() {
        showProgress();
        mAuth = FirebaseAuth.getInstance();
        mBase = FirebaseDatabase.getInstance();
        mRef = mBase.getReference("Users").child(mAuth.getUid());
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                name.setText(snapshot.child("Name").getValue().toString());
                phone.setText(snapshot.child("Mobile").getValue().toString());
                reg.setText(snapshot.child("Registration number").getValue().toString());
                room.setText(snapshot.child("Room number").getValue().toString());
                hostel.setText(snapshot.child("Hostel").getValue().toString());
                hideProgress();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                hideProgress();
            }
        });
    }

    public void showEditprofilePop() {
        EditProfile.setContentView(R.layout.profile_edit_popup);
        EditProfile.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        EditProfile.show();

        progressbar2 = EditProfile.findViewById(R.id.progress_layout11);

        progressbar2.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        final Spinner spinner = EditProfile.findViewById(R.id.spinner2);
        final View v1 = EditProfile.findViewById(R.id.view11);
        final View v2 = EditProfile.findViewById(R.id.view12);
        final EditText room = EditProfile.findViewById(R.id.editText11);
        ImageView save = EditProfile.findViewById(R.id.imageView50);
        ImageView cancel = EditProfile.findViewById(R.id.imageView51);

        room.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Resources res = getApplicationContext().getResources();
                    final TransitionDrawable transition = (TransitionDrawable) res.getDrawable(R.drawable.transition, null);
                    v2.setBackgroundDrawable(transition);
                    transition.startTransition(400);
                } else {
                    Resources res = getApplicationContext().getResources();
                    final TransitionDrawable transition2 = (TransitionDrawable) res.getDrawable(R.drawable.transition2, null);

                    v2.setBackgroundDrawable(transition2);
                    transition2.startTransition(300);

                }
            }
        });

        spinner.setFocusableInTouchMode(true);
        spinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(room.getWindowToken(), 0);
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
                    v1.setBackgroundDrawable(transition);
                    transition.startTransition(400);
                } else {
                    Resources res = getApplicationContext().getResources();
                    final TransitionDrawable transition2 = (TransitionDrawable) res.getDrawable(R.drawable.transition2, null);

                    v1.setBackgroundDrawable(transition2);
                    transition2.startTransition(300);

                }
            }
        });

        spinnerDataList = new ArrayList<>();

        mBase = FirebaseDatabase.getInstance();
        mRef = mBase.getReference("Hostels");
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot item : snapshot.getChildren()) {
                        spinnerDataList.add(item.getValue().toString());
                        nRef = mBase.getReference("Users").child(mAuth.getUid());
                        nRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                for (int i = 0; i < spinner.getCount(); i++) {
                                    if (spinner.getItemAtPosition(i).toString().equals(snapshot.child("Hostel").getValue())) {
                                        spinner.setSelection(i);
                                    }
                                    room.setText(snapshot.child("Room number").getValue().toString());
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                    adapter.notifyDataSetChanged();

                    progressbar2.setVisibility(View.INVISIBLE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressbar2.setVisibility(View.INVISIBLE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        });
        adapter = new ArrayAdapter<String>(ProfileActivity.this, R.layout.spinner_item, spinnerDataList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isConnected = ConnectivityReceiver.isConnected();
                if (isConnected) {
                    showProgress();
                    DatabaseReference pRef = mBase.getReference("Users").child(mAuth.getUid()).child("Hostel");
                    DatabaseReference qRef = mBase.getReference("Users").child(mAuth.getUid()).child("Room number");
                    String host = spinner.getSelectedItem().toString();
                    String roomNo = room.getText().toString();
                    pRef.setValue(host);
                    qRef.setValue(roomNo);
                    hideProgress();
                    Toast.makeText(ProfileActivity.this, "Profile edited successfully.", Toast.LENGTH_SHORT).show();
                    EditProfile.dismiss();
                } else {
                    startActivity(new Intent(ProfileActivity.this, OfflineActivity.class));
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditProfile.dismiss();
            }
        });


    }

    private void showProgress() {
        progressbar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void hideProgress() {
        progressbar.setVisibility(View.INVISIBLE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
}