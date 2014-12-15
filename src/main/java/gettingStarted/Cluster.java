package gettingStarted;

import com.hazelcast.core.Hazelcast;

/**
 * Created by Lucas on 12/13/2014.
 */
public class Cluster {
    public static void main(String[] args) {
        Hazelcast.newHazelcastInstance();
    }
}
