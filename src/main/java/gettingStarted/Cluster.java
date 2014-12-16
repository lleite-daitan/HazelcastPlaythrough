package gettingStarted;

import com.hazelcast.core.Hazelcast;

public class Cluster {
    public static void main(String[] args) {
        Hazelcast.newHazelcastInstance();
    }
}
