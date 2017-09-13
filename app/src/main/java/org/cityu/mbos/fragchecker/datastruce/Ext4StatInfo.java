package org.cityu.mbos.fragchecker.datastruce;

import java.util.TreeMap;

/**
 * Created by Hubery on 2017/6/21.
 */

public class Ext4StatInfo {

    private TreeMap<Integer, Long> dofMap = null;
    private TreeMap<String, Float> densityMap = null;
    private TreeMap<Integer, Float> aDofMap = null;
    private double Usage;
    private String time;

    public Ext4StatInfo() {
    }

    public Ext4StatInfo(TreeMap<Integer, Long> dofMap, TreeMap<String, Float> densityMap) {
        this.dofMap = dofMap;
        this.densityMap = densityMap;
    }

    public TreeMap<Integer, Long> getDofMap() {
        return dofMap;
    }

    public void setDofMap(TreeMap<Integer, Long> dofMap) {
        this.dofMap = dofMap;
    }

    public TreeMap<String, Float> getDensityMap() {
        return densityMap;
    }

    public void setDensityMap(TreeMap<String, Float> densityMap) {
        this.densityMap = densityMap;
    }

    public double getUsage() {
        return Usage;
    }

    public void setUsage(double usage) {
        Usage = usage;
    }

    public TreeMap<Integer, Float> getaDofMap() {
        return aDofMap;
    }

    public void setaDofMap(TreeMap<Integer, Float> aDofMap) {
        this.aDofMap = aDofMap;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return null;
    }

}
