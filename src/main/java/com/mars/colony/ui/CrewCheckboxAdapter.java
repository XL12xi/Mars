package com.mars.colony.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.mars.colony.R;
import com.mars.colony.model.CrewMember;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CrewCheckboxAdapter extends RecyclerView.Adapter<CrewCheckboxAdapter.ViewHolder> {

    private final List<CrewMember> crewList;
    private final OnCrewCheckChangedListener listener;
    private final Set<Integer> checkedCrewIds = new HashSet<>();

    public interface OnCrewCheckChangedListener {
        void onCheckChanged(CrewMember crew, boolean isChecked);
    }

    public CrewCheckboxAdapter(List<CrewMember> crewList, OnCrewCheckChangedListener listener) {
        this.crewList = crewList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_crew_checkbox, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CrewMember crew = crewList.get(position);

        if (crew.getImageResource() != 0) {
            holder.ivCrewImage.setImageResource(crew.getImageResource());
        }
        holder.cbSelected.setOnCheckedChangeListener(null);
        holder.cbSelected.setText(crew.getName());
        holder.cbSelected.setChecked(checkedCrewIds.contains(crew.getId()));
        holder.tvSpecialization.setText("Role: " + crew.getSpecialization());
        holder.tvStatus.setText(String.format(
                "Energy: %d/%d | Experience: %d%nMissions: %d | Wins: %d | Losses: %d | Win rate: %.1f%%%nTraining: %d | Medbay visits: %d",
                crew.getEnergy(),
                crew.getMaxEnergy(),
                crew.getExperience(),
                crew.getMissionsCompleted(),
                crew.getVictoriesCount(),
                crew.getLossesCount(),
                crew.getWinRate(),
                crew.getTrainingSessions(),
                crew.getMedbayVisits()
        ));

        holder.cbSelected.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkedCrewIds.add(crew.getId());
            } else {
                checkedCrewIds.remove(crew.getId());
            }
            listener.onCheckChanged(crew, isChecked);
        });
    }

    @Override
    public int getItemCount() {
        return crewList.size();
    }

    public void clearAllChecked() {
        checkedCrewIds.clear();
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbSelected;
        TextView tvSpecialization;
        TextView tvStatus;
        ImageView ivCrewImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCrewImage = itemView.findViewById(R.id.iv_crew_image);
            cbSelected = itemView.findViewById(R.id.cb_crew_selected);
            tvSpecialization = itemView.findViewById(R.id.tv_crew_specialization);
            tvStatus = itemView.findViewById(R.id.tv_crew_status);
        }
    }
}
