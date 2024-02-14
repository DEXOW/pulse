package com.example.pulse;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EventsActivity extends AppCompatActivity {
    private RelativeLayout exploreBtn, eventsBtn, savedBtn, SettingsBtn;
    private ImageButton backBtn;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        backBtn = findViewById(R.id.back_btn);
        exploreBtn = findViewById(R.id.explore_nav);
        eventsBtn = findViewById(R.id.calender_nav);
        savedBtn = findViewById(R.id.saved_nav);
        SettingsBtn = findViewById(R.id.settings_nav);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser == null) {
            firebaseAuth.signOut();
            startActivity(new Intent(EventsActivity.this, LoginActivity.class));
        }

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Toast.makeText(EventsActivity.this, "Press Back Again to Exit", Toast.LENGTH_SHORT).show();
                this.setEnabled(false);

                new android.os.Handler().postDelayed(
                        () -> this.setEnabled(true),
                        1000
                );
            }
        });

        backBtn.setOnClickListener(v -> {
            finish();
            startActivity(new Intent(EventsActivity.this, ExploreActivity.class));
        });
        exploreBtn.setOnClickListener(v -> {
            finish();
            startActivity(new Intent(EventsActivity.this, ExploreActivity.class));
        });
        eventsBtn.setOnClickListener(v -> {
            finish();
            startActivity(new Intent(EventsActivity.this, EventsActivity.class));
        });
        savedBtn.setOnClickListener(v -> {
            finish();
            startActivity(new Intent(EventsActivity.this, SavedActivity.class));
        });
        SettingsBtn.setOnClickListener(v -> {
            finish();
            startActivity(new Intent(EventsActivity.this, SettingsActivity.class));
        });
    }
}
