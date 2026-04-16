package com.mars.colony.game;

import com.mars.colony.model.CrewMember;
import com.mars.colony.model.Threat;
import java.util.List;

public class LootSystem {

    public static int calculateCrystalReward(Threat threat, List<CrewMember> survivors) {
        if (threat == null || survivors == null) {
            return 1;
        }

        MissionDifficulty difficulty = getMissionDifficulty(threat);
        int baseCrystals = difficulty.getBaseCrystalReward();
        int survivorBonus = survivors.size();
        int experienceBonus = survivors.size();

        return Math.max(1, baseCrystals + survivorBonus + experienceBonus);
    }

    public static String getCrystalRewardReport(Threat threat, List<CrewMember> survivors) {
        if (threat == null) {
            return "No reward";
        }

        MissionDifficulty difficulty = getMissionDifficulty(threat);
        int baseCrystals = difficulty.getBaseCrystalReward();
        int survivorCount = survivors == null ? 0 : survivors.size();
        int totalCrystals = calculateCrystalReward(threat, survivors);

        return String.format(
                "[Reward] Difficulty: %s | Base: %d | Survivor bonus: +%d | Experience bonus: +%d | Total: %d",
                difficulty,
                baseCrystals,
                survivorCount,
                survivorCount,
                totalCrystals
        );
    }

    public static MissionDifficulty getMissionDifficulty(Threat threat) {
        if (threat == null) {
            return MissionDifficulty.EASY;
        }

        int difficulty = threat.calculateDifficulty();
        if (difficulty <= 5) {
            return MissionDifficulty.EASY;
        } else if (difficulty <= 10) {
            return MissionDifficulty.NORMAL;
        } else if (difficulty <= 15) {
            return MissionDifficulty.HARD;
        }
        return MissionDifficulty.EXTREME;
    }

    public enum MissionDifficulty {
        EASY(2),
        NORMAL(4),
        HARD(7),
        EXTREME(12);

        private final int baseCrystalReward;

        MissionDifficulty(int baseCrystalReward) {
            this.baseCrystalReward = baseCrystalReward;
        }

        public int getBaseCrystalReward() {
            return baseCrystalReward;
        }

        @Override
        public String toString() {
            switch (this) {
                case EASY:
                    return "Easy";
                case NORMAL:
                    return "Normal";
                case HARD:
                    return "Hard";
                case EXTREME:
                    return "Extreme";
                default:
                    return "Unknown";
            }
        }
    }
}
