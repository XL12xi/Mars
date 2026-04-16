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
import com.mars.colony.upgrade.SkillUpgradeManager;
import java.util.List;

public class TrainingCenterActivity extends AppCompatActivity {

    private Colony colony;
    private RecyclerView rvSkillUpgrade;
    private SkillUpgradeAdapter skillUpgradeAdapter;
    private TextView tvTrainingCenterInfo;
    private TextView tvCrystalInfo;
    private Button btnBack;
    private CrewMember selectedCrew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_center);

        colony = GameState.getColony();

        rvSkillUpgrade = findViewById(R.id.rv_skill_upgrade);
        tvTrainingCenterInfo = findViewById(R.id.tv_training_center_info);
        tvCrystalInfo = findViewById(R.id.tv_crystal_info);
        btnBack = findViewById(R.id.btn_back);

        tvTrainingCenterInfo.setText("Training Center - upgrade crew skills with crystals");
        initializeRecyclerView();

        btnBack.setOnClickListener(v -> finish());
    }

    private void initializeRecyclerView() {
        List<CrewMember> crewList = colony.getAllCrew();

        skillUpgradeAdapter = new SkillUpgradeAdapter(
                crewList,
                this::performUpgrade,
                crew -> {
                    selectedCrew = crew;
                    updateCrystalInfo();
                }
        );

        rvSkillUpgrade.setLayoutManager(new LinearLayoutManager(this));
        rvSkillUpgrade.setAdapter(skillUpgradeAdapter);

        if (!crewList.isEmpty()) {
            selectedCrew = crewList.get(0);
            updateCrystalInfo();
        }
    }

    private void performUpgrade(CrewMember crew, String skillName, SkillUpgradeManager upgradeManager) {
        int cost = upgradeManager.getUpgradeCost(skillName);
        if (cost < 0) {
            Toast.makeText(this, skillName + " is already maxed out", Toast.LENGTH_SHORT).show();
            return;
        }

        if (upgradeManager.getSkillCrystalsOwned() < cost) {
            Toast.makeText(
                    this,
                    String.format("Not enough crystals for %s. Need %d, have %d", skillName, cost, upgradeManager.getSkillCrystalsOwned()),
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        if (upgradeManager.upgradeSkill(skillName)) {
            Toast.makeText(
                    this,
                    String.format("%s upgraded. %s", skillName, upgradeManager.getSkillEffectDisplay(skillName)),
                    Toast.LENGTH_SHORT
            ).show();
            selectedCrew = crew;
            skillUpgradeAdapter.notifyDataSetChanged();
            updateCrystalInfo();
        } else {
            Toast.makeText(this, "Upgrade failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateCrystalInfo() {
        if (selectedCrew == null || selectedCrew.getSkillUpgradeManager() == null) {
            tvCrystalInfo.setText("Select a crew member to inspect their upgrade state.");
            return;
        }

        SkillUpgradeManager mgr = selectedCrew.getSkillUpgradeManager();
        String info = String.format(
                "Selected crew: %s%nCrystals: %d%nTotal spent: %d",
                selectedCrew.getName(),
                mgr.getSkillCrystalsOwned(),
                mgr.getTotalCrystalsSpent()
        );
        tvCrystalInfo.setText(info);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (skillUpgradeAdapter != null) {
            skillUpgradeAdapter.notifyDataSetChanged();
            updateCrystalInfo();
        }
    }
}
