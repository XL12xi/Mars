package com.mars.colony.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.mars.colony.R;
import com.mars.colony.game.Colony;
import com.mars.colony.game.GameState;
import com.mars.colony.game.InteractiveBattle;
import com.mars.colony.game.MissionControl;
import com.mars.colony.model.CrewMember;
import com.mars.colony.model.Threat;
import java.util.List;

public class BattleActivity extends AppCompatActivity {

    private Colony colony;
    private MissionControl missionControl;
    private InteractiveBattle currentBattle;
    private boolean missionFinalized = false;
    private boolean missionSuccess = false;

    private TextView tvBattlePhase;
    private TextView tvRoundInfo;
    private TextView tvStorageCrystals;

    private TextView tvCrewAStatus;
    private ProgressBar pbCrewAHealth;
    private TextView tvCrewBStatus;
    private ProgressBar pbCrewBHealth;

    private TextView tvThreatStatus;
    private ProgressBar pbThreatHealth;

    private LinearLayout llActionButtons;

    private TextView tvBattleLog;
    private ScrollView svBattleLog;

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
        updateStorageCrystalsDisplay();
    }

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
        currentBattle = missionControl.startInteractiveMission(crewA, crewB, threat);

        if (currentBattle == null) {
            tvBattleResult.setText("Mission could not start");
            tvBattleLog.setText("Both selected crew must be alive before the mission can start.");
            return;
        }

        updateBattleUI();
    }

    private void updateStorageCrystalsDisplay() {
        int storageAmount = colony.getCrystalsInStorage();
        tvStorageCrystals.setText("Storage crystals: " + storageAmount);
    }

    private void updateBattleUI() {
        if (currentBattle == null) {
            return;
        }

        if (currentBattle.isBattleOver()) {
            InteractiveBattle.BattleStatus status = currentBattle.getBattleStatus();
            tvBattlePhase.setText("Current phase: " + getPhaseDisplayName(status.phase));
            tvRoundInfo.setText("Round " + status.round);
            updateCrewStatus(status);
            updateThreatStatus(status);
            finalizeBattle();
            return;
        }

        InteractiveBattle.BattleStatus status = currentBattle.getBattleStatus();

        tvBattlePhase.setText("Current phase: " + getPhaseDisplayName(status.phase));
        tvRoundInfo.setText("Round " + status.round);

        updateCrewStatus(status);
        updateThreatStatus(status);

        switch (status.phase) {
            case PLAYER_CREW_SELECT:
                displayCrewSelection();
                break;
            case PLAYER_ACTION_SELECT:
                displayActionSelection();
                break;
            case PLAYER_TURN:
                currentBattle.executePlayerTurn();
                runOnUiThread(() -> {
                    tvBattleLog.setText(currentBattle.getBattleLog());
                    svBattleLog.post(() -> svBattleLog.fullScroll(ScrollView.FOCUS_DOWN));
                    getWindow().getDecorView().postDelayed(this::updateBattleUI, 1500);
                });
                break;
            case THREAT_TURN:
                currentBattle.executeThreatTurn();
                runOnUiThread(() -> {
                    tvBattleLog.setText(currentBattle.getBattleLog());
                    svBattleLog.post(() -> svBattleLog.fullScroll(ScrollView.FOCUS_DOWN));
                    getWindow().getDecorView().postDelayed(this::updateBattleUI, 1500);
                });
                break;
            case BATTLE_END:
                finalizeBattle();
                break;
            case INITIALIZATION:
            default:
                break;
        }
    }

    private void displayCrewSelection() {
        llActionButtons.removeAllViews();

        List<String> crewList = currentBattle.getControllableCrew();
        List<Integer> crewIndices = currentBattle.getControllableCrewIndices();

        if (crewList.isEmpty()) {
            tvBattleLog.append("\nNo controllable crew members left.\n");
            return;
        }

        for (int i = 0; i < crewList.size(); i++) {
            Button btnCrew = new Button(this);
            String crewInfo = crewList.get(i);
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

    private void displayActionSelection() {
        llActionButtons.removeAllViews();

        int actionIndex = 0;
        for (String action : currentBattle.getAvailableActions()) {
            Button btnAction = new Button(this);
            btnAction.setText(action);
            int finalActionIndex = actionIndex;
            btnAction.setOnClickListener(v -> {
                InteractiveBattle.ActionType actionType = (finalActionIndex == 0)
                        ? InteractiveBattle.ActionType.NORMAL_ATTACK
                        : InteractiveBattle.ActionType.SPECIAL_ABILITY;
                if (currentBattle.selectAction(actionType)) {
                    updateBattleUI();
                }
            });
            llActionButtons.addView(btnAction);
            actionIndex++;
        }
    }

    private void updateCrewStatus(InteractiveBattle.BattleStatus status) {
        String crewAText = String.format(
                "%s (%s)%nHP: %d/%d %s",
                status.crewAName,
                status.crewASpec,
                status.crewAEnergy,
                status.crewAMaxEnergy,
                status.crewAAlive ? "READY" : "DEFEATED"
        );
        tvCrewAStatus.setText(crewAText);
        pbCrewAHealth.setMax(status.crewAMaxEnergy);
        pbCrewAHealth.setProgress(status.crewAEnergy);

        String crewBText = String.format(
                "%s (%s)%nHP: %d/%d %s",
                status.crewBName,
                status.crewBSpec,
                status.crewBEnergy,
                status.crewBMaxEnergy,
                status.crewBAlive ? "READY" : "DEFEATED"
        );
        tvCrewBStatus.setText(crewBText);
        pbCrewBHealth.setMax(status.crewBMaxEnergy);
        pbCrewBHealth.setProgress(status.crewBEnergy);
    }

    private void updateThreatStatus(InteractiveBattle.BattleStatus status) {
        String threatText = String.format(
                "%s%nHP: %d/%d %s",
                status.threatName,
                status.threatEnergy,
                status.threatMaxEnergy,
                status.threatAlive ? "ACTIVE" : "DEFEATED"
        );
        tvThreatStatus.setText(threatText);
        pbThreatHealth.setMax(status.threatMaxEnergy);
        pbThreatHealth.setProgress(status.threatEnergy);
    }

    private void finalizeBattle() {
        if (!missionFinalized) {
            try {
                missionSuccess = missionControl.finalizeInteractiveMission(currentBattle);
                missionFinalized = true;
                System.out.println("[BattleActivity] Finalize mission result: " + missionSuccess);
            } catch (Exception e) {
                System.err.println("[BattleActivity] Error finalizing mission: " + e.getMessage());
                e.printStackTrace();
            }
        }

        updateStorageCrystalsDisplay();

        String resultMessage;
        String finalNote;
        int resultColor;
        if (missionSuccess) {
            resultMessage = "Mission successful";
            finalNote = "Survivors received experience and crystals. Any defeated crew were sent to Medbay.";
            resultColor = Color.rgb(34, 139, 84);
        } else {
            resultMessage = "Mission failed - No Death mode";
            finalNote = "No Death: crew were moved back to Quarters/Medbay, loss stats were recorded, and no rewards were granted.";
            resultColor = Color.rgb(192, 57, 43);
        }

        tvBattleResult.setText(resultMessage);
        tvBattleResult.setTextColor(resultColor);
        tvBattlePhase.setText("Current phase: Battle ended");
        tvBattleLog.setText(buildFinalBattleLog(resultMessage, finalNote, resultColor));
        svBattleLog.post(() -> svBattleLog.fullScroll(ScrollView.FOCUS_DOWN));
        llActionButtons.removeAllViews();
    }

    private SpannableStringBuilder buildFinalBattleLog(String resultMessage, String finalNote, int resultColor) {
        SpannableStringBuilder log = new SpannableStringBuilder();
        log.append(currentBattle.getBattleLog());
        log.append("\n");

        int resultStart = log.length();
        log.append(resultMessage).append("\n").append(finalNote).append("\n");
        log.setSpan(
                new ForegroundColorSpan(resultColor),
                resultStart,
                log.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        return log;
    }

    private String getPhaseDisplayName(InteractiveBattle.BattlePhase phase) {
        switch (phase) {
            case INITIALIZATION:
                return "Initialization";
            case PLAYER_CREW_SELECT:
                return "Choose crew";
            case PLAYER_ACTION_SELECT:
                return "Choose action";
            case PLAYER_TURN:
                return "Crew action";
            case THREAT_TURN:
                return "Threat retaliation";
            case BATTLE_END:
                return "Battle ended";
            default:
                return "Unknown";
        }
    }
}
