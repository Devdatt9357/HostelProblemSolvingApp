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
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

public class SignUp2Activity extends AppCompatActivity {

    private Button next, back;
    private EditText mobile;
    private android.widget.EditText name;
    private View view1, view2;
    String getName, getMob;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    FirebaseAuth mAuth;
    private boolean isConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up2);

        next = findViewById(R.id.button5);
        back = findViewById(R.id.button6);
        name = findViewById(R.id.editText3);
        mobile = findViewById(R.id.editText4);
        view1 = findViewById(R.id.view3);
        view2 = findViewById(R.id.view4);

        mAuth = FirebaseAuth.getInstance();

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorBackground2));
        if (Build.VERSION.SDK_INT >= 23) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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

        mobile.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isConnected = ConnectivityReceiver.isConnected();
                if (!isConnected) {
                    startActivity(new Intent(SignUp2Activity.this, OfflineActivity.class));
                } else {

                    if (Validate()) {
                        Intent i = new Intent(SignUp2Activity.this, OtpActivity.class);
                        getName = name.getText().toString();
                        getMob = mobile.getText().toString();
                        i.putExtra("value1", getName);
                        i.putExtra("value2", getMob);
                        startActivity(i);
                   /* String mob = mobile.getText().toString().trim();
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            mob,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            SignUp2Activity.this,          // Activity (for callback binding)
                            mCallbacks);        // OnVerificationStateChangedCallbacks

                    */

                    }
                }
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                Toast.makeText(SignUp2Activity.this, "Authentication successful", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(SignUp2Activity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                Toast.makeText(SignUp2Activity.this, "Code sent to your phone", Toast.LENGTH_SHORT).show();
            }
        };


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private Boolean Validate() {
        Boolean result = false;
        String e1 = name.getText().toString();
        String p1 = mobile.getText().toString();
        if (!e1.isEmpty() && p1.length() == 13) {
            result = true;
        } else if (e1.isEmpty()) {
            Toast.makeText(this, "Name must not be empty", Toast.LENGTH_LONG).show();
        } else if (p1.length() != 13) {
            Toast.makeText(this, "Invalid mobile number", Toast.LENGTH_LONG).show();
        }


        return result;
    }

}
