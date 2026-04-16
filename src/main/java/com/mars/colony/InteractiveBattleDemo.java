package com.mars.colony;

import com.mars.colony.game.*;
import com.mars.colony.model.*;

/**
 * InteractiveBattleDemo - 交互式战斗演示程序
 * 展示如何使用新的回合制战斗系统
 * 
 * 用法:
 * 1. UI可以创建InteractiveBattle
 * 2. 根据当前的BattlePhase显示不同的UI界面
 * 3. 玩家选择时调用相应的方法
 * 4. 战斗自动进行，直到结束
 */
public class InteractiveBattleDemo {
    
    public static void main(String[] args) {
        System.out.println("=== Interactive Battle Demo ===\n");
        
        // 创建游戏世界
        Colony colony = new Colony();
        MissionControl missionControl = new MissionControl(colony);
        
        // 创建两个宇航员
        Pilot pilot = new Pilot(1, "Nova", 0);
        Engineer engineer = new Engineer(2, "Rex", 0);
        
        // 生成威胁
        Threat threat = missionControl.generateThreat();
        System.out.println("Generated Threat: " + threat);
        System.out.println();
        
        // 启动交互式战斗
        InteractiveBattle battle = missionControl.startInteractiveMission(pilot, engineer, threat);
        
        if (battle == null) {
            System.out.println("Failed to start mission!");
            return;
        }
        
        // 模拟交互式战斗流程
        simulateInteractiveBattle(battle);
        
        // 战斗结束，分配奖励
        missionControl.finalizeInteractiveMission(battle);
        
        // 显示最终结果
        System.out.println("\n" + battle.getBattleLog());
        System.out.println("\n=== Battle Summary ===");
        System.out.println("Winner: " + (battle.didCrewWin() ? "Crew" : "Threat"));
        System.out.println("Pilot HP: " + pilot.getEnergy() + "/" + pilot.getMaxEnergy());
        System.out.println("Engineer HP: " + engineer.getEnergy() + "/" + engineer.getMaxEnergy());
        System.out.println("Threat HP: " + threat.getEnergy() + "/" + threat.getMaxEnergy());
        System.out.println("Total Rounds: " + battle.getCurrentRound());
    }
    
    /**
     * 模拟交互式战斗流程
     * 这里使用AI自动选择，真实UI应该等待玩家输入
     */
    private static void simulateInteractiveBattle(InteractiveBattle battle) {
        while (!battle.isBattleOver()) {
            InteractiveBattle.BattlePhase phase = battle.getCurrentPhase();
            
            System.out.println("Battle Phase: " + phase + " (Round " + battle.getCurrentRound() + ")");
            
            switch (phase) {
                case PLAYER_CREW_SELECT:
                    // 玩家选择宇航员
                    int crewChoice = selectRandomAliveCrewIndex(battle);
                    System.out.println("Crew selected: " + crewChoice);
                    battle.selectCrew(crewChoice);
                    break;
                    
                case PLAYER_ACTION_SELECT:
                    // 玩家选择动作(AI自动选择)
                    int actionChoice = (int)(Math.random() * 2); // 0=Normal Attack, 1=Special Ability
                    InteractiveBattle.ActionType action = (actionChoice == 0) ?
                            InteractiveBattle.ActionType.NORMAL_ATTACK :
                            InteractiveBattle.ActionType.SPECIAL_ABILITY;
                    System.out.println("Action selected: " + action);
                    battle.selectAction(action);
                    break;
                    
                case PLAYER_TURN:
                    // 执行玩家回合
                    battle.executePlayerTurn();
                    System.out.println("Player turn executed");
                    break;
                    
                case THREAT_TURN:
                    // 执行威胁反击
                    battle.executeThreatTurn();
                    System.out.println("Threat turn executed");
                    break;
                    
                case BATTLE_END:
                    System.out.println("Battle ended!");
                    return;
                    
                default:
                    System.out.println("Unknown phase: " + phase);
                    return;
            }
            
            System.out.println();
        }
    }
    
    /**
     * 选择一个还活着的宇航员
     */
    private static int selectRandomAliveCrewIndex(InteractiveBattle battle) {
        InteractiveBattle.BattleStatus status = battle.getBattleStatus();
        
        if (status.crewAAlive && status.crewBAlive) {
            return (int)(Math.random() * 2);
        } else if (status.crewAAlive) {
            return 0;
        } else if (status.crewBAlive) {
            return 1;
        } else {
            return -1; // 都死了
        }
    }
    
    /**
     * 展示如何在UI中使用战斗状态信息
     */
    private static void demonstrateUIUsage() {
        System.out.println("\n=== UI Usage Example ===\n");
        
        System.out.println("// 获取战斗状态用于UI显示\n" +
        "InteractiveBattle.BattleStatus status = battle.getBattleStatus();\n" +
        "\n" +
        "// 显示两个宇航员的血量条\n" +
        "drawHealthBar(\"Crew A\", status.crewAEnergy, status.crewAMaxEnergy);\n" +
        "drawHealthBar(\"Crew B\", status.crewBEnergy, status.crewBMaxEnergy);\n" +
        "drawHealthBar(\"Threat\", status.threatEnergy, status.threatMaxEnergy);\n" +
        "\n" +
        "// 根据战斗阶段显示不同的UI\n" +
        "switch(status.phase) {\n" +
        "    case PLAYER_CREW_SELECT:\n" +
        "        // 显示两个宇航员的按钮，供玩家选择\n" +
        "        for(String crew : battle.getControllableCrew()) {\n" +
        "            displayCrewButton(crew);\n" +
        "        }\n" +
        "        break;\n" +
        "        \n" +
        "    case PLAYER_ACTION_SELECT:\n" +
        "        // 显示可用的动作按钮\n" +
        "        for(String action : battle.getAvailableActions()) {\n" +
        "            displayActionButton(action);\n" +
        "        }\n" +
        "        break;\n" +
        "        \n" +
        "    case BATTLE_END:\n" +
        "        // 显示战斗结果\n" +
        "        if(status.battleOver) {\n" +
        "            if(battle.didCrewWin()) {\n" +
        "                showVictoryScreen();\n" +
        "            } else {\n" +
        "                showDefeatScreen();\n" +
        "            }\n" +
        "        }\n" +
        "        break;\n" +
        "}\n");
    }
}
