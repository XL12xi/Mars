package com.mars.colony.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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

public class SimulatorActivity extends AppCompatActivity {

    private Colony colony;
    private RecyclerView rvCrew;
    private CrewCheckboxAdapter crewCheckboxAdapter;
    private TextView tvSimulatorInfo;
    private TextView tvTrainingLog;
    private Button btnTrain;
    private Button btnClearSelection;
    private Button btnBack;
    private LinearLayout llSelectedCrew;

    private final List<CrewMember> selectedCrew = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulator);

        colony = GameState.getColony();

        rvCrew = findViewById(R.id.rv_crew_simulator);
        tvSimulatorInfo = findViewById(R.id.tv_simulator_info);
        tvTrainingLog = findViewById(R.id.tv_training_log);
        btnTrain = findViewById(R.id.btn_train);
        btnClearSelection = findViewById(R.id.btn_clear_selection);
        btnBack = findViewById(R.id.btn_back);
        llSelectedCrew = findViewById(R.id.ll_selected_crew);

        tvSimulatorInfo.setText("Simulator - train crew for +1 to +3 experience at an energy cost");
        initializeRecyclerView();

        btnTrain.setOnClickListener(v -> trainSelectedCrew());
        btnClearSelection.setOnClickListener(v -> clearSelection());
        btnBack.setOnClickListener(v -> finish());
    }

    private void initializeRecyclerView() {
        List<CrewMember> crewList = colony.getAllCrew();
        crewCheckboxAdapter = new CrewCheckboxAdapter(crewList, (crew, isChecked) -> {
            if (isChecked) {
                if (!selectedCrew.contains(crew)) {
                    selectedCrew.add(crew);
                }
            } else {
                selectedCrew.remove(crew);
            }
            updateSelectedDisplay();
        });

        rvCrew.setLayoutManager(new LinearLayoutManager(this));
        rvCrew.setAdapter(crewCheckboxAdapter);
    }

    private void updateSelectedDisplay() {
        llSelectedCrew.removeAllViews();

        if (selectedCrew.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("No crew selected");
            llSelectedCrew.addView(empty);
            return;
        }

        for (CrewMember crew : selectedCrew) {
            TextView tv = new TextView(this);
            tv.setText("- " + crew.getName() + " (" + crew.getSpecialization() + ")");
            tv.setPadding(0, 4, 0, 4);
            llSelectedCrew.addView(tv);
        }
    }

    private void trainSelectedCrew() {
        if (selectedCrew.isEmpty()) {
            Toast.makeText(this, "Select at least one crew member", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder log = new StringBuilder("Training results:\n");

        for (CrewMember crew : selectedCrew) {
            if (crew.getEnergy() < 5) {
                log.append("- ").append(crew.getName()).append(": not enough energy\n");
                continue;
            }

            int beforeExp = crew.getExperience();
            int beforeEnergy = crew.getEnergy();
            crew.train();

            log.append(String.format(
                    "- %s: +%d exp, -%d energy (now %d/%d)%n",
                    crew.getName(),
                    crew.getExperience() - beforeExp,
                    beforeEnergy - crew.getEnergy(),
                    crew.getEnergy(),
                    crew.getMaxEnergy()
            ));
        }

        tvTrainingLog.setText(log.toString());
        crewCheckboxAdapter.notifyDataSetChanged();
    }

    private void clearSelection() {
        selectedCrew.clear();
        crewCheckboxAdapter.clearAllChecked();
        updateSelectedDisplay();
        tvTrainingLog.setText("");
    }
}
