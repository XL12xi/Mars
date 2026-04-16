package com.mars.colony.model;

/**
 * Robot (机器人) - 技能7, 韧性2, 能量22 (NEW CHARACTER)
 * 最高的能量值,独特的自修复技能
 */
public class Robot extends CrewMember {
    private static final long serialVersionUID = 1L;
    private int roundCount = 0;  // 用于追踪修复周期
    
    public Robot(int id, String name, int imageResource) {
        super(id, name, "Robot", 7, 2, 22, imageResource);
    }

    public int getRoundCount() { return roundCount; }
    public void incrementRoundCount() { roundCount++; }
    public void resetRoundCount() { roundCount = 0; }
}
