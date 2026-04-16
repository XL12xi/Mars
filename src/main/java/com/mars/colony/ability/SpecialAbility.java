package com.mars.colony.ability;

import com.mars.colony.model.CrewMember;
import com.mars.colony.model.Threat;

/**
 * 特殊技能接口 - 所有技能必须实现此接口
 * 实现OOP多态设计
 */
public interface SpecialAbility {
    
    /**
     * 检查是否可以使用该技能
     */
    boolean canUse(CrewMember crew, Threat threat, CrewMember ally);
    
    /**
     * 执行技能效果
     */
    void executeAbility(CrewMember crew, Threat threat, CrewMember ally);
    
    /**
     * 获取技能名称
     */
    String getAbilityName();
    
    /**
     * 获取技能描述
     */
    String getAbilityDescription();
    
    /**
     * 获取当前等级(1-5)
     */
    int getCurrentLevel();
    
    /**
     * 设置等级
     */
    void setLevel(int level);
}
