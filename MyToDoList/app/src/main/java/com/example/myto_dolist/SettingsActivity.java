package com.example.myto_dolist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    private TextView russianLang, englishLang;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        prefs = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        String currentLang = prefs.getString("My_Lang", "ru");
        setLocale(currentLang);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        russianLang = findViewById(R.id.language_russian);
        englishLang = findViewById(R.id.language_english);

        updateSelection(currentLang);

        russianLang.setOnClickListener(v -> changeLanguage("ru"));
        englishLang.setOnClickListener(v -> changeLanguage("en"));
    }

    private void changeLanguage(String lang) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("My_Lang", lang);
        editor.apply();

        setLocale(lang);
        recreate(); // Перезапуск экрана для применения изменений
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    private void updateSelection(String lang) {
        if (lang.equals("ru")) {
            russianLang.setBackgroundResource(R.drawable.settings_item_selected);
            russianLang.setTextColor(getResources().getColor(R.color.primary_blue));
            englishLang.setBackgroundResource(R.drawable.settings_item_background);
            englishLang.setTextColor(getResources().getColor(R.color.black));
        } else {
            englishLang.setBackgroundResource(R.drawable.settings_item_selected);
            englishLang.setTextColor(getResources().getColor(R.color.primary_blue));
            russianLang.setBackgroundResource(R.drawable.settings_item_background);
            russianLang.setTextColor(getResources().getColor(R.color.black));
        }
    }
}