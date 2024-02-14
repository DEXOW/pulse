package com.example.pulse;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout signUpBtn, loginBtn;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signUpBtn = findViewById(R.id.signupBtn);
        loginBtn = findViewById(R.id.loginBtn);

        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            Toast.makeText(this, String.format("Logged in as %s", firebaseAuth.getCurrentUser().getEmail()), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, ExploreActivity.class));
        }

        SharedPreferences sharedPreferences = getSharedPreferences("pulse", MODE_PRIVATE);
        int savedThemeMode = sharedPreferences.getInt("theme_pref", -1);

        if (savedThemeMode == -1) {
            // If no theme is saved, set the default theme based on the device's night mode setting
            if ((getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK) == android.content.res.Configuration.UI_MODE_NIGHT_YES) {
                sharedPreferences.edit().putInt("theme_pref", AppCompatDelegate.MODE_NIGHT_YES).apply();
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                // Set status bar color to black
                getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.navbarBg));
            } else {
                sharedPreferences.edit().putInt("theme_pref", AppCompatDelegate.MODE_NIGHT_NO).apply();
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        } else {
            // If a theme is saved, apply it
            AppCompatDelegate.setDefaultNightMode(savedThemeMode);
        }

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                startActivity(new Intent(MainActivity.this, SignupActivity.class));
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });
    }

}