package com.example.ninjafixit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    private View view1, view2;
    private EditText email, pass;
    private TextView error, passError, login;
    private boolean changeColor, changeColor2, eyeChange;
    boolean isConnected;
    private ImageView eye, proceed;
    private Button google, facebook;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseUser user;
    private static final String TAG = "MainActivity";
    private CallbackManager mCallbackManager;
    private RelativeLayout progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorBackground2));
        if (Build.VERSION.SDK_INT >= 23) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        view1 = findViewById(R.id.view);
        view2 = findViewById(R.id.view2);
        email = findViewById(R.id.editText);
        pass = findViewById(R.id.editText2);
        error = findViewById(R.id.textView8);
        passError = findViewById(R.id.textView10);
        eye = findViewById(R.id.imageView4);
        proceed = findViewById(R.id.imageView5);
        google = findViewById(R.id.button3);
        facebook = findViewById(R.id.button4);
        login = findViewById(R.id.textView14);
        mAuth = FirebaseAuth.getInstance();
        progressbar = findViewById(R.id.progress_layout);

        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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

        pass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches() && changeColor == false) {

                    error.setText("Looks great!");
                    int colorFrom = getResources().getColor(R.color.colorPrimary2);
                    int colorTo = getResources().getColor(R.color.colorGreen);
                    ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
                    colorAnimation.setDuration(100); // milliseconds
                    colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                        @Override
                        public void onAnimationUpdate(ValueAnimator animator) {
                            error.setTextColor((int) animator.getAnimatedValue());
                        }

                    });
                    colorAnimation.start();
                    changeColor = true;
                    changeColor2 = false;
                } else if (Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches() && changeColor == true) {
                    error.setText("Looks great!");
                    changeColor2 = false;
                } else if (!(Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) && changeColor2 == false) {
                    error.setText("Enter a valid email (e.g. email@example.com)");
                    int colorFrom = getResources().getColor(R.color.colorPrimary2);
                    int colorTo = getResources().getColor(R.color.colorCaution);
                    ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
                    colorAnimation.setDuration(100); // milliseconds
                    colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                        @Override
                        public void onAnimationUpdate(ValueAnimator animator) {
                            error.setTextColor((int) animator.getAnimatedValue());
                        }

                    });
                    colorAnimation.start();
                    changeColor2 = true;
                    changeColor = false;
                } else if (!(Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) && changeColor2 == true) {
                    error.setText("Enter a valid email (e.g. email@example.com)");
                    changeColor = false;
                }
            }
        });
        changeColor = false;
        changeColor2 = false;
        pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (pass.getText().toString().length() >= 6 && changeColor == false) {

                    passError.setText("Awesome!");
                    int colorFrom = getResources().getColor(R.color.colorPrimary2);
                    int colorTo = getResources().getColor(R.color.colorGreen);
                    ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
                    colorAnimation.setDuration(100); // milliseconds
                    colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                        @Override
                        public void onAnimationUpdate(ValueAnimator animator) {
                            passError.setTextColor((int) animator.getAnimatedValue());
                        }

                    });
                    colorAnimation.start();
                    changeColor = true;
                    changeColor2 = false;
                } else if (pass.getText().toString().length() >= 6 && changeColor == true) {
                    passError.setText("Awesome!");
                    changeColor2 = false;
                } else if (pass.getText().toString().length() < 6 && changeColor2 == false) {
                    passError.setText("Password must contain atleast 6 characters");
                    int colorFrom = getResources().getColor(R.color.colorPrimary2);
                    int colorTo = getResources().getColor(R.color.colorCaution);
                    ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
                    colorAnimation.setDuration(100); // milliseconds
                    colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                        @Override
                        public void onAnimationUpdate(ValueAnimator animator) {
                            passError.setTextColor((int) animator.getAnimatedValue());
                        }

                    });
                    colorAnimation.start();
                    changeColor2 = true;
                    changeColor = false;
                } else if (pass.getText().toString().length() < 6 && changeColor2 == true) {
                    passError.setText("Password must contain atleast 6 characters");
                    changeColor = false;
                }
            }
        });


        eye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (eyeChange == false) {
                    DrawableCompat.setTint(eye.getDrawable(), ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                    pass.setTransformationMethod(null);
                    pass.setSelection(pass.length());
                    eyeChange = true;
                } else {
                    DrawableCompat.setTint(eye.getDrawable(), ContextCompat.getColor(getApplicationContext(), R.color.colorboxBorder));
                    pass.setTransformationMethod(new PasswordTransformationMethod());
                    pass.setSelection(pass.length());
                    eyeChange = false;
                }
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isConnected = ConnectivityReceiver.isConnected();
                if (!isConnected) {
                    startActivity(new Intent(SignUpActivity.this, OfflineActivity.class));
                } else {
                    showProgress();
                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, 101);
                }

            }
        });


        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isConnected = ConnectivityReceiver.isConnected();
                if (!isConnected) {
                    startActivity(new Intent(SignUpActivity.this, OfflineActivity.class));
                } else {
                    if (Validate()) {
                        showProgress();
                        String e1 = email.getText().toString().trim();
                        String p1 = pass.getText().toString().trim();
                        mAuth.createUserWithEmailAndPassword(e1, p1).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {
                                    SendUserDatabase();
                                    Toast.makeText(SignUpActivity.this, "Signup successful", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(SignUpActivity.this, SignUp2Activity.class));
                                    hideProgress();
                                } else {
                                    String message = task.getException().toString();
                                    Toast.makeText(SignUpActivity.this, "Error occured.:\n\n" + message, Toast.LENGTH_LONG).show();
                                    hideProgress();
                                }
                            }
                        });
                    }
                }

            }
        });
        mCallbackManager = CallbackManager.Factory.create();
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isConnected = ConnectivityReceiver.isConnected();
                if (!isConnected) {
                    startActivity(new Intent(SignUpActivity.this, OfflineActivity.class));
                } else {
                    showProgress();
                    LoginManager.getInstance().logInWithReadPermissions(SignUpActivity.this, Arrays.asList("email", "public_profile"));
                    LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            Log.d(TAG, "facebook:onSuccess:" + loginResult);
                            handleFacebookAccessToken(loginResult.getAccessToken());
                        }

                        @Override
                        public void onCancel() {
                            Log.d(TAG, "facebook:onCancel");
                            hideProgress();
                            // ...
                        }

                        @Override
                        public void onError(FacebookException error) {
                            Log.d(TAG, "facebook:onError", error);
                            hideProgress();
                            // ...
                        }
                    });
                }
            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });


    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SignUpActivity.this, Home2Activity.class));
    }

    private Boolean Validate() {
        Boolean result = false;
        String e1 = email.getText().toString();
        String p1 = pass.getText().toString();
        if (!e1.isEmpty() && !p1.isEmpty()) {
            result = true;
        } else {
            Toast.makeText(this, "Email and password must not be empty", Toast.LENGTH_LONG).show();
        }

        return result;
    }

    private void SendUserDatabase() {


        FirebaseDatabase mbase = FirebaseDatabase.getInstance();
        DatabaseReference mRef = mbase.getReference("Users");
        try {

            mRef.child(mAuth.getUid()).setValue(mAuth.getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(SignUpActivity.this, "Hello world", Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SignUpActivity.this, "Hello world2"+e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }).addOnCanceledListener(new OnCanceledListener() {
                @Override
                public void onCanceled() {
                    Toast.makeText(SignUpActivity.this, "Hello world3", Toast.LENGTH_LONG).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(SignUpActivity.this, "Hello world4", Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }


    }

    private void updateUI(FirebaseUser user) {
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null)
                    firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                String message = task.getException().toString();
                Toast.makeText(SignUpActivity.this, "Google sign in failed\n\n" + message, Toast.LENGTH_LONG).show();
                hideProgress();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            boolean isNew = task.getResult().getAdditionalUserInfo().isNewUser();
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(SignUpActivity.this, "Google sign in successful", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "signInWithCredential:success");
                            user = mAuth.getCurrentUser();
                            updateUI(user);
                            if (isNew == true) {
                                SendUserDatabase();
                                FirebaseDatabase mbase = FirebaseDatabase.getInstance();
                                DatabaseReference mRef = mbase.getReference("Users");
                                        startActivity(new Intent(SignUpActivity.this, SignUp2Activity.class));
                                        hideProgress();

                            } else {
                                startActivity(new Intent(SignUpActivity.this, Home2Activity.class));
                                hideProgress();
                            }


                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SignUpActivity.this, "Google sign in failed", Toast.LENGTH_LONG).show();
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            updateUI(null);
                            hideProgress();
                        }
                    }
                });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            boolean isNew = task.getResult().getAdditionalUserInfo().isNewUser();
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            Toast.makeText(SignUpActivity.this, "Facebook sign in successful", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            if (isNew == true) {

                                SendUserDatabase();
                                startActivity(new Intent(SignUpActivity.this, SignUp2Activity.class));
                                hideProgress();
                            } else {
                                startActivity(new Intent(SignUpActivity.this, Home2Activity.class));
                                hideProgress();
                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "Authentication failed." + task.getException().toString(),
                                    Toast.LENGTH_LONG).show();
                            updateUI(null);
                            hideProgress();
                        }

                        // ...
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
