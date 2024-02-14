package com.example.pulse;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.bumptech.glide.Glide;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class SettingsActivity extends AppCompatActivity {
    private TextView username;
    private ImageView profileImage;
    private RelativeLayout exploreBtn, eventsBtn, savedBtn, SettingsBtn, logoutBtn;
    private ImageButton changeImage, backBtn;
    private SwitchMaterial darkModeSwitch;
    private FirebaseAuth firebaseAuth;
    private FirebaseStorage firebaseStorage;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SharedPreferences sharedPreferences = getSharedPreferences("pulse", MODE_PRIVATE);

        backBtn = findViewById(R.id.back_btn);
        exploreBtn = findViewById(R.id.explore_nav);
        eventsBtn = findViewById(R.id.calender_nav);
        savedBtn = findViewById(R.id.saved_nav);
        SettingsBtn = findViewById(R.id.settings_nav);

        darkModeSwitch = findViewById(R.id.darkModeSwitch);
        logoutBtn = findViewById(R.id.logOut);

        changeImage = findViewById(R.id.changeImage);
        profileImage = findViewById(R.id.profileImage);
        username = findViewById(R.id.username);

        firebaseStorage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();


        if (firebaseUser == null) {
            try {
                firebaseAuth.signOut();
            } catch (Exception e) {
                Log.d("Error", "Error: " + e.getMessage());
            }
            startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
        }

        darkModeSwitch.setChecked(sharedPreferences.getInt("theme_pref", AppCompatDelegate.MODE_NIGHT_NO) == AppCompatDelegate.MODE_NIGHT_YES);
        fetchProfileImage();
        fetchDetails();

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Toast.makeText(SettingsActivity.this, "Press Back Again to Exit", Toast.LENGTH_SHORT).show();
                this.setEnabled(false);

                new android.os.Handler().postDelayed(
                        () -> this.setEnabled(true),
                        1000
                );
            }
        });

        backBtn.setOnClickListener(v -> {
            finish();
            startActivity(new Intent(SettingsActivity.this, ExploreActivity.class));
        });
        exploreBtn.setOnClickListener(v -> {
            finish();
            startActivity(new Intent(SettingsActivity.this, ExploreActivity.class));
        });
        eventsBtn.setOnClickListener(v -> {
            finish();
            startActivity(new Intent(SettingsActivity.this, EventsActivity.class));
        });
        savedBtn.setOnClickListener(v -> {
            finish();
            startActivity(new Intent(SettingsActivity.this, SavedActivity.class));
        });
        SettingsBtn.setOnClickListener(v -> {
            finish();
            startActivity(new Intent(SettingsActivity.this, SettingsActivity.class));
        });

        darkModeSwitch.setOnClickListener(view -> {
            if (darkModeSwitch.isChecked()) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                sharedPreferences.edit().putInt("theme_pref", AppCompatDelegate.MODE_NIGHT_YES).apply();
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                sharedPreferences.edit().putInt("theme_pref", AppCompatDelegate.MODE_NIGHT_NO).apply();
            }
        });

        logoutBtn.setOnClickListener(v -> {
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
        });

        changeImage.setOnClickListener(v -> {
            changeImage();
        });
        profileImage.setOnClickListener(v -> {
            changeImage();
        });
    }
    private void changeImage(){
        if (checkSelfPermission(android.Manifest.permission.CAMERA) != getPackageManager().PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.CAMERA}, 1);
        }
        if (!checkCameraHardware(this)) {
            Toast.makeText(this, "No Camera Found", Toast.LENGTH_SHORT).show();
        }

        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            cameraIntent.putExtra("return-data", true);
            getCameraImage.launch(cameraIntent);
        } catch (Exception err) {
            err.printStackTrace();
        }
    }
    private void fetchProfileImage() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        try {
            Uri photoUrl = firebaseUser.getPhotoUrl();
            if (photoUrl.toString().isEmpty()) {
                Glide.with(this)
                        .load(R.drawable.default_user)
                        .circleCrop()
                        .into(profileImage);
            } else {
                Glide.with(this)
                        .load(photoUrl)
                        .circleCrop()
                        .into(profileImage);
            }
        } catch (Exception e) {
            Log.d("Error", "Error: " + e.getMessage());
        }
    }
    private void fetchDetails() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        username.setText((firebaseUser.getDisplayName() == null || firebaseUser.getDisplayName().isEmpty() ? "User" : (firebaseUser.getDisplayName()).substring(0, 1).toUpperCase() + (firebaseUser.getDisplayName()).substring(1)));
    }
    ActivityResultLauncher<Intent> getCameraImage = registerForActivityResult(
    new ActivityResultContracts.StartActivityForResult(),
    new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                assert data != null;
                Bitmap photo = (Bitmap) data.getExtras().get("data");

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.JPEG,  100, baos);
                byte[] imgData = baos.toByteArray();

                StorageReference storageReference = firebaseStorage.getReference().child("profileImages").child(firebaseAuth.getUid());
                UploadTask uploadTask = storageReference.putBytes(imgData);

                uploadTask.addOnFailureListener(e -> {
                    Toast.makeText(SettingsActivity.this, "Image Upload Failed", Toast.LENGTH_SHORT).show();
                }).addOnSuccessListener(taskSnapshot -> {
                    storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        firebaseAuth.getCurrentUser().updateProfile(new com.google.firebase.auth.UserProfileChangeRequest.Builder().setPhotoUri(uri).build()).addOnSuccessListener(
                                aVoid -> {
                                    fetchProfileImage();
                                    Toast.makeText(SettingsActivity.this, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
                                }
                        ).addOnFailureListener(e -> {
                            Toast.makeText(SettingsActivity.this, "Image Upload Failed", Toast.LENGTH_SHORT).show();
                        });
                    });
                });
            }
        }
    });
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
