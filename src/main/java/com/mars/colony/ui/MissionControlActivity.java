package com.mars.colony.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.mars.colony.R;
import com.mars.colony.game.Colony;
import com.mars.colony.game.GameState;
import com.mars.colony.model.CrewMember;
import com.mars.colony.model.Threat;
import java.util.List;

public class MissionControlActivity extends AppCompatActivity {

    private Colony colony;
    private TextView tvMissionInfo;
    private RadioGroup rgCrewA;
    private RadioGroup rgCrewB;
    private TextView tvCrewAInfo;
    private TextView tvCrewBInfo;
    private Button btnStartMission;
    private Button btnBack;

    private CrewMember selectedCrewA;
    private CrewMember selectedCrewB;
    private Threat currentThreat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission_control);

        colony = GameState.getColony();

        tvMissionInfo = findViewById(R.id.tv_mission_info);
        rgCrewA = findViewById(R.id.rg_crew_a);
        rgCrewB = findViewById(R.id.rg_crew_b);
        tvCrewAInfo = findViewById(R.id.tv_crew_a_info);
        tvCrewBInfo = findViewById(R.id.tv_crew_b_info);
        btnStartMission = findViewById(R.id.btn_start_mission);
        btnBack = findViewById(R.id.btn_back);

        setupThreat();
        populateCrewLists();
        setupListeners();
    }

    private void setupThreat() {
        currentThreat = new Threat("Alien Spores", "Biological", 8, 3, 25);
        tvMissionInfo.setText(String.format(
                "Current Threat: %s (%s)%nSkill Required: %d",
                currentThreat.getName(),
                currentThreat.getType(),
                currentThreat.getSkill()
        ));
    }

    private void populateCrewLists() {
        List<CrewMember> crewList = colony.getAllCrew();
        for (CrewMember crew : crewList) {
            RadioButton rbA = new RadioButton(this);
            rbA.setText(crew.getName());
            rbA.setId(View.generateViewId());
            rgCrewA.addView(rbA);

            RadioButton rbB = new RadioButton(this);
            rbB.setText(crew.getName());
            rbB.setId(View.generateViewId());
            rgCrewB.addView(rbB);
        }
    }

    private void setupListeners() {
        rgCrewA.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton rb = findViewById(checkedId);
            if (rb != null) {
                selectedCrewA = colony.findCrewByName(rb.getText().toString());
                if (selectedCrewA != null) {
                    tvCrewAInfo.setText(String.format("Skill: %d, Energy: %d", selectedCrewA.getSkill(), selectedCrewA.getEnergy()));
                }
            }
        });

        rgCrewB.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton rb = findViewById(checkedId);
            if (rb != null) {
                selectedCrewB = colony.findCrewByName(rb.getText().toString());
                if (selectedCrewB != null) {
                    tvCrewBInfo.setText(String.format("Skill: %d, Energy: %d", selectedCrewB.getSkill(), selectedCrewB.getEnergy()));
                }
            }
        });

        btnStartMission.setOnClickListener(v -> {
            if (selectedCrewA != null && selectedCrewB != null) {
                Intent intent = new Intent(this, BattleActivity.class);
                intent.putExtra("crew_a_name", selectedCrewA.getName());
                intent.putExtra("crew_b_name", selectedCrewB.getName());
                intent.putExtra("threat_name", currentThreat.getName());
                intent.putExtra("threat_type", currentThreat.getType());
                intent.putExtra("threat_skill", currentThreat.getSkill());
                intent.putExtra("threat_resilience", currentThreat.getResilience());
                intent.putExtra("threat_max_energy", currentThreat.getMaxEnergy());
                startActivity(intent);
            }
        });

        btnBack.setOnClickListener(v -> finish());
    }
}
