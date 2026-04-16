package com.mars.colony;

import com.mars.colony.ability.*;
import com.mars.colony.game.*;
import com.mars.colony.model.*;

/**
 * GlobalCrystalSystemDemo - 全球水晶仓库系统演示
 * 展示水晶如何进入仓库，然后玩家如何分配给宇航员
 */
public class GlobalCrystalSystemDemo {

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║   全球水晶仓库系统演示                  ║");
        System.out.println("╚════════════════════════════════════════╝\n");

        // 1. 创建殖民地
        System.out.println("[第1步] 初始化殖民地\n");
        Colony colony = new Colony();

        // 2. 招募宇航员
        System.out.println("[第2步] 招募宇航员\n");
        CrewMember pilot = new Pilot(colony.getStorage().getNextCrewId(), "Nova", 0);
        pilot.setSpecialAbility(new PilotEvasion());
        colony.addCrewMember(pilot);

        CrewMember soldier = new Soldier(colony.getStorage().getNextCrewId(), "Rex", 0);
        soldier.setSpecialAbility(new SoldierCriticalStrike());
        colony.addCrewMember(soldier);

        System.out.println("✓ 已招募 2 名宇航员\n");

        // 3. 显示初始状态
        System.out.println("[第3步] 初始状态\n");
        System.out.println(colony.getStatsString());
        System.out.println(String.format("仓库水晶: %d\n", colony.getCrystalsInStorage()));

        // 4. 启动战斗
        System.out.println("[第4步] 启动多次战斗以积累水晶\n");

        MissionControl missionControl = new MissionControl(colony);
        CrystalAllocator allocator = new CrystalAllocator(colony);

        // 进行 3 次战斗
        for (int i = 1; i <= 3; i++) {
            System.out.println(String.format("--- 战斗 #%d ---", i));

            // 重置宇航员位置
            colony.moveCrewTo(pilot.getId(), "MISSION_CONTROL");
            colony.moveCrewTo(soldier.getId(), "MISSION_CONTROL");

            // 生成威胁
            Threat threat = missionControl.generateThreat();
            System.out.println("威胁: " + threat.getName() + " (难度: " + threat.calculateDifficulty() + ")\n");

            // 启动交互式战斗
            InteractiveBattle battle = missionControl.startInteractiveMission(pilot, soldier, threat);

            // 模拟战斗 (简化：直接设置威胁为已打败)
            threat.setEnergy(0);
            threat.setAlive(false);
            
            // 完成战斗
            missionControl.finalizeInteractiveMission(battle);
            System.out.println(String.format("当前仓库水晶: %d\n", colony.getCrystalsInStorage()));
        }

        // 5. 显示当前可升级的选项
        System.out.println("[第5步] 水晶分配选项\n");
        allocator.displayStorageStatus();

        // 6. 进行升级
        System.out.println("\n[第6步] 进行宇航员升级\n");

        boolean upgraded1 = allocator.upgradeCrewSkill(pilot.getId(), "Evasion");
        System.out.println(String.format("升级后仓库水晶: %d\n", allocator.getAvailableCrystals()));

        boolean upgraded2 = allocator.upgradeCrewSkill(soldier.getId(), "CriticalStrike");
        System.out.println(String.format("升级后仓库水晶: %d\n", allocator.getAvailableCrystals()));

        // 7. 再升级一次
        System.out.println("[第7步] 再次升级\n");
        boolean upgraded3 = allocator.upgradeCrewSkill(pilot.getId(), "Evasion");
        System.out.println(String.format("升级后仓库水晶: %d\n", allocator.getAvailableCrystals()));

        // 8. 最终状态
        System.out.println("[第8步] 最终状态\n");
        System.out.println("宇航员技能等级:");
        System.out.println(String.format("  Nova 闪避: Lv.%d", pilot.getSpecialAbility().getCurrentLevel()));
        System.out.println(String.format("  Rex 暴击: Lv.%d", soldier.getSpecialAbility().getCurrentLevel()));
        System.out.println(String.format("\n仓库剩余水晶: %d", allocator.getAvailableCrystals()));

        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║   演示完成！                            ║");
        System.out.println("╚════════════════════════════════════════╝");
    }
}
