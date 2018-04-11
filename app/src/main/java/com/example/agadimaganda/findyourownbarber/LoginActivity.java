package com.example.agadimaganda.findyourownbarber;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    //User Interface
    private EditText emailField;
    private EditText passwordField;
    private Button loginBtn;
    private CheckBox checkBox;

    //Firebase Connection
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;

    //Variables
    private Boolean userFromSignIn = false;
    private Boolean keepMeSignedIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Firebase.setAndroidContext(this);
        auth = FirebaseAuth.getInstance();
        emailField = (EditText) findViewById(R.id.emailField);
        passwordField = (EditText) findViewById(R.id.passwordField);
        loginBtn = (Button) findViewById(R.id.loginBtn);
        checkBox = (CheckBox) findViewById(R.id.keepMeSignedIn);

        Intent intent = getIntent();
        Bundle bundle = getIntent().getExtras();
        if (intent != null && bundle != null) {
            userFromSignIn = bundle.getBoolean("userFromSignIn");
        }


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startLogin();
            }
        });


        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null && !userFromSignIn){
                    //firebaseAuth.getCurrentUser().delete();
                    Intent intent2 = new Intent(LoginActivity.this, MapsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("keepMeSignedIn", keepMeSignedIn);
                    intent2.putExtras(bundle);
                    startActivity(intent2);
                }else if(userFromSignIn){
                    userFromSignIn = false;
                }
            }
        };
    }

    protected  void onStart(){
        super.onStart();
        auth.addAuthStateListener(authStateListener);
    }

    private void startLogin(){

        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();

        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
            Toast.makeText(LoginActivity.this, "Email veya Sifre bos.", Toast.LENGTH_LONG).show();

        }else{
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(!task.isSuccessful()){
                        Toast.makeText(LoginActivity.this, "Oyle bir kullanici yok.", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }


    public void signUp(View view) {
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intent);
    }
}
