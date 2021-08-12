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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class OtpLoginActivity extends AppCompatActivity {

    private Button next, back,resend;
    private EditText otp;
    private View view;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private String mVerificationId, mob;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private TextView getmob;
    boolean isConnected, flag = false;
    private RelativeLayout progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_login);

        next = findViewById(R.id.button17);
        back = findViewById(R.id.button18);
        otp = findViewById(R.id.editText9);
        view = findViewById(R.id.view10);
        getmob = findViewById(R.id.textView73);
        resend=findViewById(R.id.textView70);
        progressbar=findViewById(R.id.progress_layout3);
        mAuth = FirebaseAuth.getInstance();
        mob = getIntent().getExtras().getString("value3");
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
                    mAuth.signInWithCredential(credential)
                            .addOnCompleteListener(OtpLoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {


                                    if (task.isSuccessful()) {
                                        boolean isNew = task.getResult().getAdditionalUserInfo().isNewUser();
                                        FirebaseUser user = task.getResult().getUser();
                                        updateUI(user);
                                        if (isNew == true) {
                                            Toast.makeText(OtpLoginActivity.this, "You haven't registered. Do registration first",
                                                    Toast.LENGTH_SHORT).show();
                                            mAuth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    startActivity(new Intent(OtpLoginActivity.this, MainActivity.class));
                                                    hideProgress();
                                                }
                                            });

                                        } else {
                                            Toast.makeText(OtpLoginActivity.this, "Authentication successful",
                                                    Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(OtpLoginActivity.this, HomeActivity.class));
                                            hideProgress();
                                        }

                                    } else {
                                        String error=((FirebaseException)task.getException()).toString();
                                        Toast.makeText(OtpLoginActivity.this, "Authentication failed.\n\n"+error,
                                                Toast.LENGTH_LONG).show();
                                        updateUI(null);
                                        startActivity(new Intent(OtpLoginActivity.this,SignUpActivity.class));
                                        hideProgress();
                                    }
                                }
                            });
                }

                @Override
                public void onVerificationFailed(FirebaseException e) {
                    String message= e.toString();
                    Toast.makeText(OtpLoginActivity.this, "Authentication failed\n"+message , Toast.LENGTH_LONG).show();
                    startActivity(new Intent(OtpLoginActivity.this,SignUpActivity.class));
                    hideProgress();
                }

                @Override
                public void onCodeSent(@NonNull String verificationId,
                                       @NonNull PhoneAuthProvider.ForceResendingToken token) {
                    Toast.makeText(OtpLoginActivity.this, "Code sent to your phone", Toast.LENGTH_SHORT).show();
                    mVerificationId = verificationId;
                    mResendToken = token;
                    flag = true;
                    startTimer();
                    hideProgress();
                }
            };


            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    mob,        // Phone number to verify
                    60,                 // Timeout duration
                    TimeUnit.SECONDS,   // Unit of timeout
                    OtpLoginActivity.this,          // Activity (for callback binding)
                    mCallbacks);

        }
        else{
            startActivity(new Intent(OtpLoginActivity.this, OfflineActivity.class));
        }

        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isConnected = ConnectivityReceiver.isConnected();
                if(!isConnected){
                    startActivity(new Intent(OtpLoginActivity.this, OfflineActivity.class));
                }
                else {
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            mob,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            OtpLoginActivity.this,          // Activity (for callback binding)
                            mCallbacks,
                            mResendToken);        // OnVerificationStateChangedCallbacks
                    startTimer();
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isConnected = ConnectivityReceiver.isConnected();
                if (!isConnected) {
                    startActivity(new Intent(OtpLoginActivity.this, OfflineActivity.class));
                } else {
                    showProgress();
                    String Otp = otp.getText().toString();
                    if (Otp.isEmpty()) {
                        Toast.makeText(OtpLoginActivity.this, "Please enter the otp", Toast.LENGTH_LONG).show();
                        hideProgress();
                    } else {
                        if (flag) {
                            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, Otp);
                            mAuth.signInWithCredential(credential)
                                    .addOnCompleteListener(OtpLoginActivity.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {



                                            if (task.isSuccessful()) {
                                                boolean isNew = task.getResult().getAdditionalUserInfo().isNewUser();
                                                FirebaseUser user = task.getResult().getUser();
                                                updateUI(user);
                                                if (isNew == true) {
                                                    mAuth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            Toast.makeText(OtpLoginActivity.this, "You haven't registered. Do registration first",
                                                                    Toast.LENGTH_SHORT).show();
                                                            startActivity(new Intent(OtpLoginActivity.this, SignUpActivity.class));
                                                            hideProgress();
                                                        }
                                                    });
                                                } else {
                                                    Toast.makeText(OtpLoginActivity.this, "Authentication successful",
                                                            Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(OtpLoginActivity.this, Home2Activity.class));
                                                    hideProgress();
                                                }
                                            } else {
                                                Toast.makeText(OtpLoginActivity.this, "Authentication failed.\n\n"+task.getException().toString(),
                                                        Toast.LENGTH_LONG).show();
                                                updateUI(null);
                                                startActivity(new Intent(OtpLoginActivity.this,SignUpActivity.class));
                                                hideProgress();
                                            }
                                        }


                                    });
                        }
                        else{
                            Toast.makeText(OtpLoginActivity.this, "Authentication failed.Try again later and make sure that internet is constantly available.",Toast.LENGTH_LONG).show();
                            startActivity(new Intent(OtpLoginActivity.this,SignUpActivity.class));
                            hideProgress();
                            finish();
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

    private void showProgress(){
        progressbar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
    private void hideProgress(){
        progressbar.setVisibility(View.INVISIBLE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
}
