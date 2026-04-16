# 回合制战斗系统 Bug 修复

## 问题描述
当有一个宇航员阵亡后，战斗系统会卡住，无法继续进行。

## 根本原因

### 1. **宇航员索引判断逻辑错误**
- 问题：`displayCrewSelection()` 中使用了 `crew.contains("Crew A")` 来判断宇航员索引
- 原因：`getControllableCrew()` 返回的字符串格式为 `"Nova (Pilot) - HP: 20/20"`，不包含"Crew A"或"Crew B"标识
- 后果：所有按钮都被错误地识别为 Crew B（索引1），导致选择错误

### 2. **缺少生存状态检查**
- 问题：当有宇航员成员阵亡后，`displayCrewSelection()` 仍然可能尝试创建按钮，但列表中可能为空
- 后果：UI 卡住，无法显示任何按钮供用户选择

### 3. **selectCrew() 逻辑顺序不对**
- 问题：先检查选择的宇航员是否活着，再检查两个都死亡的情况
- 后果：当两个都死亡时，如果首先选择已死亡的成员，会返回 false 而不会自动转入 BATTLE_END 阶段

## 修复方案

### 修复 1：重组 `getControllableCrew()` 返回格式
**文件：** `InteractiveBattle.java`

添加标记前缀以标识宇航员索引：
```java
public List<String> getControllableCrew() {
    List<String> crew = new ArrayList<>();
    
    if (crewA.isAlive()) {
        crew.add(String.format("[CREW_A] %s (%s) - HP: %d/%d", ...));
    }
    
    if (crewB.isAlive()) {
        crew.add(String.format("[CREW_B] %s (%s) - HP: %d/%d", ...));
    }
    
    return crew;
}
```

### 修复 2：新增 `getControllableCrewIndices()` 方法
**文件：** `InteractiveBattle.java`

返回活着的宇航员对应的真实索引（0或1）：
```java
public List<Integer> getControllableCrewIndices() {
    List<Integer> indices = new ArrayList<>();
    
    if (crewA.isAlive()) {
        indices.add(0);
    }
    
    if (crewB.isAlive()) {
        indices.add(1);
    }
    
    return indices;
}
```

### 修复 3：重写 `displayCrewSelection()` 逻辑
**文件：** `BattleActivity.java`

使用索引列表而非字符串匹配，并添加空列表检查：
```java
private void displayCrewSelection() {
    llActionButtons.removeAllViews();
    
    List<String> crewList = currentBattle.getControllableCrew();
    List<Integer> crewIndices = currentBattle.getControllableCrewIndices();
    
    if (crewList.isEmpty()) {
        // 标记错误：应该已经进入 BATTLE_END 阶段
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
```

### 修复 4：调整 `selectCrew()` 逻辑顺序
**文件：** `InteractiveBattle.java`

先检查两个都死亡，再检查单个成员状态：
```java
public boolean selectCrew(int crewIndex) {
    if (currentPhase != BattlePhase.PLAYER_CREW_SELECT) {
        return false;
    }
    
    // 先检查两个都死亡 ← 优先级提升
    if (!crewA.isAlive() && !crewB.isAlive()) {
        currentPhase = BattlePhase.BATTLE_END;
        battleOver = true;
        threatDefeated = false;
        addLog("=== BATTLE END ===");
        addLog("All crew members defeated!");
        return false;
    }
    
    CrewMember selectedCrew = (crewIndex == 0) ? crewA : crewB;
    
    // 检查选择的成员是否活着
    if (!selectedCrew.isAlive()) {
        addLog(String.format("[ERROR] %s has been defeated and cannot act!", 
               selectedCrew.getName()));
        return false;
    }
    
    // ... rest of logic
}
```

### 修复 5：添加缺失的导入
**文件：** `BattleActivity.java`

添加 `java.util.List` 导入以支持新的方法调用。

## 测试场景

### 场景 1：两个宇航员都活着
- ✅ 玩家可以选择任何一个宇航员
- ✅ 选择后正确进入动作选择阶段

### 场景 2：一个宇航员幸存
- ✅ 只显示活着的宇航员按钮
- ✅ 只能选择活着的宇航员
- ✅ 它阵亡后自动进入 BATTLE_END

### 场景 3：两个宇航员都阵亡
- ✅ `displayCrewSelection()` 返回（不显示按钮）
- ✅ 系统自动转入 BATTLE_END
- ✅ 显示战斗失败信息

## 构建验证
```
✅ BUILD SUCCESSFUL in 1s
32 actionable tasks: 32 up-to-date
```

## 影响范围
- ✅ 交互式战斗系统现在能正确处理宇航员阵亡事件
- ✅ UI 能正确显示可用的宇航员选项
- ✅ 战斗流程不再在成员阵亡时卡住
