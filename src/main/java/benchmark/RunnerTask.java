package benchmark;

import com.hazelcast.core.HazelcastInstance;

import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class RunnerTask implements Runnable {
    BenchmarkTask benchmarkTask = Benchmark.getBenchmarkTask();

    public void run() {
        HazelcastInstance client = retrieveClient();
        Map<String, Object> map = client.getMap(benchmarkTask.getMapName());

        switch (benchmarkTask.getMethodType()) {
            case GET:
                doGet(map);
                break;
            case PUT:
                doPut(map);
                break;
        }

        releaseClient(client);
    }

    private void doGet(Map<String, Object> map) {
        int key = new Random(benchmarkTask.getMapSize()).nextInt();

        long start = System.currentTimeMillis();
        map.get(key);
        long end = System.currentTimeMillis();

        long timeDifference = (end - start);
        saveStats(String.valueOf(key), timeDifference);
    }

    private void doPut(Map<String, Object> map) {
        String key = UUID.randomUUID().toString();
        String value = BenchmarkUtils.createValueWithKBSize(benchmarkTask.getItemSizeInKB());

        long start = System.currentTimeMillis();
        map.put(key, value);
        long end = System.currentTimeMillis();

        long timeDifference = (end - start);
        saveStats(key, timeDifference);
    }

    private HazelcastInstance retrieveClient() {
        return Benchmark.getPool().borrowObject();
    }

    private void releaseClient(HazelcastInstance client) {
        Benchmark.getPool().returnObject(client);
    }

    private void saveStats(String id, long timeDifference) {
        BenchmarkStatistics.getExecutionTimeMap().put(id, timeDifference);
    }
}

