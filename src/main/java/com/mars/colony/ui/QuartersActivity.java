package com.mars.colony.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.mars.colony.R;
import com.mars.colony.game.Colony;
import com.mars.colony.game.GameState;
import com.mars.colony.model.CrewMember;
import java.util.ArrayList;
import java.util.List;

public class QuartersActivity extends AppCompatActivity {

    private Colony colony;
    private RecyclerView rvCrew;
    private CrewCheckboxAdapter crewAdapter;
    private TextView tvQuartersInfo;
    private Button btnRecoverAll;
    private Button btnMoveToSimulator;
    private Button btnMoveToMissionControl;
    private Button btnBack;
    private final List<CrewMember> quartersCrew = new ArrayList<>();
    private final List<CrewMember> selectedCrew = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quarters);

        colony = GameState.getColony();

        rvCrew = findViewById(R.id.rv_crew);
        tvQuartersInfo = findViewById(R.id.tv_quarters_info);
        btnRecoverAll = findViewById(R.id.btn_recover_all);
        btnMoveToSimulator = findViewById(R.id.btn_move_to_simulator);
        btnMoveToMissionControl = findViewById(R.id.btn_move_to_mission_control);
        btnBack = findViewById(R.id.btn_back);

        initializeRecyclerView();

        btnRecoverAll.setOnClickListener(v -> recoverAllCrew());
        btnMoveToSimulator.setOnClickListener(v -> moveSelectedCrewTo("SIMULATOR", "Simulator"));
        btnMoveToMissionControl.setOnClickListener(v -> moveSelectedCrewTo("MISSION_CONTROL", "Mission Control"));
        btnBack.setOnClickListener(v -> finish());
    }

    private void initializeRecyclerView() {
        crewAdapter = new CrewCheckboxAdapter(quartersCrew, (crew, isChecked) -> {
            if (isChecked) {
                if (!selectedCrew.contains(crew)) {
                    selectedCrew.add(crew);
                }
            } else {
                selectedCrew.remove(crew);
            }
        });
        rvCrew.setLayoutManager(new LinearLayoutManager(this));
        rvCrew.setAdapter(crewAdapter);
        refreshCrewList();
    }

    private void recoverAllCrew() {
        for (CrewMember crew : quartersCrew) {
            crew.recover();
        }
        Toast.makeText(this, "Recovered crew in Quarters", Toast.LENGTH_SHORT).show();
        crewAdapter.notifyDataSetChanged();
    }

    private void moveSelectedCrewTo(String location, String displayName) {
        if (selectedCrew.isEmpty()) {
            Toast.makeText(this, "Select at least one crew member", Toast.LENGTH_SHORT).show();
            return;
        }

        List<CrewMember> crewToMove = new ArrayList<>(selectedCrew);
        for (CrewMember crew : crewToMove) {
            colony.moveCrewTo(crew.getId(), location);
        }

        Toast.makeText(
                this,
                "Moved " + crewToMove.size() + " crew to " + displayName,
                Toast.LENGTH_SHORT
        ).show();
        refreshCrewList();
    }

    private void refreshCrewList() {
        quartersCrew.clear();
        quartersCrew.addAll(colony.getCrewByLocation("QUARTERS"));
        selectedCrew.clear();
        crewAdapter.clearAllChecked();
        tvQuartersInfo.setText(String.format(
                "Quarters - %d crew resting. Use Recover All to restore energy before moving crew.",
                quartersCrew.size()
        ));
    }

    @Override
    protected void onResume() {
        super.onResume();
        colony = GameState.getColony();
        if (crewAdapter != null) {
            refreshCrewList();
        }
    }
}
