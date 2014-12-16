package benchmark;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class BenchmarkPerform {

    private ObjectPool<HazelcastInstance> pool;
    private Map<String, Long> time = new HashMap();

    public static void main (String[] args) throws InterruptedException {

        BenchmarkPerform perf = new BenchmarkPerform();

        perf.pool = new ObjectPool<HazelcastInstance>(50)
        {
            protected HazelcastInstance createObject() {
                HazelcastInstance client =  HazelcastClient.newHazelcastClient();
                return client;
            }
        };

        // Run test
        ExecutorService executor = Executors.newFixedThreadPool(300);

        for (int i=0; i<10000; i++) {
            executor.execute(new RunnerTask(perf.pool, perf.time));
        }

        executor.shutdown();

        try {
            executor.awaitTermination(40, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        while (!executor.isTerminated()) {
            Thread.sleep(100);
        }


        // Calculate average, minor and max
        //HazelcastClient.shutdownAll();

        System.out.println("Average: " + perf.average(perf.time));
    }

    private Long average (Map<String, Long> delay) {
        long value = 0;
        int count = 0;
        for (Map.Entry<String, Long> entry : delay.entrySet())
        {
            value += entry.getValue();
            count++;
        }

        System.out.println("Total: " + count);
        return value/count;
    }

}
