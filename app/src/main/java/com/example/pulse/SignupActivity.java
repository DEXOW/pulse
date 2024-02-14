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
import com.google.firebase.auth.UserProfileChangeRequest;

public class SignupActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private RelativeLayout signUpBtn;
    private EditText usernameInput, emailInput, passwordInput, confirmPasswordInput;
    private TextView switchToLoginBtn;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        progressBar = findViewById(R.id.progressBar);

        switchToLoginBtn = findViewById(R.id.switchToLogIn);
        signUpBtn = findViewById(R.id.signUpBtn);

        usernameInput = findViewById(R.id.usernameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confPasswordInput);

        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(SignupActivity.this, ExploreActivity.class));
        }

        switchToLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usernameVal = usernameInput.getText().toString().trim();
                String emailVal = emailInput.getText().toString().trim();
                String pass = passwordInput.getText().toString();
                String confirmPass = confirmPasswordInput.getText().toString();

                if (usernameVal.isEmpty()) {
                    usernameInput.setError("Please enter your username");
                    usernameInput.requestFocus();
                } else if (emailVal.isEmpty()) {
                    emailInput.setError("Please enter your email");
                    emailInput.requestFocus();
                } else if (pass.isEmpty()) {
                    passwordInput.setError("Please enter your password");
                    passwordInput.requestFocus();
                } else if (confirmPass.isEmpty()) {
                    confirmPasswordInput.setError("Please confirm your password");
                    confirmPasswordInput.requestFocus();
                } else if (!pass.equals(confirmPass)) {
                    confirmPasswordInput.setError("Passwords do not match");
                    confirmPasswordInput.requestFocus();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    firebaseAuth.createUserWithEmailAndPassword(emailVal, pass).addOnCompleteListener(task -> {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            firebaseAuth.getCurrentUser().updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(usernameVal).build());
                            firebaseAuth.getCurrentUser().sendEmailVerification();

                            finish();
                            Toast.makeText(SignupActivity.this, "Account created successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignupActivity.this, ExploreActivity.class));
                        } else {
                            if (task.getException().getMessage().contains("email address is already in use")) {
                                emailInput.setError("Email already in use");
                                emailInput.requestFocus();
                            } else if (task.getException().getMessage().contains("The email address is badly formatted")) {
                                emailInput.setError("Invalid email address");
                                emailInput.requestFocus();
                            } else if (task.getException().getMessage().contains("The given password is invalid")) {
                                passwordInput.setError("Invalid password");
                                passwordInput.requestFocus();
                            } else {
                                Toast.makeText(SignupActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
