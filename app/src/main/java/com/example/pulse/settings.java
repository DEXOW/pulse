package com.example.pulse;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class settings extends AppCompatActivity {
    private TextView username;
    private ImageView profileImage;
    private RelativeLayout exploreBtn, eventsBtn, savedBtn, SettingsBtn, logoutBtn;
    private ImageButton changeImage;
    private FirebaseAuth firebaseAuth;
    private FirebaseStorage firebaseStorage;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        exploreBtn = findViewById(R.id.explore_nav);
        eventsBtn = findViewById(R.id.calender_nav);
        savedBtn = findViewById(R.id.saved_nav);
        SettingsBtn = findViewById(R.id.settings_nav);

        logoutBtn = findViewById(R.id.logOut);
        changeImage = findViewById(R.id.changeImage);
        profileImage = findViewById(R.id.profileImage);

        username = findViewById(R.id.username);

        firebaseStorage = FirebaseStorage.getInstance();


        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser == null) {
            firebaseAuth.signOut();
            startActivity(new Intent(settings.this, login.class));
        }

        username.setText((firebaseUser.getDisplayName() == null || firebaseUser.getDisplayName().isEmpty() ? "User" : (firebaseUser.getDisplayName()).substring(0, 1).toUpperCase() + (firebaseUser.getDisplayName()).substring(1)));
        try {
            String photoUrl = firebaseUser.getPhotoUrl().toString();
            if (photoUrl.isEmpty()) {
                profileImage.setImageResource(R.drawable.default_user);
                Toast.makeText(this, "No photo", Toast.LENGTH_SHORT).show();
            }
            Toast.makeText(this, photoUrl, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        profileImage.setImageURI(firebaseUser.getPhotoUrl());

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
        });
    }
    ActivityResultLauncher<Intent> getCameraImage = registerForActivityResult(
    new ActivityResultContracts.StartActivityForResult(),
    new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                // There are no request codes
                Intent data = result.getData();
                assert data != null;
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                // Store the image in firebase cloud storage
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.JPEG,  100, baos);
                byte[] imgData = baos.toByteArray();

                StorageReference storageReference = firebaseStorage.getReference().child("profileImages").child(firebaseAuth.getUid());
                UploadTask uploadTask = storageReference.putBytes(imgData);

                uploadTask.addOnFailureListener(e -> {
                    Toast.makeText(settings.this, "Image Upload Failed", Toast.LENGTH_SHORT).show();
                }).addOnSuccessListener(taskSnapshot -> {
                    storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        firebaseAuth.getCurrentUser().updateProfile(new com.google.firebase.auth.UserProfileChangeRequest.Builder().setPhotoUri(uri).build());
                    });
                    profileImage.setImageBitmap(photo);
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
