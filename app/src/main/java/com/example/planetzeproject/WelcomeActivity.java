package com.example.planetzeproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class WelcomeActivity extends AppCompatActivity {

    private Button login;
    private Button registration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome);

        // Check if the app is opened for the first time after installation
        SharedPreferences preferences = getSharedPreferences("PREFERENCES", MODE_PRIVATE);
        boolean isFirstInstall = preferences.getBoolean("FirstTimeInstall", true); // Default is true for the first run

        if (isFirstInstall) {
            // Mark as no longer the first time
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("FirstTimeInstall", false); // Set to false after the first run
            editor.apply();

            // Redirect to RegistrationActivity
            Intent intent = new Intent(WelcomeActivity.this, OnboardingActivity.class);
            startActivity(intent);
            finish(); // Prevent returning to this activity
        }

        // Set up system bar insets for the login button
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set up login button
        login = findViewById(R.id.login);
        login.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        // Set up registration button
        registration = findViewById(R.id.signup);
        registration.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, RegistrationActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
