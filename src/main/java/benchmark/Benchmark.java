package benchmark;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;

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

    private void runTasks() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(benchmarkTask.getThreadPoolSize());

        long start = System.currentTimeMillis();
        long maxExecutionTime = start + (benchmarkTask.getExecutionTimeInMinutes() * 60 * 1000);
        LOG.info("Start while loop: " + start);
        LOG.info("maxExecutionTime: " + maxExecutionTime);
        while (System.currentTimeMillis() < maxExecutionTime) {
//        for (int i = 0; i < benchmarkTask.getOperationCount(); i++) {
            executor.execute(new RunnerTask());
        }
        LOG.info("End while loop: " + System.currentTimeMillis());

        executor.shutdown();
        executor.awaitTermination(60, TimeUnit.SECONDS);
        LOG.info("Terminated");

        while (!executor.isTerminated()) {
            LOG.info("USing shutdown now");
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