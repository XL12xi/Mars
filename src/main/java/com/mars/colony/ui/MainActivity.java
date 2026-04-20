package com.mars.colony.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.mars.colony.R;
import com.mars.colony.game.Colony;
import com.mars.colony.game.GameState;

public class MainActivity extends AppCompatActivity {

    private Colony colony;
    private TextView tvGameTitle;
    private TextView tvGameStats;
    private Button btnQuarters;
    private Button btnRecruit;
    private Button btnSimulator;
    private Button btnMissionControl;
    private Button btnTrainingCenter;
    private Button btnSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        colony = GameState.getColony();

        tvGameTitle = findViewById(R.id.tv_game_title);
        tvGameStats = findViewById(R.id.tv_game_stats);
        btnQuarters = findViewById(R.id.btn_quarters);
        btnRecruit = findViewById(R.id.btn_recruit);
        btnSimulator = findViewById(R.id.btn_simulator);
        btnMissionControl = findViewById(R.id.btn_mission_control);
        btnTrainingCenter = findViewById(R.id.btn_training_center);
        btnSettings = findViewById(R.id.btn_settings);

        tvGameTitle.setText(getString(R.string.app_name));
        updateGameStats();
        setupListeners();
    }

    private void updateGameStats() {
        String stats = String.format(
                "Crew: %d%nCompleted missions: %d%nCrew victories: %d",
                colony.getCrewCount(),
                colony.getMissionCount(),
                colony.getVictoryCount()
        );
        tvGameStats.setText(stats);
    }

    private void setupListeners() {
        btnQuarters.setOnClickListener(v -> startActivity(new Intent(this, QuartersActivity.class)));
        btnRecruit.setOnClickListener(v -> startActivity(new Intent(this, RecruitActivity.class)));
        btnSimulator.setOnClickListener(v -> startActivity(new Intent(this, SimulatorActivity.class)));
        btnMissionControl.setOnClickListener(v -> startActivity(new Intent(this, MissionControlActivity.class)));
        btnTrainingCenter.setOnClickListener(v -> startActivity(new Intent(this, TrainingCenterActivity.class)));
        btnSettings.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        colony = GameState.getColony();
        updateGameStats();
    }
}
