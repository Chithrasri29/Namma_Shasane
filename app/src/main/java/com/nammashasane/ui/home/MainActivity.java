package com.nammashasane.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.nammashasane.R;
import com.nammashasane.data.model.Inscription;
import com.nammashasane.data.repository.InscriptionRepository;
import com.nammashasane.ui.alert.PreservationAlertActivity;
import com.nammashasane.ui.map.MapActivity;
import com.nammashasane.ui.photo.PhotoTagActivity;
import com.nammashasane.ui.story.StoryViewActivity;
import com.nammashasane.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvRecent;
    private InscriptionAdapter adapter;
    private InscriptionRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        repository = new InscriptionRepository();

        setupRecyclerView();
        setupQuickActions();
        setupBottomNav();
        loadRecentInscriptions();
    }

    private void setupRecyclerView() {
        rvRecent = findViewById(R.id.rv_recent);
        rvRecent.setLayoutManager(new LinearLayoutManager(this));
        rvRecent.setNestedScrollingEnabled(false);

        adapter = new InscriptionAdapter(inscription -> {
            Intent intent = new Intent(this, StoryViewActivity.class);
            intent.putExtra(Constants.EXTRA_INSCRIPTION_ID, inscription.getId());
            intent.putExtra(Constants.EXTRA_INSCRIPTION_NAME, inscription.getName());
            startActivity(intent);
        });
        rvRecent.setAdapter(adapter);
    }

    private void setupQuickActions() {
        findViewById(R.id.card_find).setOnClickListener(v ->
                startActivity(new Intent(this, MapActivity.class)));

        findViewById(R.id.card_photo).setOnClickListener(v ->
                startActivity(new Intent(this, PhotoTagActivity.class)));

        findViewById(R.id.card_story).setOnClickListener(v ->
                startActivity(new Intent(this, MapActivity.class)));

        findViewById(R.id.card_alert).setOnClickListener(v ->
                startActivity(new Intent(this, PreservationAlertActivity.class)));
    }

    private void setupBottomNav() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setSelectedItemId(R.id.nav_home);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                return true;
            } else if (id == R.id.nav_map) {
                startActivity(new Intent(this, MapActivity.class));
                return true;
            } else if (id == R.id.nav_photo) {
                startActivity(new Intent(this, PhotoTagActivity.class));
                return true;
            } else if (id == R.id.nav_alert) {
                startActivity(new Intent(this, PreservationAlertActivity.class));
                return true;
            }
            return false;
        });
    }

    private void loadRecentInscriptions() {
        repository.getRecentInscriptions(new InscriptionRepository.OnInscriptionsFetched() {
            @Override
            public void onSuccess(List<Inscription> inscriptions) {
                runOnUiThread(() -> {
                    if (inscriptions.isEmpty()) {
                        // Show sample/demo data if Firestore is empty
                        adapter.setInscriptions(getSampleData());
                    } else {
                        adapter.setInscriptions(inscriptions);
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    // Show sample data on error (e.g., no internet)
                    adapter.setInscriptions(getSampleData());
                });
            }
        });
    }

    private List<Inscription> getSampleData() {
        List<Inscription> samples = new ArrayList<>();

        Inscription s1 = new Inscription();
        s1.setId("sample1");
        s1.setName("Shravana Belagola Shasane");
        s1.setLocationName("Hassan, Karnataka");
        s1.setDynasty("Ganga Dynasty");
        s1.setPeriod("~981 CE");

        Inscription s2 = new Inscription();
        s2.setId("sample2");
        s2.setName("Aihole Inscription");
        s2.setLocationName("Bagalkot, Karnataka");
        s2.setDynasty("Chalukya Dynasty");
        s2.setPeriod("~634 CE");

        Inscription s3 = new Inscription();
        s3.setId("sample3");
        s3.setName("Hampi Temple Shasane");
        s3.setLocationName("Vijayanagara, Karnataka");
        s3.setDynasty("Vijayanagara Empire");
        s3.setPeriod("~1336-1565 CE");

        samples.add(s1);
        samples.add(s2);
        samples.add(s3);
        return samples;
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRecentInscriptions();
    }
}
