package benchmark;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.core.HazelcastInstance;

import java.util.Map;

public class BenchmarkUtils {
    public static String createValueWithKBSize(int sizeInKB) {
        int stringLength = sizeInKB / 2; //char = 2 bytes
        stringLength = stringLength * 1024;
        StringBuilder sb = new StringBuilder(stringLength);

        for (int i=0; i < stringLength; i++) {
            sb.append('a');
        }

        return sb.toString();
    }

    public static ObjectPool<HazelcastInstance> initiatePool(BenchmarkTask benchmarkTask) {
        final ClientConfig clientConfig = new ClientConfig();
        clientConfig.getNetworkConfig().addAddress("184.73.62.74","184.73.62.74:5702");
        clientConfig.getNetworkConfig().setSmartRouting(false);
        clientConfig.setGroupConfig(new GroupConfig("dev","dev-pass"));

        ObjectPool<HazelcastInstance> pool = new ObjectPool<HazelcastInstance>(benchmarkTask.getClientPoolSize()) {
            protected HazelcastInstance createObject() {
                HazelcastInstance client = HazelcastClient.newHazelcastClient(clientConfig);
                return client;
            }
        };

        return pool;
    }

    public static void populateDefaultMap(BenchmarkTask benchmarkTask) {
        HazelcastInstance client = HazelcastClient.newHazelcastClient();
        Map<Integer, String> map = client.getMap(benchmarkTask.getMapName());

        String value = BenchmarkUtils.createValueWithKBSize(benchmarkTask.getItemSizeInKB());

        for (int i = 0; i <= benchmarkTask.getMapSize(); i++) {
            map.put(i, value);
        }
    }
}
