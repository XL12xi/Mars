package com.mars.colony.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import com.mars.colony.R;

public class SettingsActivity extends AppCompatActivity {

    private SwitchCompat swSoundEnabled;
    private SwitchCompat swVibrateEnabled;
    private Button btnAbout;
    private Button btnRules;
    private Button btnBack;
    private TextView tvSettingsInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        swSoundEnabled = findViewById(R.id.sw_sound_enabled);
        swVibrateEnabled = findViewById(R.id.sw_vibrate_enabled);
        btnAbout = findViewById(R.id.btn_about);
        btnRules = findViewById(R.id.btn_rules);
        btnBack = findViewById(R.id.btn_back);
        tvSettingsInfo = findViewById(R.id.tv_settings_info);

        swSoundEnabled.setChecked(true);
        swVibrateEnabled.setChecked(true);
        tvSettingsInfo.setText("Settings - local toggles and project information");

        btnAbout.setOnClickListener(v -> showAbout());
        btnRules.setOnClickListener(v -> showRules());
        btnBack.setOnClickListener(v -> finish());
    }

    private void showAbout() {
        new AlertDialog.Builder(this)
                .setTitle("About")
                .setMessage(
                        "Space Colony is a small Android coursework demo.\n\n" +
                        "You can inspect crew members, train them, run missions, and spend crystals on upgrades."
                )
                .setPositiveButton("Close", null)
                .show();
    }

    private void showRules() {
        new AlertDialog.Builder(this)
                .setTitle("Rules")
                .setMessage(
                        "1. Recover crew in Quarters.\n" +
                        "2. Train them in the Simulator.\n" +
                        "3. Pick two crew members in Mission Control.\n" +
                        "4. Win missions to earn crystals.\n" +
                        "5. Spend crystals in the Training Center."
                )
                .setPositiveButton("Close", null)
                .show();
    }
}
