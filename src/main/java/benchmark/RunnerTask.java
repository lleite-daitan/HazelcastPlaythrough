package benchmark;

import com.hazelcast.core.HazelcastInstance;

import java.util.Map;
import java.util.UUID;

public class RunnerTask implements Runnable {

    private ObjectPool<HazelcastInstance> pool;

    private Map<String, Long> delay;

    public RunnerTask(ObjectPool<HazelcastInstance> pool, Map<String, Long> delay) {
        this.pool = pool;
        this.delay = delay;
    }

    public void run() {
        // get an object from the pool
        HazelcastInstance client = pool.borrowObject();

        String id = UUID.randomUUID().toString();
        long time = System.currentTimeMillis();
        Map<String, Object> map = client.getMap("calls");
        map.put(id, "anystring");
        long difference = System.currentTimeMillis() - time;

        delay.put(id, difference);

        // return ExportingProcess instance back to the pool
        pool.returnObject(client);
    }
}