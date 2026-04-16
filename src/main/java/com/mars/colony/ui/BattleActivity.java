package com.mars.colony.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import com.mars.colony.R;
import com.mars.colony.game.Colony;
import com.mars.colony.game.GameState;
import com.mars.colony.game.InteractiveBattle;
import com.mars.colony.game.MissionControl;
import com.mars.colony.model.CrewMember;
import com.mars.colony.model.Threat;
import java.util.List;

/**
 * BattleActivity - 交互式回合制战斗
 * 玩家可以选择宇航员和技能，就像宝可梦一样
 */
public class BattleActivity extends AppCompatActivity {

    private Colony colony;
    private MissionControl missionControl;
    private InteractiveBattle currentBattle;
    
    // UI 组件
    private TextView tvBattlePhase;
    private TextView tvRoundInfo;
    private TextView tvStorageCrystals;  // ✨ 新增：显示仓库水晶数
    
    // 宇航员状态显示
    private TextView tvCrewAStatus;
    private ProgressBar pbCrewAHealth;
    private TextView tvCrewBStatus;
    private ProgressBar pbCrewBHealth;
    
    // 威胁状态显示
    private TextView tvThreatStatus;
    private ProgressBar pbThreatHealth;
    
    // 动作按钮容器
    private LinearLayout llActionButtons;
    
    // 日志滚动视图
    private TextView tvBattleLog;
    private ScrollView svBattleLog;
    
