package com.example.agadimaganda.findyourownbarber;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {

    private EditText emailField;
    private EditText passwordField;
    private EditText passwordAgainField;
    private Button signInBtn;
    private FirebaseAuth auth;

    private Boolean userFromSignIn = true;
    private String email;
    private String password;
    private String passwordAgain;

// TODO: 10.04.2018 username alanı eklenecek. Username ve mail adresleri daha önceden kullanılmış mı diye database'ten arama yapılacak.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        emailField = (EditText) findViewById(R.id.emailField);
        passwordField = (EditText) findViewById(R.id.passwordField);
        passwordAgainField = (EditText) findViewById(R.id.passwordAgainField);
        signInBtn = (Button) findViewById(R.id.signInBtn);



        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });

    }

    private void registerUser(){

        auth = FirebaseAuth.getInstance();

        email = emailField.getText().toString().trim();
        password = passwordField.getText().toString().trim();
        passwordAgain = passwordAgainField.getText().toString().trim();

        if(email.isEmpty()){
            emailField.setError("Email Gerekli!");
            emailField.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailField.setError("Geçerli bir email giriniz.");
            emailField.requestFocus();
            return;
        }

        if(password.isEmpty()){
            passwordField.setError("Sifre Gerekli!");
            passwordField.requestFocus();
            return;
        }

        if(password.length() < 6){
            passwordField.setError("En az 6 karakter uzunluğunda bir şifre giriniz!");
            passwordField.requestFocus();
            return;
        }

        if(passwordAgain.isEmpty()){
            passwordAgainField.setError("Sifre Dogrulama Gerekli!");
            passwordAgainField.requestFocus();
            return;
        }

        if(!password.equalsIgnoreCase(passwordAgain)){
            passwordAgainField.setError("Parola Dogrulamasi Yanlis!");
            passwordAgainField.requestFocus();
            return;
        }


        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //task.getResult();
                    Toast.makeText(getApplicationContext(), "Kullanıcı oluşturma başarılı oldu", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("userFromSignIn", userFromSignIn);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    FirebaseAuth.getInstance().signOut();
                }
            }
        });

    }

    public void backToLogin(View view) {
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean("userFromSignIn", userFromSignIn);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
