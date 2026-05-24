package com.nammashasane.ui.onboarding;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.button.MaterialButton;
import com.nammashasane.R;
import com.nammashasane.ui.home.MainActivity;
import com.nammashasane.utils.Constants;

public class OnboardingActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private MaterialButton btnNext;
    private TextView tvSkip;
    private LinearLayout indicatorLayout;

    private final String[] titles = {"Discover Inscriptions", "Tag & Translate", "Preserve History"};
    private final String[] descriptions = {
            "Explore ancient Shasanas found on temple walls and village stones across Karnataka.",
            "Photograph an inscription, add it to our map, and get an AI-powered translation in modern Kannada.",
            "Report damaged or at-risk stones so we can protect Karnataka's heritage for future generations."
    };
    private final String[] emojis = {"🪨", "📸", "🛡️"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        viewPager = findViewById(R.id.viewpager);
        btnNext = findViewById(R.id.btn_next);
        tvSkip = findViewById(R.id.tv_skip);
        indicatorLayout = findViewById(R.id.indicator_layout);

        OnboardingAdapter adapter = new OnboardingAdapter(titles, descriptions, emojis);
        viewPager.setAdapter(adapter);

        setupIndicators();
        updateIndicator(0);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateIndicator(position);
                if (position == titles.length - 1) {
                    btnNext.setText(getString(R.string.get_started));
                    tvSkip.setVisibility(View.INVISIBLE);
                } else {
                    btnNext.setText(getString(R.string.next));
                    tvSkip.setVisibility(View.VISIBLE);
                }
            }
        });

        btnNext.setOnClickListener(v -> {
            if (viewPager.getCurrentItem() < titles.length - 1) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            } else {
                finishOnboarding();
            }
        });

        tvSkip.setOnClickListener(v -> finishOnboarding());
    }

    private void setupIndicators() {
        indicatorLayout.removeAllViews();
        for (int i = 0; i < titles.length; i++) {
            ImageView dot = new ImageView(this);
            dot.setImageResource(R.drawable.ic_dot_inactive);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(8, 8);
            params.setMargins(6, 0, 6, 0);
            dot.setLayoutParams(params);
            indicatorLayout.addView(dot);
        }
    }

    private void updateIndicator(int position) {
        for (int i = 0; i < indicatorLayout.getChildCount(); i++) {
            ImageView dot = (ImageView) indicatorLayout.getChildAt(i);
            if (i == position) {
                dot.setImageResource(R.drawable.ic_dot_active);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(24, 8);
                params.setMargins(6, 0, 6, 0);
                dot.setLayoutParams(params);
            } else {
                dot.setImageResource(R.drawable.ic_dot_inactive);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(8, 8);
                params.setMargins(6, 0, 6, 0);
                dot.setLayoutParams(params);
            }
        }
    }

    private void finishOnboarding() {
        SharedPreferences prefs = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putBoolean(Constants.KEY_ONBOARDING_DONE, true).apply();

        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
