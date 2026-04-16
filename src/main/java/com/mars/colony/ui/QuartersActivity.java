package com.mars.colony.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.mars.colony.R;
import com.mars.colony.game.Colony;
import com.mars.colony.game.GameState;
import com.mars.colony.model.CrewMember;
import java.util.List;

public class QuartersActivity extends AppCompatActivity {

    private Colony colony;
    private RecyclerView rvCrew;
    private CrewAdapter crewAdapter;
    private TextView tvQuartersInfo;
    private Button btnRecoverAll;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quarters);

        colony = GameState.getColony();

        rvCrew = findViewById(R.id.rv_crew);
        tvQuartersInfo = findViewById(R.id.tv_quarters_info);
        btnRecoverAll = findViewById(R.id.btn_recover_all);
        btnBack = findViewById(R.id.btn_back);

        tvQuartersInfo.setText("Quarters - recover and inspect your crew");
        initializeRecyclerView();

        btnRecoverAll.setOnClickListener(v -> recoverAllCrew());
        btnBack.setOnClickListener(v -> finish());
    }

    private void initializeRecyclerView() {
        List<CrewMember> crewList = colony.getAllCrew();
        crewAdapter = new CrewAdapter(crewList, this::showCrewDetails);
        rvCrew.setLayoutManager(new LinearLayoutManager(this));
        rvCrew.setAdapter(crewAdapter);
    }

    private void recoverAllCrew() {
        for (CrewMember crew : colony.getAllCrew()) {
            crew.recover();
        }
        crewAdapter.notifyDataSetChanged();
    }

    private void showCrewDetails(CrewMember crew) {
        String details = String.format(
                "Name: %s%nRole: %s%nSkill: %d%nResilience: %d%nEnergy: %d/%d%nExperience: %d%nMissions: %d%nVictories: %d",
                crew.getName(),
                crew.getSpecialization(),
                crew.getSkill(),
                crew.getResilience(),
                crew.getEnergy(),
                crew.getMaxEnergy(),
                crew.getExperience(),
                crew.getMissionsCompleted(),
                crew.getVictoriesCount()
        );

        new AlertDialog.Builder(this)
                .setTitle(crew.getName())
                .setMessage(details)
                .setPositiveButton("Close", null)
                .show();
    }
}