    // 返回菜单按钮
    private Button btnReturnToMenu;
    private TextView tvBattleResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle);

        colony = GameState.getColony();
        missionControl = new MissionControl(colony);

        initializeUI();
        startInteractiveBattle();
    }

    /**
     * 初始化 UI 组件
     */
    private void initializeUI() {
        tvBattlePhase = findViewById(R.id.tv_battle_phase);
        tvRoundInfo = findViewById(R.id.tv_round_info);
        tvStorageCrystals = findViewById(R.id.tv_storage_crystals);
        
        tvCrewAStatus = findViewById(R.id.tv_crew_a_status);
        pbCrewAHealth = findViewById(R.id.pb_crew_a_health);
        tvCrewBStatus = findViewById(R.id.tv_crew_b_status);
        pbCrewBHealth = findViewById(R.id.pb_crew_b_health);
        
        tvThreatStatus = findViewById(R.id.tv_threat_status);
        pbThreatHealth = findViewById(R.id.pb_threat_health);
        
        llActionButtons = findViewById(R.id.ll_action_buttons);
        
        tvBattleLog = findViewById(R.id.tv_battle_log);
        svBattleLog = findViewById(R.id.sv_battle_log);
        
        btnReturnToMenu = findViewById(R.id.btn_return_to_menu);
        tvBattleResult = findViewById(R.id.tv_battle_result);
        
        btnReturnToMenu.setOnClickListener(v -> finish());
        
        // 更新水晶仓库显示
        updateStorageCrystalsDisplay();
    }

    /**
     * 启动交互式战斗
     */
    private void startInteractiveBattle() {
        String crewAName = getIntent().getStringExtra("crew_a_name");
        String crewBName = getIntent().getStringExtra("crew_b_name");
        String threatName = getIntent().getStringExtra("threat_name");
        String threatType = getIntent().getStringExtra("threat_type");
        int threatSkill = getIntent().getIntExtra("threat_skill", 5);
        int threatResilience = getIntent().getIntExtra("threat_resilience", 2);
        int threatMaxEnergy = getIntent().getIntExtra("threat_max_energy", 20);

        CrewMember crewA = colony.findCrewByName(crewAName);
        CrewMember crewB = colony.findCrewByName(crewBName);

        if (crewA == null || crewB == null) {
            tvBattleResult.setText("Mission could not start");
            tvBattleLog.setText("Selected crew were not found in the current game state.");
            return;
        }

        Threat threat = new Threat(threatName, threatType, threatSkill, threatResilience, threatMaxEnergy);
        
        // 启动交互式战斗
        currentBattle = missionControl.startInteractiveMission(crewA, crewB, threat);
        
        if (currentBattle == null) {
            tvBattleResult.setText("Mission could not start");
            return;
        }
        
        // 开始战斗循环
        updateBattleUI();
    }

    /**
     * 更新水晶仓库显示
     */
    private void updateStorageCrystalsDisplay() {
        int storageAmount = colony.getCrystalsInStorage();
        tvStorageCrystals.setText("💎 水晶仓库: " + storageAmount);
    }

    /**
     * 更新战斗 UI 和处理战斗流程
     */
    private void updateBattleUI() {
        if (currentBattle.isBattleOver()) {
            // 战斗结束
            finalizeBattle();
            return;
        }

        InteractiveBattle.BattleStatus status = currentBattle.getBattleStatus();
        
        // 更新基本信息
        tvBattlePhase.setText("当前阶段: " + getPhaseDisplayName(status.phase));
        tvRoundInfo.setText("第 " + status.round + " 回合");
        
        // 更新宇航员状态
        updateCrewStatus(status);
        
        // 更新威胁状态
        updateThreatStatus(status);
        
        // 根据战斗阶段处理
        switch(status.phase) {
            case PLAYER_CREW_SELECT:
                displayCrewSelection();
                break;
                
            case PLAYER_ACTION_SELECT:
                displayActionSelection();
                break;
                
            case PLAYER_TURN:
                // 自动执行玩家回合
                currentBattle.executePlayerTurn();
                runOnUiThread(() -> {
                    tvBattleLog.append(currentBattle.getBattleLog());
                    svBattleLog.post(() -> svBattleLog.fullScroll(ScrollView.FOCUS_DOWN));
                    // 延迟后继续更新
                    getWindow().getDecorView().postDelayed(this::updateBattleUI, 1500);
                });
                return;
                
            case THREAT_TURN:
                // 自动执行威胁反击
                currentBattle.executeThreatTurn();
                runOnUiThread(() -> {
                    tvBattleLog.append(currentBattle.getBattleLog());
                    svBattleLog.post(() -> svBattleLog.fullScroll(ScrollView.FOCUS_DOWN));
                    // 延迟后继续更新
                    getWindow().getDecorView().postDelayed(this::updateBattleUI, 1500);
                });
                return;
                
            case BATTLE_END:
                finalizeBattle();
                break;
        }
    }

    /**
     * 显示宇航员选择界面
     */
    private void displayCrewSelection() {
        llActionButtons.removeAllViews();
        
        List<String> crewList = currentBattle.getControllableCrew();
        List<Integer> crewIndices = currentBattle.getControllableCrewIndices();
        
        if (crewList.isEmpty()) {
            // 没有活着的宇航员，战斗应该已经结束
            tvBattleLog.append("\n[ERROR] No controllable crew members left! Battle should have ended.\n");
            return;
        }
        
        for (int i = 0; i < crewList.size(); i++) {
            Button btnCrew = new Button(this);
            String crewInfo = crewList.get(i);
            // 移除标记，只显示宇航员信息
            btnCrew.setText(crewInfo.replace("[CREW_A] ", "").replace("[CREW_B] ", ""));
            
            final int crewIndex = crewIndices.get(i);
            btnCrew.setOnClickListener(v -> {
                if (currentBattle.selectCrew(crewIndex)) {
                    updateBattleUI();
                }
            });
            llActionButtons.addView(btnCrew);
        }
    }

    /**
     * 显示动作选择界面
     */
    private void displayActionSelection() {
        llActionButtons.removeAllViews();
        
        int actionIndex = 0;
        for (String action : currentBattle.getAvailableActions()) {
            Button btnAction = new Button(this);
            btnAction.setText(action);
            int finalActionIndex = actionIndex;
            btnAction.setOnClickListener(v -> {
                InteractiveBattle.ActionType actionType = (finalActionIndex == 0) ?
                        InteractiveBattle.ActionType.NORMAL_ATTACK :
                        InteractiveBattle.ActionType.SPECIAL_ABILITY;
                if (currentBattle.selectAction(actionType)) {
                    updateBattleUI();
                }
            });
            llActionButtons.addView(btnAction);
            actionIndex++;
        }
    }

    /**
     * 更新宇航员状态显示
     */
    private void updateCrewStatus(InteractiveBattle.BattleStatus status) {
        // 宇航员 A
        String crewAText = String.format("%s (%s)\nHP: %d/%d %s",
                status.crewAName, status.crewASpec,
                status.crewAEnergy, status.crewAMaxEnergy,
                status.crewAAlive ? "✓" : "✗ 已击败");
        tvCrewAStatus.setText(crewAText);
        pbCrewAHealth.setMax(status.crewAMaxEnergy);
        pbCrewAHealth.setProgress(status.crewAEnergy);
        
        // 宇航员 B
        String crewBText = String.format("%s (%s)\nHP: %d/%d %s",
                status.crewBName, status.crewBSpec,
                status.crewBEnergy, status.crewBMaxEnergy,
                status.crewBAlive ? "✓" : "✗ 已击败");
        tvCrewBStatus.setText(crewBText);
        pbCrewBHealth.setMax(status.crewBMaxEnergy);
        pbCrewBHealth.setProgress(status.crewBEnergy);
    }

    /**
     * 更新威胁状态显示
     */
    private void updateThreatStatus(InteractiveBattle.BattleStatus status) {
        String threatText = String.format("%s\nHP: %d/%d %s",
                status.threatName,
                status.threatEnergy, status.threatMaxEnergy,
                status.threatAlive ? "✓" : "✗ 已击败");
        tvThreatStatus.setText(threatText);
        pbThreatHealth.setMax(status.threatMaxEnergy);
        pbThreatHealth.setProgress(status.threatEnergy);
    }

    /**
     * 完成战斗 - 分配奖励并显示结果
     */
    private void finalizeBattle() {
        try {
            boolean success = missionControl.finalizeInteractiveMission(currentBattle);
            System.out.println("[BattleActivity] Finalize mission result: " + success);
        } catch (Exception e) {
            System.err.println("[BattleActivity] Error finalizing mission: " + e.getMessage());
            e.printStackTrace();
        }

        // 更新水晶仓库显示
        updateStorageCrystalsDisplay();

        InteractiveBattle.BattleStatus status = currentBattle.getBattleStatus();
        
        if (currentBattle.didCrewWin()) {
            tvBattleResult.setText("🎉 任务成功！");
        } else {
            tvBattleResult.setText("❌ 任务失败！");
        }
        
        // 显示完整的战斗日志
        tvBattleLog.setText(currentBattle.getBattleLog());
        svBattleLog.post(() -> svBattleLog.fullScroll(ScrollView.FOCUS_DOWN));
        
        llActionButtons.removeAllViews();
    }

    /**
     * 获取阶段的显示名称
     */
    private String getPhaseDisplayName(InteractiveBattle.BattlePhase phase) {
        switch(phase) {
            case INITIALIZATION: return "初始化";
            case PLAYER_CREW_SELECT: return "选择宇航员";
            case PLAYER_ACTION_SELECT: return "选择动作";
            case PLAYER_TURN: return "执行动作";
            case THREAT_TURN: return "敌人反击";
            case BATTLE_END: return "战斗结束";
            default: return "未知";
        }
    }
}
