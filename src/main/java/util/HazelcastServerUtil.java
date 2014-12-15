package util;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import java.util.ArrayList;
import java.util.List;

public class HazelcastServerUtil {
    private Config config = null;

    public HazelcastServerUtil() {}

    public HazelcastServerUtil(Config config) {
        this.config = config;
    }

    public HazelcastInstance createCluster() {
        return createCluster(1).get(0);
    }

    public List<HazelcastInstance> createCluster(int nodes) {
        return addNodeToCluster(nodes);
    }

    public HazelcastInstance addNodeToCluster() {
        return addNodeToCluster(1).get(0);
    }

    public List<HazelcastInstance> addNodeToCluster(int nodes) {
        List<HazelcastInstance> nodeList = new ArrayList<>();

        for (int nodeCount = 0; nodeCount < nodes; nodeCount++) {
            nodeList.add(Hazelcast.newHazelcastInstance(config));
        }

        return nodeList;
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }
}
