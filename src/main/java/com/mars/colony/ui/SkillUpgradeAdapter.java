package com.mars.colony.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.mars.colony.R;
import com.mars.colony.model.CrewMember;
import com.mars.colony.upgrade.SkillUpgradeManager;
import java.util.List;

public class SkillUpgradeAdapter extends RecyclerView.Adapter<SkillUpgradeAdapter.ViewHolder> {

    private static final String[][] SKILLS = {
        {"Evasion", "Pilot dodge"},
        {"Shield", "Engineer shield"},
        {"Healing", "Medic heal"},
        {"Analysis", "Scientist analysis"},
        {"CriticalStrike", "Soldier crit"},
        {"SelfRepair", "Robot repair"}
    };

    private final List<CrewMember> crewList;
    private final OnSkillUpgradeListener upgradeListener;
    private final OnCrewSelectListener selectListener;

    public interface OnSkillUpgradeListener {
        void onUpgradeClick(CrewMember crew, String skillName, SkillUpgradeManager manager);
    }

    public interface OnCrewSelectListener {
        void onCrewSelect(CrewMember crew);
    }

    public SkillUpgradeAdapter(
            List<CrewMember> crewList,
            OnSkillUpgradeListener upgradeListener,
            OnCrewSelectListener selectListener
    ) {
        this.crewList = crewList;
        this.upgradeListener = upgradeListener;
        this.selectListener = selectListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_crew_skill_upgrade, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CrewMember crew = crewList.get(position);
        SkillUpgradeManager upgradeManager = crew.getSkillUpgradeManager();
        if (upgradeManager == null) {
            upgradeManager = new SkillUpgradeManager(crew.getId(), crew.getName());
            crew.setSkillUpgradeManager(upgradeManager);
        }

        holder.tvCrewName.setText(crew.getName());
        holder.tvCrewSpecialization.setText(crew.getSpecialization());
        holder.tvCrystals.setText(String.format("Crystals: %d", upgradeManager.getSkillCrystalsOwned()));
        holder.llSkills.removeAllViews();

        SkillUpgradeManager finalUpgradeManager = upgradeManager;
        for (String[] skill : SKILLS) {
            addSkillRow(holder.llSkills, crew, finalUpgradeManager, skill[0], skill[1]);
        }

        holder.itemView.setOnClickListener(v -> selectListener.onCrewSelect(crew));
    }

    private void addSkillRow(
            LinearLayout container,
            CrewMember crew,
            SkillUpgradeManager manager,
            String skillName,
            String label
    ) {
        LinearLayout row = new LinearLayout(container.getContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(0, 8, 0, 8);

        TextView info = new TextView(container.getContext());
        int level = manager.getSkillLevel(skillName);
        int cost = manager.getUpgradeCost(skillName);
        String effect = manager.getSkillEffectDisplay(skillName);
        info.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        info.setText(String.format(
                "%s | Lv.%d | %s | Next cost: %s",
                label,
                level,
                effect,
                cost < 0 ? "MAX" : String.valueOf(cost)
        ));

        Button button = new Button(container.getContext());
        button.setText(cost < 0 ? "MAX" : "Upgrade");
        button.setEnabled(cost >= 0 && manager.getSkillCrystalsOwned() >= cost);
        button.setOnClickListener(v -> {
            selectListener.onCrewSelect(crew);
            upgradeListener.onUpgradeClick(crew, skillName, manager);
        });

        row.addView(info);
        row.addView(button);
        container.addView(row);
    }

    @Override
    public int getItemCount() {
        return crewList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCrewName;
        TextView tvCrewSpecialization;
        TextView tvCrystals;
        LinearLayout llSkills;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCrewName = itemView.findViewById(R.id.tv_crew_name_upgrade);
            tvCrewSpecialization = itemView.findViewById(R.id.tv_crew_specialization_upgrade);
            tvCrystals = itemView.findViewById(R.id.tv_crystals);
            llSkills = itemView.findViewById(R.id.ll_skills);
        }
    }
}
