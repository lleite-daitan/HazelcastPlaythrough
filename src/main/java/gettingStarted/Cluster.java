package gettingStarted;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;

public class Cluster {
    public static void main(String[] args) {
        Config config = new Config();
        config.getGroupConfig().setName("test");
        Hazelcast.newHazelcastInstance(config);
    }
}
