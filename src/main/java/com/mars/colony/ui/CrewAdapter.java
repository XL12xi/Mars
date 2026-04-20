package com.mars.colony.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.mars.colony.R;
import com.mars.colony.model.CrewMember;
import java.util.List;

public class CrewAdapter extends RecyclerView.Adapter<CrewAdapter.ViewHolder> {

    private final List<CrewMember> crewList;
    private final OnCrewClickListener listener;

    public interface OnCrewClickListener {
        void onCrewClick(CrewMember crew);
    }

    public CrewAdapter(List<CrewMember> crewList, OnCrewClickListener listener) {
        this.crewList = crewList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_crew, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CrewMember crew = crewList.get(position);

        if (crew.getImageResource() != 0) {
            holder.ivCrewImage.setImageResource(crew.getImageResource());
        }
        holder.tvName.setText(crew.getName());
        holder.tvSpecialization.setText("Role: " + crew.getSpecialization());
        holder.tvSkill.setText(String.format("Skill: %d", crew.getSkill()));
        holder.tvResilience.setText(String.format("Resilience: %d", crew.getResilience()));
        holder.tvEnergy.setText(String.format("Energy: %d/%d", crew.getEnergy(), crew.getMaxEnergy()));
        holder.tvExperience.setText(String.format(
                "Experience: %d | Missions: %d | Wins: %d | Losses: %d | Win rate: %.1f%% | Medbay: %d",
                crew.getExperience(),
                crew.getMissionsCompleted(),
                crew.getVictoriesCount(),
                crew.getLossesCount(),
                crew.getWinRate(),
                crew.getMedbayVisits()
        ));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCrewClick(crew);
            }
        });
    }

    @Override
    public int getItemCount() {
        return crewList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvSpecialization;
        TextView tvSkill;
        TextView tvResilience;
        TextView tvEnergy;
        TextView tvExperience;
        ImageView ivCrewImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCrewImage = itemView.findViewById(R.id.iv_crew_image);
            tvName = itemView.findViewById(R.id.tv_crew_name);
            tvSpecialization = itemView.findViewById(R.id.tv_crew_specialization);
            tvSkill = itemView.findViewById(R.id.tv_crew_skill);
            tvResilience = itemView.findViewById(R.id.tv_crew_resilience);
            tvEnergy = itemView.findViewById(R.id.tv_crew_energy);
            tvExperience = itemView.findViewById(R.id.tv_crew_experience);
        }
    }
}
