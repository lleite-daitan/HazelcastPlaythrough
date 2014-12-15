package dataStructures.map;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.monitor.LocalMapStats;

import java.util.Date;
import java.util.List;

public class MapStatistics {
    private String label;
    private Date time;
    private long size;
    private long ownedEntryCount;
    private long ownedEntryMemoryCost;
    private long backupEntryCount;
    private long backupEntryMemoryCost;

    public static MapStatistics createMapStats(HazelcastInstance node, String mapName, String label) {
        IMap<Integer, String> map = node.getMap(mapName);
        LocalMapStats localMapStats = map.getLocalMapStats();

        MapStatistics mapStatistics = new MapStatistics();
        mapStatistics.setLabel(label);
        mapStatistics.setTime(new Date());
        mapStatistics.setSize(map.size());
        mapStatistics.setOwnedEntryCount(localMapStats.getOwnedEntryCount());
        mapStatistics.setOwnedEntryMemoryCost(localMapStats.getOwnedEntryMemoryCost());
        mapStatistics.setBackupEntryCount(localMapStats.getBackupEntryCount());
        mapStatistics.setBackupEntryMemoryCost(localMapStats.getBackupEntryMemoryCost());

        return mapStatistics;
    }

    public static void printStats(List<MapStatistics> mapStatisticsList){
        for (MapStatistics mapStatistics : mapStatisticsList) {
            System.out.println(mapStatistics.toString());
        }
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getOwnedEntryCount() {
        return ownedEntryCount;
    }

    public void setOwnedEntryCount(long ownedEntryCount) {
        this.ownedEntryCount = ownedEntryCount;
    }

    public long getOwnedEntryMemoryCost() {
        return ownedEntryMemoryCost;
    }

    public void setOwnedEntryMemoryCost(long ownedEntryMemoryCost) {
        this.ownedEntryMemoryCost = ownedEntryMemoryCost;
    }

    public long getBackupEntryCount() {
        return backupEntryCount;
    }

    public void setBackupEntryCount(long backupEntryCount) {
        this.backupEntryCount = backupEntryCount;
    }

    public long getBackupEntryMemoryCost() {
        return backupEntryMemoryCost;
    }

    public void setBackupEntryMemoryCost(long backupEntryMemoryCost) {
        this.backupEntryMemoryCost = backupEntryMemoryCost;
    }

    @Override
    public String toString() {
        return "MapStats{" +
                "label='" + getLabel() + '\'' +
                ", time=" + getTime() +
                ", size=" + getSize() +
                ", ownedEntryCount=" + getOwnedEntryCount() +
                ", ownedEntryMemoryCost=" + getOwnedEntryMemoryCost() +
                ", backupEntryCount=" + getBackupEntryCount() +
                ", backupEntryMemoryCost=" + getBackupEntryMemoryCost() +
                '}';
    }
}
