package com.nammashasane.ui.photo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.nammashasane.R;
import com.nammashasane.data.model.Inscription;
import com.nammashasane.data.repository.InscriptionRepository;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PhotoTagActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST = 101;

    private ImageView ivPhotoPreview;
    private LinearLayout layoutPhotoPlaceholder;
    private TextInputEditText etName, etNotes;
    private TextView tvLocationStatus;
    private MaterialButton btnSubmit;
    private LinearProgressIndicator progressUpload;

    private Uri selectedPhotoUri;
    private Uri cameraPhotoUri;
    private double latitude = 0, longitude = 0;

    private InscriptionRepository repository;
    private FusedLocationProviderClient locationClient;

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedPhotoUri = result.getData().getData();
                    showPhotoPreview();
                }
            }
    );

    private final ActivityResultLauncher<Uri> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.TakePicture(),
            success -> {
                if (success && cameraPhotoUri != null) {
                    selectedPhotoUri = cameraPhotoUri;
                    showPhotoPreview();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_tag);

        repository = new InscriptionRepository();
        locationClient = LocationServices.getFusedLocationProviderClient(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        ivPhotoPreview = findViewById(R.id.iv_photo_preview);
        layoutPhotoPlaceholder = findViewById(R.id.layout_photo_placeholder);
        etName = findViewById(R.id.et_name);
        etNotes = findViewById(R.id.et_notes);
        tvLocationStatus = findViewById(R.id.tv_location_status);
        btnSubmit = findViewById(R.id.btn_submit);
        progressUpload = findViewById(R.id.progress_upload);

        findViewById(R.id.btn_take_photo).setOnClickListener(v -> takePhoto());
        findViewById(R.id.btn_choose_gallery).setOnClickListener(v -> pickFromGallery());
        findViewById(R.id.btn_get_location).setOnClickListener(v -> getLocation());
        btnSubmit.setOnClickListener(v -> submitInscription());
    }

    private void takePhoto() {
        try {
            File photoFile = createImageFile();
            cameraPhotoUri = FileProvider.getUriForFile(this,
                    getPackageName() + ".fileprovider", photoFile);
            cameraLauncher.launch(cameraPhotoUri);
        } catch (IOException e) {
            Toast.makeText(this, "Could not create photo file", Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        File storageDir = getExternalFilesDir(null);
        return File.createTempFile("SHASANE_" + timestamp, ".jpg", storageDir);
    }

    private void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private void showPhotoPreview() {
        if (selectedPhotoUri != null) {
            layoutPhotoPlaceholder.setVisibility(View.GONE);
            ivPhotoPreview.setVisibility(View.VISIBLE);
            Glide.with(this).load(selectedPhotoUri).centerCrop().into(ivPhotoPreview);
        }
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST);
            return;
        }
        tvLocationStatus.setText("Getting location…");
        locationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                tvLocationStatus.setText(String.format(Locale.US,
                        "%.5f, %.5f", latitude, longitude));
            } else {
                tvLocationStatus.setText("Could not get location");
            }
        });
    }

    private void submitInscription() {
        String name = etName.getText() != null ? etName.getText().toString().trim() : "";
        String notes = etNotes.getText() != null ? etNotes.getText().toString().trim() : "";

        if (selectedPhotoUri == null && name.isEmpty()) {
            Toast.makeText(this, "Please add a photo or inscription name", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSubmit.setEnabled(false);
        progressUpload.setVisibility(View.VISIBLE);

        String locationStr = (latitude != 0) ?
                String.format(Locale.US, "%.5f, %.5f", latitude, longitude) : "";

        Inscription inscription = new Inscription(
                name.isEmpty() ? "Unnamed Shasane" : name,
                locationStr,
                latitude, longitude,
                null, notes
        );

        repository.uploadInscriptionWithPhoto(selectedPhotoUri, inscription,
                new InscriptionRepository.OnOperationComplete() {
                    @Override
                    public void onSuccess(String id) {
                        runOnUiThread(() -> {
                            progressUpload.setVisibility(View.GONE);
                            Toast.makeText(PhotoTagActivity.this,
                                    "Added to map successfully! 🎉", Toast.LENGTH_LONG).show();
                            finish();
                        });
                    }

                    @Override
                    public void onError(String error) {
                        runOnUiThread(() -> {
                            progressUpload.setVisibility(View.GONE);
                            btnSubmit.setEnabled(true);
                            Toast.makeText(PhotoTagActivity.this,
                                    "Error: " + error, Toast.LENGTH_LONG).show();
                        });
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLocation();
        }
    }
}
