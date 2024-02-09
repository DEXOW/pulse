package com.example.pulse;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class login extends AppCompatActivity {
    private RelativeLayout loginBtn;
    private TextView switchToSignUpBtn;
    private EditText emailInput, passwordInput;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressBar = findViewById(R.id.progressBar);

        switchToSignUpBtn = findViewById(R.id.switchToSignUp);
        loginBtn = findViewById(R.id.loginBtn);

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {
            finish();
            startActivity(new Intent(login.this, explore.class));
        }

        switchToSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(login.this, signup.class));
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailVal = emailInput.getText().toString().trim();
                String pass = passwordInput.getText().toString();
                if (emailVal.isEmpty()) {
                    emailInput.setError("Please enter your email");
                    emailInput.requestFocus();
                }
                if (pass.isEmpty()) {
                    passwordInput.setError("Please enter your password");
                    passwordInput.requestFocus();
                }
                if (!(emailVal.isEmpty() && pass.isEmpty())) {
                    progressBar.setVisibility(View.VISIBLE);
                    firebaseAuth.signInWithEmailAndPassword(emailVal, pass).addOnCompleteListener(login.this, task -> {
                        progressBar.setVisibility(View.GONE);
                        if (!task.isSuccessful()) {
                            passwordInput.setError("Invalid email or password");
                            passwordInput.requestFocus();
                        } else {
                            finish();
                            Toast.makeText(login.this, String.format("Logged in as %s", firebaseAuth.getCurrentUser().getEmail()), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(login.this, explore.class));
                        }
                    });
                } else {
                    passwordInput.setError("Error occurred");
                    passwordInput.requestFocus();
                }
            }
        });

    }



}
