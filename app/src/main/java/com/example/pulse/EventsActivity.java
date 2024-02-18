package com.example.pulse;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EventsActivity extends AppCompatActivity {
    private RelativeLayout exploreBtn, eventsBtn, savedBtn, SettingsBtn;
    private ImageButton backBtn;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FusedLocationProviderClient fusedLocationClient;
    private List<DocumentSnapshot> events;
    private RadioButton filter1, filter2, filter3, filter4, filter5;
    private String district, city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        backBtn = findViewById(R.id.back_btn);
        exploreBtn = findViewById(R.id.explore_nav);
        eventsBtn = findViewById(R.id.calender_nav);
        savedBtn = findViewById(R.id.saved_nav);
        SettingsBtn = findViewById(R.id.settings_nav);

        filter1 = findViewById(R.id.filter1);
        filter2 = findViewById(R.id.filter2);
        filter3 = findViewById(R.id.filter3);
        filter4 = findViewById(R.id.filter4);
        filter5 = findViewById(R.id.filter5);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        getLocation();

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

        firebaseFirestore.collection("events").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                events = task.getResult().getDocuments();
                setEvents(events);
            } else {
                Toast.makeText(this, "Error loading events", Toast.LENGTH_SHORT).show();
            }
        });

        filter1.setOnClickListener(v -> {
            List<DocumentSnapshot> filteredEvents = new ArrayList<>();
            for (DocumentSnapshot event : events) {
                Log.d("Location", event.getString("location") + " " + city + " " + district);
                if (event.getString("location") != null && (event.getString("location").contains(city) || event.getString("location").contains(district))) {
                    filteredEvents.add(event);
                }
            }
            setEvents(filteredEvents);
        });
        filter2.setOnClickListener(v -> {
            List<DocumentSnapshot> filteredEvents = new ArrayList<>();
            for (DocumentSnapshot event : events) {
                if (event.getString("catergory") != null && event.getString("catergory").equals("sports")) {
                    filteredEvents.add(event);
                }
            }
            setEvents(filteredEvents);
        });
        filter3.setOnClickListener(v -> {
            List<DocumentSnapshot> filteredEvents = new ArrayList<>();
            for (DocumentSnapshot event : events) {
                if (event.getString("catergory") != null && event.getString("catergory").equals("music")) {
                    filteredEvents.add(event);
                }
            }
            setEvents(filteredEvents);
        });
        filter4.setOnClickListener(v -> {
            List<DocumentSnapshot> filteredEvents = new ArrayList<>();
            for (DocumentSnapshot event : events) {
                if (event.getString("catergory") != null && event.getString("catergory").equals("food")) {
                    filteredEvents.add(event);
                }
            }
            setEvents(filteredEvents);
        });
        filter5.setOnClickListener(v -> {
            List<DocumentSnapshot> filteredEvents = new ArrayList<>();
            for (DocumentSnapshot event : events) {
                if (event.getString("catergory") != null && event.getString("catergory").equals("wine")) {
                    filteredEvents.add(event);
                }
            }
            setEvents(filteredEvents);
        });
    }

    private void setEvents(List<DocumentSnapshot> events) {
        EventAdapter eventsTodayAdapter = new EventAdapter(events);
        RecyclerView eventsRecyclerView1 = findViewById(R.id.scrollView1);
        eventsRecyclerView1.setLayoutManager(new GridLayoutManager(this, 2));
        eventsRecyclerView1.setAdapter(eventsTodayAdapter);
    }

    private void getLocation() {
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
                    city = address.get(0).getSubAdminArea();
                    district = address.get(0).getCountryName();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        });
    }
}
