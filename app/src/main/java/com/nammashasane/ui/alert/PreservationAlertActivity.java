package com.nammashasane.ui.alert;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
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
import com.nammashasane.data.model.PreservationAlert;
import com.nammashasane.data.repository.InscriptionRepository;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PreservationAlertActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST = 102;

    private ImageView ivDamagePhoto;
    private LinearLayout layoutPhotoPlaceholder;
    private RadioGroup rgDamageType;
    private TextInputEditText etNotes;
    private TextView tvLocationStatus;
    private MaterialButton btnSubmitAlert;
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
        setContentView(R.layout.activity_preservation_alert);

        repository = new InscriptionRepository();
        locationClient = LocationServices.getFusedLocationProviderClient(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        ivDamagePhoto = findViewById(R.id.iv_damage_photo);
        layoutPhotoPlaceholder = findViewById(R.id.layout_photo_placeholder);
        rgDamageType = findViewById(R.id.rg_damage_type);
        etNotes = findViewById(R.id.et_notes);
        tvLocationStatus = findViewById(R.id.tv_location_status);
        btnSubmitAlert = findViewById(R.id.btn_submit_alert);
        progressUpload = findViewById(R.id.progress_upload);

        // Photo tapping
        ivDamagePhoto.setOnClickListener(v -> takePhoto());
        layoutPhotoPlaceholder.setOnClickListener(v -> takePhoto());

        findViewById(R.id.btn_damage_camera).setOnClickListener(v -> takePhoto());
        findViewById(R.id.btn_damage_gallery).setOnClickListener(v -> pickFromGallery());
        findViewById(R.id.btn_get_location).setOnClickListener(v -> getLocation());
        btnSubmitAlert.setOnClickListener(v -> submitAlert());
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
        return File.createTempFile("ALERT_" + timestamp, ".jpg", storageDir);
    }

    private void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private void showPhotoPreview() {
        if (selectedPhotoUri != null) {
            layoutPhotoPlaceholder.setVisibility(View.GONE);
            ivDamagePhoto.setVisibility(View.VISIBLE);
            Glide.with(this).load(selectedPhotoUri).centerCrop().into(ivDamagePhoto);
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
                tvLocationStatus.setText("Location unavailable");
            }
        });
    }

    private String getSelectedDamageType() {
        int id = rgDamageType.getCheckedRadioButtonId();
        if (id == R.id.rb_painted) return "Painted Over";
        if (id == R.id.rb_broken) return "Broken / Cracked";
        if (id == R.id.rb_buried) return "Buried / Hidden";
        if (id == R.id.rb_other) return "Other";
        return "Unknown";
    }

    private void submitAlert() {
        String damageType = getSelectedDamageType();
        String notes = etNotes.getText() != null ? etNotes.getText().toString().trim() : "";

        btnSubmitAlert.setEnabled(false);
        progressUpload.setVisibility(View.VISIBLE);

        PreservationAlert alert = new PreservationAlert(damageType, null, latitude, longitude, notes);

        repository.uploadAlertWithPhoto(selectedPhotoUri, alert,
                new InscriptionRepository.OnOperationComplete() {
                    @Override
                    public void onSuccess(String id) {
                        runOnUiThread(() -> {
                            progressUpload.setVisibility(View.GONE);
                            Toast.makeText(PreservationAlertActivity.this,
                                    "Report submitted! Thank you for protecting our heritage 🙏",
                                    Toast.LENGTH_LONG).show();
                            finish();
                        });
                    }

                    @Override
                    public void onError(String error) {
                        runOnUiThread(() -> {
                            progressUpload.setVisibility(View.GONE);
                            btnSubmitAlert.setEnabled(true);
                            Toast.makeText(PreservationAlertActivity.this,
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
