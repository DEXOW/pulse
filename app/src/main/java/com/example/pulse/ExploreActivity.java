package com.example.pulse;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExploreActivity extends AppCompatActivity {
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
                    Log.d("Location", address.get(0).toString());
                    userLocation.setText(address.get(0).getSubAdminArea() + ", " + address.get(0).getCountryName());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        });

        if (firebaseUser == null) {
            firebaseAuth.signOut();
            startActivity(new Intent(ExploreActivity.this, LoginActivity.class));
        }

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Toast.makeText(ExploreActivity.this, "Press Back Again to Exit", Toast.LENGTH_SHORT).show();
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
            startActivity(new Intent(ExploreActivity.this, ExploreActivity.class));
        });
        eventsBtn.setOnClickListener(v -> {
            finish();
            startActivity(new Intent(ExploreActivity.this, EventsActivity.class));
        });
        savedBtn.setOnClickListener(v -> {
            finish();
            startActivity(new Intent(ExploreActivity.this, SavedActivity.class));
        });
        SettingsBtn.setOnClickListener(v -> {
            finish();
            startActivity(new Intent(ExploreActivity.this, SettingsActivity.class));
        });

        firebaseFirestore.collection("events").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<DocumentSnapshot> events = task.getResult().getDocuments();
                List<DocumentSnapshot> eventsToday = new ArrayList<>();
                List<DocumentSnapshot> eventsUpcoming = new ArrayList<>();
                Date currentDate = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String formattedDate = df.format(currentDate);

                for (DocumentSnapshot event : events) {
                    Timestamp eventDate = (Timestamp) event.get("start");
                    if (eventDate.equals(formattedDate)) {
                        eventsToday.add(event);
                    } else if (eventDate.compareTo(new Timestamp(currentDate)) > 0) {
                        eventsUpcoming.add(event);
                    }
                }
                EventAdapter eventsTodayAdapter = new EventAdapter(eventsToday);
                EventAdapter eventsUpcomingAdapter = new EventAdapter(eventsUpcoming);

                RecyclerView eventsRecyclerView1 = findViewById(R.id.scrollView1);
                RecyclerView eventsRecyclerView2 = findViewById(R.id.scrollView2);

                eventsRecyclerView1.setLayoutManager(new GridLayoutManager(this, 2));
                eventsRecyclerView2.setLayoutManager(new GridLayoutManager(this, 2));

                eventsRecyclerView1.setAdapter(eventsTodayAdapter);
                eventsRecyclerView2.setAdapter(eventsUpcomingAdapter);
            } else {
                Toast.makeText(this, "Error loading events", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
