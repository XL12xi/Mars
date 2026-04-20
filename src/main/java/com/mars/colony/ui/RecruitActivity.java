package com.mars.colony.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.mars.colony.R;
import com.mars.colony.game.Colony;
import com.mars.colony.game.GameState;
import com.mars.colony.model.CrewMember;

public class RecruitActivity extends AppCompatActivity {

    private Colony colony;
    private EditText etCrewName;
    private RadioGroup rgSpecialization;
    private TextView tvRoleStats;
    private TextView tvRecruitSummary;
    private Button btnCreateCrew;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recruit);

        colony = GameState.getColony();

        etCrewName = findViewById(R.id.et_crew_name);
        rgSpecialization = findViewById(R.id.rg_specialization);
        tvRoleStats = findViewById(R.id.tv_role_stats);
        tvRecruitSummary = findViewById(R.id.tv_recruit_summary);
        btnCreateCrew = findViewById(R.id.btn_create_crew);
        btnBack = findViewById(R.id.btn_back);

        updateRoleStats();
        updateRecruitSummary();

        rgSpecialization.setOnCheckedChangeListener((group, checkedId) -> updateRoleStats());
        btnCreateCrew.setOnClickListener(v -> recruitCrew());
        btnBack.setOnClickListener(v -> finish());
    }

    private void recruitCrew() {
        String name = etCrewName.getText().toString().trim();
        if (name.isEmpty()) {
            Toast.makeText(this, "Enter a crew name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (colony.findCrewByName(name) != null) {
            Toast.makeText(this, "A crew member with this name already exists", Toast.LENGTH_SHORT).show();
            return;
        }

        String specialization = getSelectedSpecialization();
        CrewMember crew = GameState.recruitCrew(name, specialization);
        Toast.makeText(
                this,
                crew.getName() + " recruited to Quarters",
                Toast.LENGTH_SHORT
        ).show();

        etCrewName.setText("");
        updateRecruitSummary();
    }

    private String getSelectedSpecialization() {
        int checkedId = rgSpecialization.getCheckedRadioButtonId();

        if (checkedId == R.id.rb_engineer) {
            return "Engineer";
        }
        if (checkedId == R.id.rb_medic) {
            return "Medic";
        }
        if (checkedId == R.id.rb_scientist) {
            return "Scientist";
        }
        if (checkedId == R.id.rb_soldier) {
            return "Soldier";
        }
        if (checkedId == R.id.rb_robot) {
            return "Robot";
        }
        return "Pilot";
    }

    private void updateRoleStats() {
        String specialization = getSelectedSpecialization();
        String stats;

        switch (specialization) {
            case "Engineer":
                stats = "Engineer\nSkill: 6 | Resilience: 3 | Energy: 19\nAbility: Shield";
                break;
            case "Medic":
                stats = "Medic\nSkill: 7 | Resilience: 2 | Energy: 18\nAbility: Healing";
                break;
            case "Scientist":
                stats = "Scientist\nSkill: 8 | Resilience: 1 | Energy: 17\nAbility: Analysis";
                break;
            case "Soldier":
                stats = "Soldier\nSkill: 9 | Resilience: 0 | Energy: 16\nAbility: Critical Strike";
                break;
            case "Robot":
                stats = "Robot\nSkill: 7 | Resilience: 2 | Energy: 22\nAbility: Self Repair";
                break;
            case "Pilot":
            default:
                stats = "Pilot\nSkill: 5 | Resilience: 4 | Energy: 20\nAbility: Evasion";
                break;
        }

        tvRoleStats.setText(stats);
    }

    private void updateRecruitSummary() {
        tvRecruitSummary.setText(String.format(
                "Current crew: %d%nNew recruits start in Quarters with full energy.",
                colony.getCrewCount()
        ));
    }
}
