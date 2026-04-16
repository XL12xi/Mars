package com.mars.colony.model;

/**
 * Scientist (科学家) - 技能8, 韧性1, 能量17
 */
public class Scientist extends CrewMember {
    private static final long serialVersionUID = 1L;
    private boolean analysisActive = false;
    
    public Scientist(int id, String name, int imageResource) {
        super(id, name, "Scientist", 8, 1, 17, imageResource);
    }

    public boolean isAnalysisActive() { return analysisActive; }
    public void setAnalysisActive(boolean active) { analysisActive = active; }
}
