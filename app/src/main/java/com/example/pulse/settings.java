package com.example.pulse;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class settings extends AppCompatActivity {
    private TextView username;
    private RelativeLayout exploreBtn, eventsBtn, savedBtn, SettingsBtn, logoutBtn;
    private ImageButton changeImage;
    private FirebaseAuth firebaseAuth;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        exploreBtn = findViewById(R.id.explore_nav);
        eventsBtn = findViewById(R.id.calender_nav);
        savedBtn = findViewById(R.id.saved_nav);
        SettingsBtn = findViewById(R.id.settings_nav);

        logoutBtn = findViewById(R.id.logOut);
        changeImage = findViewById(R.id.changeImage);

        username = findViewById(R.id.username);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser == null) {
            firebaseAuth.signOut();
            startActivity(new Intent(settings.this, login.class));
        }

        username.setText((firebaseUser.getDisplayName() == null || firebaseUser.getDisplayName().isEmpty() ? "User" : (firebaseUser.getDisplayName()).substring(0, 1).toUpperCase() + (firebaseUser.getDisplayName()).substring(1)));

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Toast.makeText(settings.this, "Press Back Again to Exit", Toast.LENGTH_SHORT).show();
                this.setEnabled(false);

                new android.os.Handler().postDelayed(
                        () -> this.setEnabled(true),
                        1000
                );
            }
        });

        exploreBtn.setOnClickListener(v -> {
            finish();
            startActivity(new Intent(settings.this, explore.class));
        });
        eventsBtn.setOnClickListener(v -> {
            finish();
            startActivity(new Intent(settings.this, events.class));
        });
        savedBtn.setOnClickListener(v -> {
            finish();
            startActivity(new Intent(settings.this, saved.class));
        });
        SettingsBtn.setOnClickListener(v -> {
            finish();
            startActivity(new Intent(settings.this, settings.class));
        });

        logoutBtn.setOnClickListener(v -> {
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(settings.this, login.class));
        });

        changeImage.setOnClickListener(v -> {
            if (checkSelfPermission(android.Manifest.permission.CAMERA) != getPackageManager().PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.CAMERA}, 1);
            }
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivity(cameraIntent);
        });
    }

    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }
}
