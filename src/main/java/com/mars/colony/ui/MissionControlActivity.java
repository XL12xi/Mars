package com.mars.colony.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.mars.colony.R;
import com.mars.colony.game.Colony;
import com.mars.colony.game.GameState;
import com.mars.colony.game.MissionControl;
import com.mars.colony.model.CrewMember;
import com.mars.colony.model.Threat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MissionControlActivity extends AppCompatActivity {

    private Colony colony;
    private MissionControl missionControl;
    private TextView tvMissionInfo;
    private RadioGroup rgCrewA;
    private RadioGroup rgCrewB;
    private TextView tvCrewAInfo;
    private TextView tvCrewBInfo;
    private Button btnStartMission;
    private Button btnReturnSelectedToQuarters;
    private Button btnBack;

    private CrewMember selectedCrewA;
    private CrewMember selectedCrewB;
    private Threat currentThreat;
    private int threatMissionCount = -1;
    private final Map<Integer, CrewMember> crewAByRadioId = new HashMap<>();
    private final Map<Integer, CrewMember> crewBByRadioId = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission_control);

        colony = GameState.getColony();
        missionControl = new MissionControl(colony);

        tvMissionInfo = findViewById(R.id.tv_mission_info);
        rgCrewA = findViewById(R.id.rg_crew_a);
        rgCrewB = findViewById(R.id.rg_crew_b);
        tvCrewAInfo = findViewById(R.id.tv_crew_a_info);
        tvCrewBInfo = findViewById(R.id.tv_crew_b_info);
        btnStartMission = findViewById(R.id.btn_start_mission);
        btnReturnSelectedToQuarters = findViewById(R.id.btn_return_selected_to_quarters);
        btnBack = findViewById(R.id.btn_back);

        setupThreat();
        populateCrewLists();
        setupListeners();
    }

    private void setupThreat() {
        currentThreat = missionControl.generateThreat();
        threatMissionCount = colony.getMissionCount();
    }

    private void updateMissionInfo(int assignedCrewCount) {
        tvMissionInfo.setText(String.format(
                "Current Threat: %s (%s)%nSkill: %d | Resilience: %d | Energy: %d%nCompleted missions: %d%nCrew assigned to Mission Control: %d",
                currentThreat.getName(),
                currentThreat.getType(),
                currentThreat.getSkill(),
                currentThreat.getResilience(),
                currentThreat.getMaxEnergy(),
                colony.getMissionCount(),
                assignedCrewCount
        ));
    }

    private void populateCrewLists() {
        rgCrewA.removeAllViews();
        rgCrewB.removeAllViews();
        crewAByRadioId.clear();
        crewBByRadioId.clear();
        selectedCrewA = null;
        selectedCrewB = null;
        tvCrewAInfo.setText("");
        tvCrewBInfo.setText("");

        List<CrewMember> crewList = colony.getCrewByLocation("MISSION_CONTROL");
        updateMissionInfo(crewList.size());
        btnStartMission.setEnabled(crewList.size() >= 2);

        for (CrewMember crew : crewList) {
            RadioButton rbA = new RadioButton(this);
            rbA.setText(String.format("%s (%s)", crew.getName(), crew.getSpecialization()));
            rbA.setId(View.generateViewId());
            crewAByRadioId.put(rbA.getId(), crew);
            rgCrewA.addView(rbA);

            RadioButton rbB = new RadioButton(this);
            rbB.setText(String.format("%s (%s)", crew.getName(), crew.getSpecialization()));
            rbB.setId(View.generateViewId());
            crewBByRadioId.put(rbB.getId(), crew);
            rgCrewB.addView(rbB);
        }

        if (crewList.isEmpty()) {
            tvCrewAInfo.setText("Move crew from Quarters to Mission Control first.");
            tvCrewBInfo.setText("Move crew from Quarters to Mission Control first.");
        } else if (crewList.size() == 1) {
            tvCrewBInfo.setText("At least two crew members are required for a mission.");
        }
    }

    private void setupListeners() {
        rgCrewA.setOnCheckedChangeListener((group, checkedId) -> {
            selectedCrewA = crewAByRadioId.get(checkedId);
            if (selectedCrewA != null) {
                tvCrewAInfo.setText(getCrewInfo(selectedCrewA));
            }
        });

        rgCrewB.setOnCheckedChangeListener((group, checkedId) -> {
            selectedCrewB = crewBByRadioId.get(checkedId);
            if (selectedCrewB != null) {
                tvCrewBInfo.setText(getCrewInfo(selectedCrewB));
            }
        });

        btnStartMission.setOnClickListener(v -> {
            if (selectedCrewA == null || selectedCrewB == null) {
                Toast.makeText(this, "Select two crew members", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedCrewA.getId() == selectedCrewB.getId()) {
                Toast.makeText(this, "Select two different crew members", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(this, BattleActivity.class);
            intent.putExtra("crew_a_name", selectedCrewA.getName());
            intent.putExtra("crew_b_name", selectedCrewB.getName());
            intent.putExtra("threat_name", currentThreat.getName());
            intent.putExtra("threat_type", currentThreat.getType());
            intent.putExtra("threat_skill", currentThreat.getSkill());
            intent.putExtra("threat_resilience", currentThreat.getResilience());
            intent.putExtra("threat_max_energy", currentThreat.getMaxEnergy());
            startActivity(intent);
        });

        btnReturnSelectedToQuarters.setOnClickListener(v -> returnSelectedCrewToQuarters());
        btnBack.setOnClickListener(v -> finish());
    }

    private String getCrewInfo(CrewMember crew) {
        return String.format(
                "Skill: %d | Resilience: %d | Energy: %d/%d | Experience: %d",
                crew.getSkill(),
                crew.getResilience(),
                crew.getEnergy(),
                crew.getMaxEnergy(),
                crew.getExperience()
        );
    }

    private void returnSelectedCrewToQuarters() {
        List<CrewMember> crewToReturn = new ArrayList<>();
        if (selectedCrewA != null) {
            crewToReturn.add(selectedCrewA);
        }
        if (selectedCrewB != null && (selectedCrewA == null || selectedCrewB.getId() != selectedCrewA.getId())) {
            crewToReturn.add(selectedCrewB);
        }

        if (crewToReturn.isEmpty()) {
            Toast.makeText(this, "Select crew to return", Toast.LENGTH_SHORT).show();
            return;
        }

        for (CrewMember crew : crewToReturn) {
            colony.moveCrewTo(crew.getId(), "QUARTERS");
        }

        Toast.makeText(
                this,
                "Returned " + crewToReturn.size() + " crew to Quarters",
                Toast.LENGTH_SHORT
        ).show();
        populateCrewLists();
    }

    @Override
    protected void onResume() {
        super.onResume();
        colony = GameState.getColony();
        missionControl = new MissionControl(colony);
        if (currentThreat == null || threatMissionCount != colony.getMissionCount()) {
            setupThreat();
        }
        if (rgCrewA != null && rgCrewB != null) {
            populateCrewLists();
        }
    }
}
