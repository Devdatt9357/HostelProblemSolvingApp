package com.example.ninjafixit;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private OnBoardingAdepter onboardingAdapter;
    private LinearLayout layoutOnboardingIndicators;
    private MaterialButton next,skip;
    private TextView nextText,skipText;
    private FirebaseAuth mAuth;
    private long backPressedtime;
    private Toast backToast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.colorBackground2));
        if(Build.VERSION.SDK_INT>=23)
        {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        //dont alter the place of below lines otherwise things will not work
        layoutOnboardingIndicators=findViewById(R.id.layoutOnboardingIndicators);
        next =findViewById(R.id.button2);
        skip=findViewById(R.id.button);
        nextText=findViewById(R.id.textView2);
        skipText=findViewById(R.id.textView4);
        setupOnboardingItems();

        final ViewPager2 onboardingViewPager=findViewById(R.id.viewPager);
        onboardingViewPager.setAdapter(onboardingAdapter);

        setupOnboardingIndicators();
        setCurrentOnboarding(0);

        onboardingViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentOnboarding(position);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onboardingViewPager.getCurrentItem()+1<onboardingAdapter.getItemCount())
                {
                    onboardingViewPager.setCurrentItem(onboardingViewPager.getCurrentItem()+1);
                }
                else
                {
                        startActivity(new Intent(getApplicationContext(),SignUpActivity.class));
                }
            }
        });

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),SignUpActivity.class));
            }
        });

    }
    private void setupOnboardingItems()
    {
        List<OnBoardingItem> onboardingItems =new ArrayList<>();
        OnBoardingItem frame1=new OnBoardingItem();
        frame1.setTitle("Oh no!!");
        frame1.setDescription("Wanna get your furniture repaired as soon as possible? Call the carpenter at lightening speed through the app.");
        frame1.setImage(R.drawable.frame_1);

        OnBoardingItem frame2=new OnBoardingItem();
        frame2.setTitle("Heading 2");
        frame2.setDescription("Description 2");
        frame2.setImage(R.drawable.frame_1);

        OnBoardingItem frame3=new OnBoardingItem();
        frame3.setTitle("Heading 3");
        frame3.setDescription("Description 3");
        frame3.setImage(R.drawable.frame_1);

        onboardingItems.add(frame1);
        onboardingItems.add(frame2);
        onboardingItems.add(frame3);

        onboardingAdapter =new OnBoardingAdepter(onboardingItems);
    }

    private void setupOnboardingIndicators()
    {
        ImageView[] indicators=new ImageView[onboardingAdapter.getItemCount()];
        LinearLayout.LayoutParams layoutParams= new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(8,0,8,0);
        for(int i=0; i<indicators.length;i++)
        {
            indicators[i]=new ImageView(getApplicationContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(
                    getApplicationContext(),
                    R.drawable.indicator_default));
            indicators[i].setLayoutParams(layoutParams);
            layoutOnboardingIndicators.addView(indicators[i]);

        }
    }
    private void setCurrentOnboarding(int index)
    {
        int count=layoutOnboardingIndicators.getChildCount();
        for(int i=0; i<count; i++)

        {
            ImageView imagrView=(ImageView) layoutOnboardingIndicators.getChildAt(i);
            if(i==index)
            {
                imagrView.setImageDrawable(
                        ContextCompat.getDrawable(getApplicationContext(),R.drawable.indicator_selected)
                );
            }
            else
            {
                imagrView.setImageDrawable(
                        ContextCompat.getDrawable(getApplicationContext(),R.drawable.indicator_default)
                );
            }
        }
        if(index==onboardingAdapter.getItemCount()-1)
        {
            nextText.setTextColor(getResources().getColor(R.color.colorPrimary));
            nextText.setText("Start");
            next.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this,R.color.colorAccent));
            skipText.setVisibility(View.INVISIBLE);
            skip.setVisibility(View.INVISIBLE);
            skip.setClickable(false);

        }
        else{
            nextText.setTextColor(getResources().getColor(R.color.colorAccent));
            nextText.setText("Next");
            next.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this,R.color.colorPrimary2));
            skip.setBackgroundColor(getResources().getColor(R.color.colorPrimary2));
            skipText.setVisibility(View.VISIBLE);
            skip.setVisibility(View.VISIBLE);
            skip.setClickable(true);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth=FirebaseAuth.getInstance();
        FirebaseUser user= mAuth.getCurrentUser();
        if(user !=null)
        {
            startActivity(new Intent(MainActivity.this, Home2Activity.class));
        }
    }

    @Override
    public void onBackPressed() {

        if(backPressedtime+2000>System.currentTimeMillis()) {
            backToast.cancel();
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            startActivity(a);
            return;
        }
        else {
            backToast=Toast.makeText(MainActivity.this,"Press again to exit.",Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressedtime = System.currentTimeMillis();
    }



}
