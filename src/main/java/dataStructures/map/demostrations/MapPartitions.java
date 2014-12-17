package dataStructures.map.demostrations;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.HazelcastInstance;
import dataStructures.map.ExecutionTiming;
import dataStructures.map.MapStatistics;
import util.HazelcastServerUtil;

import java.util.*;

/**
 * This Demo explores the basic capabilities of the distributed maps on Hazelcast.
 *
 * This scenario will create a single Hazelcast instance and get a distributed map from it. This map will be populated
 * with enough data to use all partitions. Then, new nodes will be created and added to the cluster - this will force
 * Hazelcast to, after each node added, repartition the data between the nodes in the cluter.
 * After each step, statistics will be gathered from each node. These will be printed at the end of the test.
 *
 * The statistics data at the end will have the following format:
 * MapStats{label='<USER DEFINED LABEL>', time=<CURRENT TIMESTAMP>, mapSize=<MAP SIZE>, ownedEntryCount=<TOTAL OF ENTRIES OWNED>, ownedEntryMemoryCost=<TOTAL MEMORY FOR ENTRIES OWNED>, backupEntryCount=<TOTAL OF BACKUP ENTRIES>, backupEntryMemoryCost=<TOTAL MEMORY FOR BACKUP ENTRIES>}
 * Where the data is always related to a SINGLE node only. The "ownedEntry" entries refers to data that are primarily
 * stored on this node while "backupEntry" refers to data that is stored as a backup on the node being shown.
 *
 * This demo will use the default values for backup count (1) and additional nodes to be added to the cluster (1).
 * These parameters can be tweaked to create different scenarios - for example adding 4 more nodes and having each entry
 * being backed up on 2 different nodes.
 *
 * The test will populate the map with the same number of default partitions on the node (271).
 */
public class MapPartitions {
    private final MapDemoConfiguration mapDemoConfiguration = new MapDemoConfiguration();
    private final HazelcastServerUtil hazelcastServerUtil = new HazelcastServerUtil(createConfig());
    private final List<MapStatistics> mapStatisticsList = new ArrayList<>();
    private final List<ExecutionTiming> executionTimingList = new ArrayList<>();
    private final String mapName = "mapSizeDemo";

    public static void main(String[] args) {
        MapPartitions mapSizeDemo = new MapPartitions();
        mapSizeDemo.run();
        System.exit(0);
    }

    public void run() {
        List<HazelcastInstance> initialInstanceList = hazelcastServerUtil.createCluster(mapDemoConfiguration.getInitialNodeCount());
        HazelcastInstance instance = initialInstanceList.get(0);
        Map map = instance.getMap(mapName);

        //initialInstanceList.stream().forEach(initialInstance -> gatherStats("Map initialized [initial node]", initialInstance));
        populateMap(map, mapDemoConfiguration.getMapSize());
      //  initialInstanceList.stream().forEach(initialInstance -> gatherStats("Map populated [initial node]", initialInstance));

        timeReadValue("Initial Instances Reads", 2);

        List<HazelcastInstance> newInstanceList = hazelcastServerUtil.addNodeToCluster(mapDemoConfiguration.getAdditionalNodeCount());
       // initialInstanceList.stream().forEach(initialInstance -> gatherStats("Nodes added to cluster [initial node]", initialInstance));

      //  newInstanceList.stream().forEach(newInstance -> gatherStats("Nodes added to cluster [new node]", newInstance));

        timeReadValue("Added Instances Reads", 2);

        mapDemoConfiguration.printConfiguration();
        MapStatistics.printStats(mapStatisticsList);
        ExecutionTiming.printTimings(executionTimingList);
    }

    private void gatherStats(String label, HazelcastInstance instance) {
        MapStatistics mapStatistics = MapStatistics.createMapStats(instance, mapName, label);
        mapStatisticsList.add(mapStatistics);
    }

    private void timeReadValue(String userLabel, int timesToRead) {
        for (int i = 0; i < timesToRead; i++) {
            HazelcastInstance clientInstance = HazelcastClient.newHazelcastClient();
            int mapReadIndex = new Random().nextInt(mapDemoConfiguration.getMapSize());

            long startTime = System.currentTimeMillis();
            clientInstance.getMap(mapName).get(mapReadIndex);
            long endTime = System.currentTimeMillis();

            String label = String.format("%s - Read value for index [%s]", userLabel, mapReadIndex);

            ExecutionTiming executionTiming = new ExecutionTiming(label, new Date(startTime), new Date(endTime));
            executionTimingList.add(executionTiming);
        }
    }

    private Config createConfig() {
        Config config = new Config();

        checkReadBackupValue();

        MapConfig mapConfig = new MapConfig();
        mapConfig.setName( mapName );
        mapConfig.setBackupCount(mapDemoConfiguration.getSyncBackupCount());
        mapConfig.setAsyncBackupCount(mapDemoConfiguration.getAsyncBackupCount());
        mapConfig.setReadBackupData(mapDemoConfiguration.isReadBackupNodes());

        config.addMapConfig(mapConfig);
        return config;
    }

    public void populateMap(Map<Integer, String> map, int size) {
        for (int i = 0; i < size; i++) {
            map.put(i, "Test " + i);
        }
    }

    private void checkReadBackupValue() {
        if (mapDemoConfiguration.getSyncBackupCount() >= 0 && mapDemoConfiguration.getAsyncBackupCount() >= 0) {
            mapDemoConfiguration.setReadBackupNodes(false);
        }
    }
}

class MapDemoConfiguration {
    private final int mapSize = 100000;
    private final int initialNodeCount = 1;
    private final int additionalNodeCount = 1;
    private final int syncBackupCount = 1;
    private final int asyncBackupCount = 0;
    private boolean readBackupNodes = true;

    public void printConfiguration(){
        System.out.println(toString());
    }

    public int getMapSize() {
        return mapSize;
    }

    public int getInitialNodeCount() {
        return initialNodeCount;
    }

    public int getAdditionalNodeCount() {
        return additionalNodeCount;
    }

    public int getSyncBackupCount() {
        return syncBackupCount;
    }

    public int getAsyncBackupCount() {
        return asyncBackupCount;
    }

    public boolean isReadBackupNodes() {
        return readBackupNodes;
    }

    public void setReadBackupNodes(boolean readBackupNodes) {
        this.readBackupNodes = readBackupNodes;
    }

    @Override
    public String toString() {
        return "MapDemoConfiguration{" +
                "mapSize=" + mapSize +
                ", initialNodeCount=" + initialNodeCount +
                ", additionalNodeCount=" + additionalNodeCount +
                ", syncBackupCount=" + syncBackupCount +
                ", asyncBackupCount=" + asyncBackupCount +
                ", readBackupNodes=" + readBackupNodes +
                '}';
    }
}