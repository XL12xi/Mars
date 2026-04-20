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

        tvTrainingCenterInfo.setText("Training Center - spend colony storage crystals to upgrade crew abilities");
        initializeRecyclerView();

        btnBack.setOnClickListener(v -> finish());
    }

    private void initializeRecyclerView() {
        List<CrewMember> crewList = colony.getAllCrew();

        skillUpgradeAdapter = new SkillUpgradeAdapter(
                crewList,
                colony.getCrystalsInStorage(),
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
        String expectedSkill = getUpgradeableSkillName(crew);
        if (expectedSkill == null || !expectedSkill.equals(skillName)) {
            Toast.makeText(this, "This crew cannot upgrade " + skillName, Toast.LENGTH_SHORT).show();
            return;
        }

        int cost = upgradeManager.getUpgradeCost(skillName);
        if (cost < 0) {
            Toast.makeText(this, skillName + " is already maxed out", Toast.LENGTH_SHORT).show();
            return;
        }

        int storageCrystals = colony.getCrystalsInStorage();
        if (storageCrystals < cost) {
            Toast.makeText(
                    this,
                    String.format("Not enough storage crystals for %s. Need %d, have %d", skillName, cost, storageCrystals),
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        if (upgradeManager.upgradeSkillWithExternalPayment(skillName)) {
            colony.setCrystalsInStorage(storageCrystals - cost);
            syncAbilityLevel(crew, skillName, upgradeManager);
            Toast.makeText(
                    this,
                    String.format("%s upgraded for %d storage crystals. %s", skillName, cost, upgradeManager.getSkillEffectDisplay(skillName)),
                    Toast.LENGTH_SHORT
            ).show();
            selectedCrew = crew;
            skillUpgradeAdapter.setStorageCrystals(colony.getCrystalsInStorage());
            skillUpgradeAdapter.notifyDataSetChanged();
            updateCrystalInfo();
        } else {
            Toast.makeText(this, "Upgrade failed", Toast.LENGTH_SHORT).show();
        }
    }

    private String getUpgradeableSkillName(CrewMember crew) {
        switch (crew.getSpecialization()) {
            case "Pilot":
                return "Evasion";
            case "Engineer":
                return "Shield";
            case "Medic":
                return "Healing";
            case "Scientist":
                return "Analysis";
            case "Soldier":
                return "CriticalStrike";
            case "Robot":
                return "SelfRepair";
            default:
                return null;
        }
    }

    private void syncAbilityLevel(CrewMember crew, String skillName, SkillUpgradeManager upgradeManager) {
        if (crew.getSpecialAbility() == null) {
            return;
        }

        crew.getSpecialAbility().setLevel(upgradeManager.getSkillLevel(skillName));
    }

    private void updateCrystalInfo() {
        if (selectedCrew == null || selectedCrew.getSkillUpgradeManager() == null) {
            tvCrystalInfo.setText("Select a crew member to inspect their upgrade state.");
            return;
        }

        SkillUpgradeManager mgr = selectedCrew.getSkillUpgradeManager();
        String info = String.format(
                "Selected crew: %s%nColony storage crystals: %d%nCrew upgrade crystals spent: %d",
                selectedCrew.getName(),
                colony.getCrystalsInStorage(),
                mgr.getTotalCrystalsSpent()
        );
        tvCrystalInfo.setText(info);
    }

    @Override
    protected void onResume() {
        super.onResume();
        colony = GameState.getColony();
        if (skillUpgradeAdapter != null) {
            skillUpgradeAdapter.setStorageCrystals(colony.getCrystalsInStorage());
            skillUpgradeAdapter.notifyDataSetChanged();
            updateCrystalInfo();
        }
    }
}
