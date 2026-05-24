package com.nammashasane.ui.map;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nammashasane.R;
import com.nammashasane.data.model.Inscription;
import com.nammashasane.data.repository.InscriptionRepository;
import com.nammashasane.ui.story.StoryViewActivity;
import com.nammashasane.utils.Constants;

import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST = 100;

    private GoogleMap googleMap;
    private InscriptionRepository repository;
    private CardView cardInscriptionInfo;
    private TextView tvInscriptionName, tvInscriptionLocation;
    private Inscription selectedInscription;

    // Default to Karnataka center
    private static final LatLng KARNATAKA_CENTER = new LatLng(15.3173, 75.7139);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        repository = new InscriptionRepository();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        cardInscriptionInfo = findViewById(R.id.card_inscription_info);
        tvInscriptionName = findViewById(R.id.tv_inscription_name);
        tvInscriptionLocation = findViewById(R.id.tv_inscription_location);

        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        if (mapFragment != null) mapFragment.getMapAsync(this);

        FloatingActionButton fabLocation = findViewById(R.id.fab_my_location);
        fabLocation.setOnClickListener(v -> moveToMyLocation());

        findViewById(R.id.btn_view_story).setOnClickListener(v -> {
            if (selectedInscription != null) {
                Intent intent = new Intent(this, StoryViewActivity.class);
                intent.putExtra(Constants.EXTRA_INSCRIPTION_ID, selectedInscription.getId());
                intent.putExtra(Constants.EXTRA_INSCRIPTION_NAME, selectedInscription.getName());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);

        // Move to Karnataka
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(KARNATAKA_CENTER, 7f));

        checkLocationPermission();
        loadInscriptionMarkers();

        googleMap.setOnMarkerClickListener(marker -> {
            selectedInscription = (Inscription) marker.getTag();
            if (selectedInscription != null) {
                showInscriptionInfo(selectedInscription);
            }
            return false;
        });

        googleMap.setOnMapClickListener(latLng -> {
            cardInscriptionInfo.setVisibility(View.GONE);
            selectedInscription = null;
        });
    }

    private void loadInscriptionMarkers() {
        repository.getRecentInscriptions(new InscriptionRepository.OnInscriptionsFetched() {
            @Override
            public void onSuccess(List<Inscription> inscriptions) {
                runOnUiThread(() -> {
                    if (inscriptions.isEmpty()) {
                        addSampleMarkers();
                    } else {
                        for (Inscription inscription : inscriptions) {
                            if (inscription.getLatitude() != 0 || inscription.getLongitude() != 0) {
                                addMarker(inscription);
                            }
                        }
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> addSampleMarkers());
            }
        });
    }

    private void addSampleMarkers() {
        // Sample Karnataka inscriptions with real coordinates
        double[][] coords = {
                {12.8593, 76.6211}, // Shravana Belagola
                {15.9129, 75.8003}, // Aihole
                {15.3350, 76.4600}, // Hampi
                {12.3051, 76.6552}, // Belur
                {13.3409, 77.1014}  // Kolar
        };
        String[] names = {
                "Shravana Belagola", "Aihole Temple", "Hampi Shasane",
                "Belur Inscription", "Kolar Ancient Stone"
        };
        String[] dynasties = {"Ganga", "Chalukya", "Vijayanagara", "Hoysala", "Ganga"};

        for (int i = 0; i < names.length; i++) {
            Inscription inscription = new Inscription();
            inscription.setId("sample" + i);
            inscription.setName(names[i]);
            inscription.setLatitude(coords[i][0]);
            inscription.setLongitude(coords[i][1]);
            inscription.setDynasty(dynasties[i]);
            inscription.setLocationName(names[i] + ", Karnataka");
            addMarker(inscription);
        }
    }

    private void addMarker(Inscription inscription) {
        if (googleMap == null) return;
        LatLng position = new LatLng(inscription.getLatitude(), inscription.getLongitude());
        MarkerOptions options = new MarkerOptions()
                .position(position)
                .title(inscription.getName())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
        Marker marker = googleMap.addMarker(options);
        if (marker != null) marker.setTag(inscription);
    }

    private void showInscriptionInfo(Inscription inscription) {
        String name = inscription.getName();
        tvInscriptionName.setText((name != null && !name.isEmpty()) ? name : "Unnamed Shasane");
        tvInscriptionLocation.setText("📍 " + (inscription.getLocationName() != null ?
                inscription.getLocationName() : "Unknown location"));
        cardInscriptionInfo.setVisibility(View.VISIBLE);
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST);
        }
    }

    private void moveToMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && googleMap != null) {
            googleMap.setMyLocationEnabled(true);
            // The default location button on map handles this
            Toast.makeText(this, "Tap the location icon on the map", Toast.LENGTH_SHORT).show();
        } else {
            checkLocationPermission();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            checkLocationPermission();
        }
    }
}
