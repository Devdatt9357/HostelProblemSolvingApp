package com.example.ninjafixit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.concurrent.TimeUnit;

public class OtpActivity extends AppCompatActivity {

    private Button next, back,resend;
    private EditText otp;
    private View view;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private String mVerificationId, name, mob;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private TextView getname, getmob;
    boolean isConnected,flag=false;
    private GoogleSignInClient mClient;
    private RelativeLayout progressbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        next = findViewById(R.id.button7);
        back = findViewById(R.id.button8);
        otp = findViewById(R.id.editText5);
        view = findViewById(R.id.view5);
        getmob = findViewById(R.id.textView66);
        getname = findViewById(R.id.textView67);
        resend=findViewById(R.id.textView25);
        progressbar=findViewById(R.id.progress_layout2);
        mAuth = FirebaseAuth.getInstance();

        name = getIntent().getExtras().getString("value1");
        getname.setText(name);
        mob = getIntent().getExtras().getString("value2");
        getmob.setText(mob);


        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorBackground2));
        if (Build.VERSION.SDK_INT >= 23) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        otp.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Resources res = getApplicationContext().getResources();
                    final TransitionDrawable transition = (TransitionDrawable) res.getDrawable(R.drawable.transition, null);
                    view.setBackgroundDrawable(transition);
                    transition.startTransition(400);
                } else {
                    Resources res = getApplicationContext().getResources();
                    final TransitionDrawable transition2 = (TransitionDrawable) res.getDrawable(R.drawable.transition2, null);

                    view.setBackgroundDrawable(transition2);
                    transition2.startTransition(300);

                }
            }
        });

        isConnected = ConnectivityReceiver.isConnected();
        if(isConnected) {
            showProgress();
            mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onVerificationCompleted(PhoneAuthCredential credential) {
                    mAuth.getCurrentUser().linkWithCredential(credential)
                            .addOnCompleteListener(OtpActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(OtpActivity.this, "Authentication successful",
                                                Toast.LENGTH_SHORT).show();
                                        FirebaseUser user = task.getResult().getUser();
                                        updateUI(user);
                                        SendUserDatabase();
                                        startActivity(new Intent(OtpActivity.this, WelcomeActivity.class));
                                        hideProgress();
                                        finish();
                                    } else {
                                        String error=((FirebaseException)task.getException()).toString();
                                        Toast.makeText(OtpActivity.this, "Authentication failed.\n\n"+error,
                                                Toast.LENGTH_LONG).show();
                                        updateUI(null);
                                       logout();
                                       hideProgress();
                                    }

                                    // ...
                                }
                            });
                }

                @Override
                public void onVerificationFailed(FirebaseException e) {
                    String message= e.toString();
                    Toast.makeText(OtpActivity.this, "Authentication failed\n"+message, Toast.LENGTH_LONG).show();
                    logout();
                    hideProgress();
                }

                @Override
                public void onCodeSent(@NonNull String verificationId,
                                       @NonNull PhoneAuthProvider.ForceResendingToken token) {
                    Toast.makeText(OtpActivity.this, "Code sent to your phone", Toast.LENGTH_SHORT).show();
                    mVerificationId = verificationId;
                    mResendToken = token;
                    flag = true;
                    startTimer();
                    hideProgress();
                }
            };


            //Keep this phoneauthprovider always under mCallbacks otherwise it will throw error.

            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    mob,        // Phone number to verify
                    60,                 // Timeout duration
                    TimeUnit.SECONDS,   // Unit of timeout
                    OtpActivity.this,          // Activity (for callback binding)
                    mCallbacks);        // OnVerificationStateChangedCallbacks
        }
        else {
            startActivity(new Intent(OtpActivity.this, OfflineActivity.class));
        }

        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isConnected = ConnectivityReceiver.isConnected();
                if(!isConnected){
                    startActivity(new Intent(OtpActivity.this, OfflineActivity.class));
                }
                else {
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            mob,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            OtpActivity.this,          // Activity (for callback binding)
                            mCallbacks,
                            mResendToken);        // OnVerificationStateChangedCallbacks
                    startTimer();
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isConnected = ConnectivityReceiver.isConnected();
                if (!isConnected) {
                    startActivity(new Intent(OtpActivity.this, OfflineActivity.class));
                } else {
                    showProgress();
                    String Otp = otp.getText().toString();
                    if (Otp.isEmpty()) {
                        Toast.makeText(OtpActivity.this, "Please enter the otp", Toast.LENGTH_LONG).show();
                        hideProgress();
                    } else {
                        if (flag) {

                            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, Otp);

                            mAuth.getCurrentUser().linkWithCredential(credential)
                                    .addOnCompleteListener(OtpActivity.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(OtpActivity.this, "Authentication successful",
                                                        Toast.LENGTH_SHORT).show();
                                                FirebaseUser user = task.getResult().getUser();
                                                updateUI(user);
                                                SendUserDatabase();
                                                startActivity(new Intent(OtpActivity.this, WelcomeActivity.class));
                                                hideProgress();
                                                finish();
                                            } else {
                                                Toast.makeText(OtpActivity.this, "Authentication failed.\n\n"+task.getException().toString(),
                                                        Toast.LENGTH_LONG).show();
                                                updateUI(null);
                                                logout();
                                                hideProgress();
                                            }

                                            // ...
                                        }
                                    });
                        }
                        else{
                            Toast.makeText(OtpActivity.this, "Authentication failed.Try again later and make sure that internet is constantly available.",Toast.LENGTH_LONG).show();
                            logout();
                            hideProgress();
                        }
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

    private void updateUI(FirebaseUser currentUser) {
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            String uid = user.getUid();
        }
    }

    private void SendUserDatabase() {
        FirebaseDatabase mbase = FirebaseDatabase.getInstance();
        DatabaseReference mRef = mbase.getReference("Users").child(mAuth.getUid()).child("Mobile");
        DatabaseReference nRef = mbase.getReference("Users").child(mAuth.getUid()).child("Name");
        String mob = getmob.getText().toString();
        String name = getname.getText().toString();
        mRef.setValue(mob);
        nRef.setValue(name);

    }
    private void startTimer(){
        CountDownTimer timer=new CountDownTimer(60000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                resend.setText("Resend otp in "+millisUntilFinished/1000);
                resend.setClickable(false);
            }

            @Override
            public void onFinish() {
                resend.setText("Resend OTP");
                resend.setClickable(true);
            }
        };
        timer.start();
    }

    private void logout(){
        mAuth=FirebaseAuth.getInstance();
        mAuth.signOut();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mClient = GoogleSignIn.getClient(OtpActivity.this, gso);

        mClient.signOut().addOnCompleteListener(OtpActivity.this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        startActivity(new Intent(OtpActivity.this, SignUpActivity.class));
                        finish();
                    }
                });
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
