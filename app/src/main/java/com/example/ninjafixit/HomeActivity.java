package com.example.ninjafixit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Button menu;
    private ImageView feedback, bug, contact, logout, delete,rate,share,profile;
    private View header;
    private Dialog contactDialog, logoutDialog, deleteDialog;
    private GoogleSignInClient mClient;
    private FirebaseAuth mAuth;
    private DatabaseReference mRef, nRef, pRef;
    private FirebaseDatabase mBase;
    boolean isConnected;
    private TextView name;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private ArrayList<Messages> messageList;
    private RecyclingAdapterHome recyclerAdapter;
    private RecyclingAdapterHome.RecycleClick listner;
    private static final int Request_call=1;
    private RelativeLayout progressbar;
    private long backPressedtime;
    private Toast backToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        menu = findViewById(R.id.button12);
        feedback = findViewById(R.id.imageView30);
        name = findViewById(R.id.textView37);
        recyclerView = findViewById(R.id.recycle_home);
        progressbar=findViewById(R.id.progress_layout6);

        mAuth = FirebaseAuth.getInstance();
        mBase = FirebaseDatabase.getInstance();
        showProgress();
        nRef = mBase.getReference("Users").child(mAuth.getUid()).child("Name");
        nRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String s = snapshot.getValue().toString();
                name.setText(s);
                hideProgress();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                hideProgress();
            }
        });

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorAccent2));
        contactDialog = new Dialog(this);
        logoutDialog = new Dialog(this);
        deleteDialog = new Dialog(this);

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.START);
            }
        });

        header = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);

        feedback = (ImageView) header.findViewById(R.id.imageView30);
        bug = (ImageView) header.findViewById(R.id.imageView27);
        contact = (ImageView) header.findViewById(R.id.imageView25);
        logout = (ImageView) header.findViewById(R.id.imageView22);
        delete = (ImageView) header.findViewById(R.id.imageView23);
        rate=(ImageView) header.findViewById(R.id.imageView26);
        share=(ImageView) header.findViewById(R.id.imageView24);
        profile=(ImageView) header.findViewById(R.id.imageView21);


        logoutDialog.setContentView(R.layout.logout_popup);
        logoutDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final TextView account = logoutDialog.findViewById(R.id.textView59);
        showProgress();
        mRef = mBase.getReference("Users").child(mAuth.getUid()).child("Mobile");
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String s = snapshot.getValue().toString();
                account.setText(s);
                hideProgress();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                hideProgress();
            }
        });


        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isConnected=ConnectivityReceiver.isConnected();
                if(isConnected) {
                    startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                }
                else {
                    startActivity(new Intent(getApplicationContext(), OfflineActivity.class));
                }
            }
        });

        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), FeedbackActivity.class));
            }
        });


        bug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), BugActivity.class));
            }
        });

        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showContactPop();
            }
        });

        rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri=Uri.parse("https://play.google.com/store/apps/details?id="+getApplicationContext().getPackageName());
                Intent i=new Intent(Intent.ACTION_VIEW,uri);
                try {
                    startActivity(i);
                } catch (Exception e) {
                    Toast.makeText(HomeActivity.this,"Error occured. Please try again.\n"+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent i=new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    String shareSub="subject";
                    String shareApp="https://play.google.com/store/apps/details?id="+BuildConfig.APPLICATION_ID+"\n\n";
                    i.putExtra(Intent.EXTRA_SUBJECT,shareSub);
                    i.putExtra(Intent.EXTRA_TEXT,shareApp);
                    startActivity(Intent.createChooser(i,"Share using"));
                } catch (Exception e) {
                    Toast.makeText(HomeActivity.this,"Could not share the app. Please try again.\n"+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutPop();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeletePop();
            }
        });

        layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        int spacingInPixels=getResources().getDimensionPixelSize(R.dimen.recycler_item_space);
        recyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));

        pRef = FirebaseDatabase.getInstance().getReference("App data");
        messageList = new ArrayList<>();
        ClearAll();

        getDatafromFirebase();
    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if(backPressedtime+2000>System.currentTimeMillis()) {
                backToast.cancel();
                Intent a = new Intent(Intent.ACTION_MAIN);
                a.addCategory(Intent.CATEGORY_HOME);
                a.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                startActivity(a);
                return;
            }
            else {
                backToast=Toast.makeText(HomeActivity.this,"Press again to exit.",Toast.LENGTH_SHORT);
                backToast.show();
            }
            backPressedtime = System.currentTimeMillis();
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return true;
    }

    public void showContactPop() {
        contactDialog.setContentView(R.layout.contact_popup);
        contactDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        contactDialog.show();

        ImageView call=contactDialog.findViewById(R.id.imageView43);
        ImageView mail=contactDialog.findViewById(R.id.imageView44);

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MakePhoneCall();
            }
        });

        mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMail();
            }
        });

    }

    public void showLogoutPop() {

        logoutDialog.show();
        final ImageView logout = logoutDialog.findViewById(R.id.imageView45);
        ImageView cancel = logoutDialog.findViewById(R.id.imageView46);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();
                mClient = GoogleSignIn.getClient(HomeActivity.this, gso);

                mClient.signOut().addOnCompleteListener(HomeActivity.this,
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                startActivity(new Intent(HomeActivity.this, SignUpActivity.class));
                                finish();
                            }
                        });
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutDialog.dismiss();
            }
        });
    }

    public void showDeletePop() {
        deleteDialog.setContentView(R.layout.delete_account_popup);
        deleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        deleteDialog.show();

        ImageView delete = deleteDialog.findViewById(R.id.imageView47);
        ImageView cancel = deleteDialog.findViewById(R.id.imageView48);
        final RelativeLayout progress=deleteDialog.findViewById(R.id.progress_layout7);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isConnected = ConnectivityReceiver.isConnected();
                if (isConnected) {

                    progress.setVisibility(View.VISIBLE);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getUid());
                    ref.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            currentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Intent i = new Intent(HomeActivity.this, SignUpActivity.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        mAuth.signOut();
                                        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                                .requestIdToken(getString(R.string.default_web_client_id))
                                                .requestEmail()
                                                .build();
                                        mClient = GoogleSignIn.getClient(HomeActivity.this, gso);

                                        mClient.signOut().addOnCompleteListener(HomeActivity.this,
                                                new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                    }
                                                });

                                        startActivity(i);
                                        Toast.makeText(HomeActivity.this, "Account deleted successfully.", Toast.LENGTH_SHORT).show();
                                        progress.setVisibility(View.INVISIBLE);
                                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                        finish();
                                    } else {
                                        Toast.makeText(HomeActivity.this, task.getException().toString(), Toast.LENGTH_LONG).show();
                                        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                                .requestIdToken(getString(R.string.default_web_client_id))
                                                .requestEmail()
                                                .build();
                                        mClient = GoogleSignIn.getClient(HomeActivity.this, gso);

                                        mClient.signOut().addOnCompleteListener(HomeActivity.this,
                                                new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        startActivity(new Intent(HomeActivity.this, SignUpActivity.class));
                                                        finish();
                                                    }
                                                });
                                        progress.setVisibility(View.INVISIBLE);
                                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    }

                                }
                            });

                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(HomeActivity.this, "Problem occured while deleting the account.", Toast.LENGTH_LONG).show();
                                    progress.setVisibility(View.INVISIBLE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                }
                            });

                } else {
                    startActivity(new Intent(HomeActivity.this, OfflineActivity.class));
                    deleteDialog.dismiss();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialog.dismiss();
            }
        });

    }


    private void ClearAll() {
        if (messageList != null) {
            messageList.clear();
            if (recyclerAdapter != null) {
                recyclerAdapter.notifyDataSetChanged();
            }
        } else {
            messageList = new ArrayList<>();
        }
    }

    private void setOnClicklistner() {
        listner = new RecyclingAdapterHome.RecycleClick() {
            @Override
            public void onClick(View v, int position) {
                isConnected = ConnectivityReceiver.isConnected();
                if (isConnected) {
                    Intent i = new Intent(getApplicationContext(), RecyclerViewActivity.class);
                    i.putExtra("headingName", messageList.get(position).getHeadname());
                    startActivity(i);
                } else {
                    startActivity(new Intent(HomeActivity.this, OfflineActivity.class));
                }
            }
        };
    }

    private void getDatafromFirebase() {
        showProgress();
        Query query = pRef.child("Main topics");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ClearAll();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Messages messages = new Messages();
                    messages.setHeadUrl(dataSnapshot.child("2").getValue().toString());
                    messages.setHeadname(dataSnapshot.child("1").getValue().toString());
                    messageList.add(messages);
                }
                setOnClicklistner();
                recyclerAdapter = new RecyclingAdapterHome(getApplicationContext(), messageList, listner);
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

    private void MakePhoneCall(){
        if(ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(HomeActivity.this,new String[]{Manifest.permission.CALL_PHONE}, Request_call);
        }
        else {
            String phonenumber="tel:"+"+917405339670";
            startActivity(new Intent(Intent.ACTION_CALL,Uri.parse(phonenumber)));
        }
    }

    private void SendMail(){
        String email="devdattchavda1692000@gmail.com";
        String[] multipleEmail=email.split(",");
        Intent i=new Intent(Intent.ACTION_SEND);
        i.putExtra(Intent.EXTRA_EMAIL,multipleEmail);
        i.setType("message/rfc822");
        startActivity(Intent.createChooser(i,"Contact the developer"));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==Request_call){
            if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                MakePhoneCall();
            }
            else{
                Toast.makeText(HomeActivity.this,"Permission denied",Toast.LENGTH_SHORT).show();
            }
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
