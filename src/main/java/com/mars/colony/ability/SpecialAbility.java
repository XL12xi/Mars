package com.mars.colony.ability;

import com.mars.colony.model.CrewMember;
import com.mars.colony.model.Threat;

/**
 * Common interface for all crew special abilities.
 * Keeping abilities behind this interface supports polymorphism and extension.
 */
public interface SpecialAbility {

    boolean canUse(CrewMember crew, Threat threat, CrewMember ally);

    void executeAbility(CrewMember crew, Threat threat, CrewMember ally);

    String getAbilityName();

    String getAbilityDescription();

    int getCurrentLevel();

    void setLevel(int level);
}
