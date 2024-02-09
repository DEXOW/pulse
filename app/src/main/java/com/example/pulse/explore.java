package com.example.pulse;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.List;

public class explore extends AppCompatActivity {
    private FusedLocationProviderClient fusedLocationClient;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private TextView greeting, userLocation;
    private RelativeLayout exploreBtn, eventsBtn, savedBtn, SettingsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        greeting = findViewById(R.id.greeting);
        userLocation = findViewById(R.id.usrLocation);

        exploreBtn = findViewById(R.id.explore_nav);
        eventsBtn = findViewById(R.id.calender_nav);
        savedBtn = findViewById(R.id.saved_nav);
        SettingsBtn = findViewById(R.id.settings_nav);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != getPackageManager().PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                Geocoder geocoder = new Geocoder(this);
                try {
                    List<Address> address = geocoder.getFromLocation(latitude, longitude, 1);
                    userLocation.setText(address.get(0).getSubAdminArea() + ", " + address.get(0).getCountryName());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        });

        if (firebaseUser == null) {
            firebaseAuth.signOut();
            startActivity(new Intent(explore.this, login.class));
        }

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Toast.makeText(explore.this, "Press Back Again to Exit", Toast.LENGTH_SHORT).show();
                this.setEnabled(false);

                new android.os.Handler().postDelayed(
                        () -> this.setEnabled(true),
                        1000
                );
            }
        });

        greeting.setText("Welcome, " + (firebaseUser.getDisplayName() == null || firebaseUser.getDisplayName().isEmpty() ? "User" : (firebaseUser.getDisplayName()).substring(0, 1).toUpperCase() + (firebaseUser.getDisplayName()).substring(1)));

        exploreBtn.setOnClickListener(v -> {
            finish();
            startActivity(new Intent(explore.this, explore.class));
        });
        eventsBtn.setOnClickListener(v -> {
            finish();
            startActivity(new Intent(explore.this, events.class));
        });
        savedBtn.setOnClickListener(v -> {
            finish();
            startActivity(new Intent(explore.this, saved.class));
        });
        SettingsBtn.setOnClickListener(v -> {
            finish();
            startActivity(new Intent(explore.this, settings.class));
        });

        firebaseFirestore.collection("events").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                task.getResult().getDocuments().forEach(documentSnapshot -> {
                    // Append events to the view

                });
                Toast.makeText(this, "Events loaded", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error loading events", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
