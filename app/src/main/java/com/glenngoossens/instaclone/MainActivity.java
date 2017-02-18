package com.glenngoossens.instaclone;

import android.content.Intent;
import android.hardware.input.InputManager;
import android.net.Uri;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.glenngoossens.instaclone.FireBase.FirebaseDB;
import com.glenngoossens.instaclone.models.User;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,View.OnKeyListener{

    private EditText usernameField;
    private EditText passwordField;
    private TextView changeSignUpModeTextView;
    private Boolean signUpModeActive;
    private Button signUpButton;
    private ImageView logo;
    private ConstraintLayout layout;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseDatabase mDatabase;
    private DatabaseReference myRef;

    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        signUpModeActive = true;
        usernameField = (EditText) findViewById(R.id.usernameEditText);
        passwordField = (EditText) findViewById(R.id.passwordEditText);
        changeSignUpModeTextView = (TextView) findViewById(R.id.changeSignUpMode);
        signUpButton = (Button) findViewById(R.id.signUpButton);
        logo =(ImageView) findViewById(R.id.imageViewLogo);
        layout = (ConstraintLayout) findViewById(R.id.constraintLayout);

        changeSignUpModeTextView.setOnClickListener(this);
        //logo.setOnClickListener(this);
       // layout.setOnClickListener(this);

        passwordField.setOnKeyListener(this);

        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    Log.d(TAG, "onAuthStateChanged: signed in" + user.getUid());
                    Intent i = new Intent(getApplicationContext(),UserListActivity.class);
                    startActivity(i);
                }else{
                    Log.d(TAG, "onAuthStateChanged: signed out");
                }
            }
        };

        mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference("UserList");

    }

    public void signUpOrLogIn(View view){
        Log.i(TAG, "signUpOrLogIn: username = " + usernameField.getText().toString() + " password = " + passwordField.getText().toString());
        if(signUpModeActive == true) {
            //SignUP
            mAuth.createUserWithEmailAndPassword(usernameField.getText().toString(), passwordField.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "onComplete: " + task.isSuccessful());
                            if (!task.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "Account already exists", Toast.LENGTH_SHORT).show();
                            }else{
                                User user = new User();
                                user.setEmail(usernameField.getText().toString());
                                FirebaseUser userFireBase = mAuth.getCurrentUser();
                                user.setUid(userFireBase.getUid());
                                DatabaseReference newRef = myRef.push();
                                newRef.setValue(user);
                                Log.d(TAG, "onComplete: "+ user.toString());
                                Intent intent = new Intent(getApplicationContext(),UserListActivity.class);
                                startActivity(intent);
                            }
                        }
                    });
        }else{
            //SignIn
            mAuth.signInWithEmailAndPassword(usernameField.getText().toString(),passwordField.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "onComplete: " + task.isSuccessful());
                            if(!task.isSuccessful()){
                                Log.w(TAG, "onComplete: ", task.getException());
                                Toast.makeText(MainActivity.this,"Authentication failed",Toast.LENGTH_SHORT).show();
                            }else{
                                Intent intent = new Intent(getApplicationContext(),UserListActivity.class);
                                startActivity(intent);
                            }
                        }
                    });
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthStateListener != null){
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.changeSignUpMode){
            if(signUpModeActive == true){
                signUpModeActive = false;
                changeSignUpModeTextView.setText("Sign Up");
                signUpButton.setText("Log In");

            }else{
                signUpModeActive = true;
                changeSignUpModeTextView.setText("Log In");
                signUpButton.setText("Sign Up");
            }

        }else if(v.getId() == R.id.imageViewLogo || v.getId() == R.id.constraintLayout){
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
        }


    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN){
        signUpOrLogIn(v);
        }
        return false;
    }
}
