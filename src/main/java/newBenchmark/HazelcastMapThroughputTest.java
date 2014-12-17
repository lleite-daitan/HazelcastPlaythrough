package newBenchmark;

import benchmark.BenchmarkUtils;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HazelcastMapThroughputTest {
  private static final Logger LOG = LoggerFactory.getLogger(HazelcastMapThroughputTest.class);

  private static final int TOTAL = 1000000;
  private static final int LAP   = 50000;

  public static void main(String[] args) throws InterruptedException {
    final ClientConfig clientConfig = new ClientConfig();
    clientConfig.setGroupConfig(new GroupConfig("test"));
    final HazelcastInstance client = HazelcastClient.newHazelcastClient(clientConfig);
    final IMap<Object, Object> map = client.getMap("test");

    String value = BenchmarkUtils.createValueWithKBSize(3);

    long start = System.currentTimeMillis();
    long lastLap = start;
    LOG.info((System.currentTimeMillis() - lastLap) + " Start PUTing msgs");
    for (int i = 1; i < TOTAL + 1; ++i) {
      map.put(i, value);

      if (i % LAP == 0) {
        if (i > 0) {
          final long lapTime = System.currentTimeMillis() - lastLap;
          LOG.info(String.format("[PUT] messages %d/%d = %dms (%f msg/sec)", i, TOTAL, lapTime, ((float) LAP * 1000 / lapTime)));
          lastLap = System.currentTimeMillis();
        }
      }
    }


    start = System.currentTimeMillis();
    lastLap = start;
    LOG.info((System.currentTimeMillis() - lastLap) + " Start GETing msgs");
    for (int i = 0; i < TOTAL ; i++) {
        map.get(i);
        if (i % LAP == 0) {
          if (i > 0) {
            final long lapTime = System.currentTimeMillis() - lastLap;
            LOG.info(String.format("[GET] messages %d/%d = %dms (%f msg/sec)", i, TOTAL, lapTime, ((float) LAP * 1000 / lapTime)));
            lastLap = System.currentTimeMillis();
          }
        }

    }

    LOG.info((System.currentTimeMillis() - start) + " Finished sending msgs");

    LOG.info((System.currentTimeMillis() - start) + " Test finished");
  }
}