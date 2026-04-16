package com.mars.colony.game;

import com.mars.colony.model.CrewMember;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Storage - 数据存储类
 * 管理所有宇航员和他们的数据
 */
public class Storage implements Serializable {
    private static final long serialVersionUID = 1L;

    private HashMap<Integer, CrewMember> crewMap = new HashMap<>();
    private int nextCrewId = 1;

    /**
     * 添加宇航员
     */
    public void addCrew(CrewMember crew) {
        crewMap.put(crew.getId(), crew);
        if (crew.getId() >= nextCrewId) {
            nextCrewId = crew.getId() + 1;
        }
    }

    /**
     * 移除宇航员
     */
    public void removeCrew(int id) {
        crewMap.remove(id);
    }

    /**
     * 获取宇航员
     */
    public CrewMember getCrew(int id) {
        return crewMap.get(id);
    }

    /**
     * 获取所有宇航员
     */
    public List<CrewMember> getAllCrew() {
        return new ArrayList<>(crewMap.values());
    }

    /**
     * 按位置获取宇航员
     */
    public List<CrewMember> getCrewByLocation(String location) {
        List<CrewMember> result = new ArrayList<>();
        for (CrewMember crew : crewMap.values()) {
            if (location.equals(crew.getLocation())) {
                result.add(crew);
            }
        }
        return result;
    }

    /**
     * 获取下一个可用的ID
     */
    public int getNextCrewId() {
        return nextCrewId++;
    }

    /**
     * 获取宇航员总数
     */
    public int getCrewCount() {
        return crewMap.size();
    }

    /**
     * 清空所有宇航员
     */
    public void clear() {
        crewMap.clear();
        nextCrewId = 1;
    }
}
