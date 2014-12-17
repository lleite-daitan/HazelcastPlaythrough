package benchmark;

import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.core.IMap;
import com.hazelcast.nio.serialization.SerializationService;
import com.hazelcast.nio.serialization.SerializationServiceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Benchmark {
    static final Logger LOG = LoggerFactory.getLogger(Benchmark.class);

    private static ObjectPool<HazelcastInstance> pool;
    private static BenchmarkTask benchmarkTask = new BenchmarkTask();

    public static void main(String[] args) {
        new Benchmark().run();
    }

    public void run() {
        pool = BenchmarkUtils.initiatePool(benchmarkTask);

        if (benchmarkTask.getMethodType() == MethodType.GET) {
            BenchmarkUtils.populateDefaultMap(benchmarkTask);
        }

        try {
            runTasks();
            printStats();
        } catch (InterruptedException e) {
            LOG.error("Error while waiting for Executors to terminate.", e);
        }

        HazelcastClient.shutdownAll();
        System.exit(0);
    }

    private void runSimpleTest() throws InterruptedException {
        final ClientConfig clientConfig = new ClientConfig();
        clientConfig.setGroupConfig(new GroupConfig("test"));
        HazelcastInstance client = HazelcastClient.newHazelcastClient(clientConfig);

        IMap testMap = client.getMap("customers");
//        String value = BenchmarkUtils.createValueWithKBSize(3);
        String value = "teste";

        int count = 1000000;

        LOG.info("Starting PUT test with count " + count);
        long startPut = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            testMap.put(i, value);
        }
        long endPut = System.currentTimeMillis();

        LOG.info("Starting GET test with count " + count);
        long startGet = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            testMap.get(i);
        }
        long endGet = System.currentTimeMillis();

        long putDifference = endPut - startPut;
        long getDifference = endGet - startGet;

        LOG.info(String.format("%s PUT took %s ms", count, putDifference));
        LOG.info(String.format("%s GET took %s ms", client, getDifference));

        SerializationService ss = new SerializationServiceBuilder()
                .setConfig(clientConfig.getSerializationConfig()).build();
        LOG.info("Binary size of value is " + ss.toData(testMap.get(1)).bufferSize() + " bytes");
    }

    private void runTasks() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(benchmarkTask.getThreadPoolSize());

        long start = System.currentTimeMillis();
        long maxExecutionTime = start + (benchmarkTask.getExecutionTimeInMinutes() * 60 * 1000);
        LOG.debug("Start while loop: " + start);
        LOG.debug("maxExecutionTime: " + maxExecutionTime);
        while (System.currentTimeMillis() < maxExecutionTime) {
//        for (int i = 0; i < benchmarkTask.getOperationCount(); i++) {
            executor.execute(new RunnerTask());
        }
        LOG.debug("End while loop: " + System.currentTimeMillis());

        executor.shutdown();
        executor.awaitTermination(60, TimeUnit.SECONDS);
        LOG.debug("Terminated");

        while (!executor.isTerminated()) {
            LOG.warn("Using 'shutdownNow'");
            executor.shutdownNow();
        }
        long end = System.currentTimeMillis();

        LOG.info("Total Benchmark time: " + (end - start) + "ms");
    }

    private void printStats() {
        BenchmarkStatistics benchmarkStatistics = BenchmarkStatistics.createStatistics();

        LOG.info("Method used: " + benchmarkTask.getMethodType().name());
        LOG.info("Average time: " + benchmarkStatistics.getAverage() + "ms");
        LOG.info("% Under 1 ms put: " + benchmarkStatistics.getTill1ms() + "ms");
        LOG.info("% Under 2 ms get: " + benchmarkStatistics.getTill2ms() + "ms");
        LOG.info("Request per second put: " + benchmarkStatistics.getRequestPerSecond() + "ms");
    }

    public static ObjectPool<HazelcastInstance> getPool() {
        return pool;
    }

    public static BenchmarkTask getBenchmarkTask() {
        return benchmarkTask;
    }
}