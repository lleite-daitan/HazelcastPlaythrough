package gettingStarted;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;

import java.util.Map;

/**
 * Created by Lucas on 12/13/2014.
 */
public class Client {
    public static void main(String[] args) {
        ClientConfig clientConfig = new ClientConfig();

        HazelcastInstance hazelcastClient = HazelcastClient.newHazelcastClient(clientConfig);
        Map<Integer,String> map = hazelcastClient.getMap("Teste");
        map.put(1, "Test 1");
        map.put(2, "Test 2");
        map.put(3, "Test 3");

        System.out.println(map.get(1));

        System.exit(0);
    }
}
