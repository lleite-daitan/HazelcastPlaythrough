package benchmark;

import com.hazelcast.core.HazelcastInstance;

import java.util.Map;
import java.util.UUID;

public class RunnerTask implements Runnable {

    private ObjectPool<HazelcastInstance> pool;

    private Map<String, Long> delayPut;
    private Map<String, Long> delayGet;

    public RunnerTask(ObjectPool<HazelcastInstance> pool, Map<String, Long> delayPut, Map<String, Long> delayGet) {
        this.pool = pool;
        this.delayPut = delayPut;
        this.delayGet = delayGet;
    }

    public void run() {
        // get an object from the pool
        HazelcastInstance client = pool.borrowObject();

        String id = UUID.randomUUID().toString();
        Map<String, Object> map = client.getMap("calls");
        long time = System.currentTimeMillis();
        map.put(id, "anystring");
        long difference = System.currentTimeMillis() - time;

        delayPut.put(id, difference);

        // Do a get
        time = System.currentTimeMillis();
        map = client.getMap("calls");
        map.get(id);
        difference = System.currentTimeMillis() - time;

        delayGet.put(id, difference);

        // return ExportingProcess instance back to the pool
        pool.returnObject(client);
    }
}