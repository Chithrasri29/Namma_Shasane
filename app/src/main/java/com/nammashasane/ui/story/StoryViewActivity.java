package com.nammashasane.ui.story;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.nammashasane.R;
import com.nammashasane.data.model.Inscription;
import com.nammashasane.data.repository.InscriptionRepository;
import com.nammashasane.utils.Constants;
import com.nammashasane.utils.OpenRouterClient;

import java.util.List;

public class StoryViewActivity extends AppCompatActivity {

    private ImageView ivInscriptionImage;
    private TextView tvInscriptionTitle, tvLocationName, tvDynasty, tvPeriod,
            tvTranslation, tvGiftLaw;
    private CircularProgressIndicator progressTranslate;
    private MaterialButton btnTranslate;

    private InscriptionRepository repository;
    private OpenRouterClient openRouterClient;
    private Inscription currentInscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_view);

        repository = new InscriptionRepository();
        openRouterClient = new OpenRouterClient();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        ivInscriptionImage = findViewById(R.id.iv_inscription_image);
        tvInscriptionTitle = findViewById(R.id.tv_inscription_title);
        tvLocationName = findViewById(R.id.tv_location_name);
        tvDynasty = findViewById(R.id.tv_dynasty);
        tvPeriod = findViewById(R.id.tv_period);
        tvTranslation = findViewById(R.id.tv_translation);
        tvGiftLaw = findViewById(R.id.tv_gift_law);
        progressTranslate = findViewById(R.id.progress_translate);
        btnTranslate = findViewById(R.id.btn_translate);

        btnTranslate.setOnClickListener(v -> triggerTranslation());

        String inscriptionId = getIntent().getStringExtra(Constants.EXTRA_INSCRIPTION_ID);
        String inscriptionName = getIntent().getStringExtra(Constants.EXTRA_INSCRIPTION_NAME);

        if (inscriptionId != null && !inscriptionId.startsWith("sample")) {
            loadInscription(inscriptionId);
        } else {
            // Show sample / demo content
            showSampleStory(inscriptionId, inscriptionName);
        }
    }

    private void loadInscription(String id) {
        progressTranslate.setVisibility(View.VISIBLE);
        repository.getInscriptionById(id, new InscriptionRepository.OnInscriptionsFetched() {
            @Override
            public void onSuccess(List<Inscription> inscriptions) {
                if (!inscriptions.isEmpty()) {
                    currentInscription = inscriptions.get(0);
                    runOnUiThread(() -> populateUI(currentInscription));
                }
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    progressTranslate.setVisibility(View.GONE);
                    Toast.makeText(StoryViewActivity.this,
                            "Could not load inscription", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void populateUI(Inscription inscription) {
        progressTranslate.setVisibility(View.GONE);

        String name = inscription.getName();
        tvInscriptionTitle.setText((name != null && !name.isEmpty()) ? name : "Unnamed Shasane");

        String loc = inscription.getLocationName();
        tvLocationName.setText("📍 " + ((loc != null && !loc.isEmpty()) ? loc : "Karnataka"));

        tvDynasty.setText(inscription.getDynasty() != null ? inscription.getDynasty() : "—");
        tvPeriod.setText(inscription.getPeriod() != null ? inscription.getPeriod() : "—");

        if (inscription.getPhotoUrl() != null && !inscription.getPhotoUrl().isEmpty()) {
            Glide.with(this)
                    .load(inscription.getPhotoUrl())
                    .centerCrop()
                    .placeholder(R.drawable.ic_photo_placeholder)
                    .into(ivInscriptionImage);
        }

        if (inscription.getTranslationKannada() != null &&
                !inscription.getTranslationKannada().isEmpty()) {
            tvTranslation.setText(inscription.getTranslationKannada());
            tvGiftLaw.setText(inscription.getGiftLaw() != null ? inscription.getGiftLaw() : "—");
            btnTranslate.setVisibility(View.GONE);
        } else {
            tvTranslation.setText("Tap the button below to decode this inscription with AI.");
            btnTranslate.setVisibility(View.VISIBLE);
        }
    }

    private void showSampleStory(String sampleId, String name) {
        progressTranslate.setVisibility(View.GONE);

        // Populate with rich demo content based on sampleId
        if ("sample0".equals(sampleId) || (name != null && name.contains("Shravana"))) {
            tvInscriptionTitle.setText("Shravana Belagola Shasane");
            tvLocationName.setText("📍 Hassan District, Karnataka");
            tvDynasty.setText("Ganga Dynasty");
            tvPeriod.setText("~981 CE");
            tvTranslation.setText("ಈ ಶಾಸನವು ಚಾವುಂಡರಾಯ ಮತ್ತು ಅವನ ತಾಯಿ ಅಜ್ಜಲದೇವಿ ಇವರ ಬಗ್ಗೆ ತಿಳಿಸುತ್ತದೆ. ಗಂಗ ಸಾಮ್ರಾಜ್ಯದ ಸೇನಾಪತಿ ಚಾವುಂಡರಾಯ ೫೭ ಅಡಿ ಎತ್ತರದ ಗೊಮ್ಮಟೇಶ್ವರ ವಿಗ್ರಹವನ್ನು ನಿರ್ಮಿಸಿದನು. ಇದು ಭಾರತದ ಇತಿಹಾಸದಲ್ಲಿ ಒಂದು ಮಹತ್ವದ ಘಟನೆ.");
            tvGiftLaw.setText("ಭಗವಾನ್ ಬಾಹುಬಲಿಯ ಸ್ಮರಣಾರ್ಥ ಬೃಹತ್ ಏಕಶಿಲಾ ಮೂರ್ತಿ ನಿರ್ಮಾಣ — ವಿಶ್ವದ ಅತಿ ಎತ್ತರದ ಏಕಶಿಲಾ ಮೂರ್ತಿ.");
        } else if ("sample1".equals(sampleId) || (name != null && name.contains("Aihole"))) {
            tvInscriptionTitle.setText("Aihole Inscription");
            tvLocationName.setText("📍 Bagalkot District, Karnataka");
            tvDynasty.setText("Chalukya Dynasty");
            tvPeriod.setText("~634 CE");
            tvTranslation.setText("ಈ ಶಾಸನವು ಪುಲಕೇಶಿ II ನ ಆಳ್ವಿಕೆಯ ಕಾಲದಲ್ಲಿ ರಚಿತವಾಗಿದೆ. ಕವಿ ರವಿಕೀರ್ತಿ ರಚಿಸಿದ ಈ ಶಾಸನ ಸಂಸ್ಕೃತ ಭಾಷೆಯಲ್ಲಿದ್ದು ಪುಲಕೇಶಿಯ ಮಹಾನ್ ವಿಜಯಗಳನ್ನು ವರ್ಣಿಸುತ್ತದೆ. ಹರ್ಷ ಚಕ್ರವರ್ತಿಯ ವಿರುದ್ಧ ಗೆದ್ದ ಯುದ್ಧವನ್ನು ವಿಶೇಷವಾಗಿ ಉಲ್ಲೇಖಿಸಲಾಗಿದೆ.");
            tvGiftLaw.setText("ಪ್ರಾಂತೀಯ ದೇವಾಲಯ ನಿರ್ಮಾಣಕ್ಕೆ ಭೂಮಿ ದಾನ ಮತ್ತು ಬ್ರಾಹ್ಮಣರಿಗೆ ಗ್ರಾಮ ನೀಡಿಕೆ.");
        } else {
            tvInscriptionTitle.setText(name != null ? name : "Ancient Shasane");
            tvLocationName.setText("📍 Karnataka");
            tvDynasty.setText("Vijayanagara Empire");
            tvPeriod.setText("~14th–16th Century CE");
            tvTranslation.setText("ಈ ಶಾಸನವು ವಿಜಯನಗರ ಸಾಮ್ರಾಜ್ಯದ ಕಾಲಕ್ಕೆ ಸೇರಿದ್ದಾಗಿದೆ. ರಾಜ ಕೃಷ್ಣದೇವರಾಯ ಇಲ್ಲಿ ಕಾಣಿಕೆ ನೀಡಿದ ಬಗ್ಗೆ ಉಲ್ಲೇಖವಿದೆ. ಈ ಪ್ರದೇಶ ಒಂದು ಮಹತ್ವದ ವ್ಯಾಪಾರ ಕೇಂದ್ರವಾಗಿತ್ತು.");
            tvGiftLaw.setText("ದೇವಾಲಯ ನವೀಕರಣ ಮತ್ತು ವಾರ್ಷಿಕ ಉತ್ಸವಕ್ಕೆ ಭೂಮಿ ದಾನ.");
        }

        btnTranslate.setVisibility(View.GONE);

        // Build a temp inscription for AI decode if needed
        currentInscription = new Inscription();
        currentInscription.setId(sampleId);
        currentInscription.setName(tvInscriptionTitle.getText().toString());
        currentInscription.setNotes(tvTranslation.getText().toString());
    }

    private void triggerTranslation() {
        if (currentInscription == null) return;

        String context = currentInscription.getNotes();
        if (context == null || context.isEmpty()) {
            context = "Karnataka ancient inscription. " + currentInscription.getName();
        }

        btnTranslate.setEnabled(false);
        progressTranslate.setVisibility(View.VISIBLE);
        tvTranslation.setText("Decoding with AI…");

        final String finalContext = context;
        openRouterClient.translateInscription(finalContext, new OpenRouterClient.OnTranslationResult() {
            @Override
            public void onSuccess(String translation, String dynasty, String period, String giftLaw) {
                runOnUiThread(() -> {
                    progressTranslate.setVisibility(View.GONE);
                    btnTranslate.setVisibility(View.GONE);
                    tvTranslation.setText(translation);
                    tvDynasty.setText(dynasty);
                    tvPeriod.setText(period);
                    tvGiftLaw.setText(giftLaw);

                    // Save back to Firestore if it's a real inscription
                    if (currentInscription.getId() != null &&
                            !currentInscription.getId().startsWith("sample")) {
                        repository.updateTranslation(currentInscription.getId(),
                                translation, dynasty, period, giftLaw);
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    progressTranslate.setVisibility(View.GONE);
                    btnTranslate.setEnabled(true);
                    tvTranslation.setText("Could not decode. Check your OpenRouter API key and internet connection.");
                    Toast.makeText(StoryViewActivity.this,
                            "AI Error: " + error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }
}
