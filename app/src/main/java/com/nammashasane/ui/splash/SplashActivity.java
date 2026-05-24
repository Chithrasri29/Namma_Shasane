package com.nammashasane.ui.splash;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.appcompat.app.AppCompatActivity;

import com.nammashasane.R;
import com.nammashasane.ui.home.MainActivity;
import com.nammashasane.ui.onboarding.OnboardingActivity;
import com.nammashasane.utils.Constants;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Animate logo
        View logo = findViewById(R.id.tv_logo_icon);
        if (logo != null) {
            logo.setAlpha(0f);
            logo.setScaleX(0.6f);
            logo.setScaleY(0.6f);
            logo.animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(700)
                    .setInterpolator(new DecelerateInterpolator())
                    .start();
        }

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            SharedPreferences prefs = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
            boolean onboardingDone = prefs.getBoolean(Constants.KEY_ONBOARDING_DONE, false);

            Intent intent;
            if (onboardingDone) {
                intent = new Intent(this, MainActivity.class);
            } else {
                intent = new Intent(this, OnboardingActivity.class);
            }
            startActivity(intent);
            finish();
        }, 1800);
    }
}
