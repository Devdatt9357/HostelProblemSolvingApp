package com.example.ninjafixit;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class OfflineActivity extends AppCompatActivity {
    private Button button;
    boolean isConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline);

        button=findViewById(R.id.button20);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isConnected=ConnectivityReceiver.isConnected();
                if(isConnected){
                    finish();
                }
                else{
                    Toast.makeText(OfflineActivity.this, "Please check your internet connection and try again.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        isConnected=ConnectivityReceiver.isConnected();
        if(isConnected){
            finish();
        }
        else{
            Toast.makeText(OfflineActivity.this, "Please check your internet connection and try again.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /*@Override
    public void onBackPressed() {
        isConnected=ConnectivityReceiver.isConnected();
        if(isConnected){
            super.onBackPressed();
        }
        else{
            Toast.makeText(OfflineActivity.this, "Please check your internet connection and try again.",
                    Toast.LENGTH_SHORT).show();
        }
    }*/
}
