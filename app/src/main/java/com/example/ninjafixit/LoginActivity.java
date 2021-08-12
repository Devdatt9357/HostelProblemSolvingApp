package com.example.ninjafixit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private Button next, back;
    private EditText mobile;
    private View view;
    String getMob;
    private boolean isConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        next = findViewById(R.id.button9);
        back = findViewById(R.id.button10);
        mobile = findViewById(R.id.editText6);
        view = findViewById(R.id.view6);

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorBackground2));

        if (Build.VERSION.SDK_INT >= 23) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        mobile.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isConnected = ConnectivityReceiver.isConnected();
                if (!isConnected) {
                    startActivity(new Intent(LoginActivity.this, OfflineActivity.class));
                } else {

                    if (Validate()) {
                        Intent i = new Intent(LoginActivity.this, OtpLoginActivity.class);
                        getMob = mobile.getText().toString();
                        i.putExtra("value3", getMob);
                        startActivity(i);
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

    private Boolean Validate() {
        Boolean result = false;
        String p1 = mobile.getText().toString();
        if (p1.length() == 13) {
            result = true;
        } else {
            Toast.makeText(this, "Invalid mobile number", Toast.LENGTH_LONG).show();
        }


        return result;
    }
}
