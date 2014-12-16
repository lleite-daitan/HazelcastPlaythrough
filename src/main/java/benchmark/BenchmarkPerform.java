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
    private Map<String, Long> delayPut = new HashMap();
    private Map<String, Long> delayGet = new HashMap();

    public static void main(String[] args) throws InterruptedException {

        BenchmarkPerform perf = new BenchmarkPerform();

        perf.pool = new ObjectPool<HazelcastInstance>(50) {
            protected HazelcastInstance createObject() {
                HazelcastInstance client = HazelcastClient.newHazelcastClient();
                return client;
            }
        };

        // Run test
        ExecutorService executor = Executors.newFixedThreadPool(300);

        long start = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            executor.execute(new RunnerTask(perf.pool, perf.delayPut, perf.delayGet));
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

        System.out.print("Total time ms: " + (System.currentTimeMillis() - start));


        // Calculate average, minor and max
        //HazelcastClient.shutdownAll();
        Statistics putStat = perf.average(perf.delayPut);
        Statistics getStat = perf.average(perf.delayGet);
        System.out.println("Average put: " + putStat.getAverage());
        System.out.println("Average get: " + getStat.getAverage());
        System.out.println("% Under 1 ms put: " + putStat.getTill1ms());
        System.out.println("% Under 1 ms get: " + getStat.getTill1ms());
        System.out.println("% Under 2 ms put: " + putStat.getTill2ms());
        System.out.println("% Under 2 ms get: " + getStat.getTill2ms());
        System.out.println("Request per second put: " + putStat.getRequestPerSecond());
        System.out.println("Request per second get: " + getStat.getRequestPerSecond());

        HazelcastClient.shutdownAll();
    }

    private Statistics average(Map<String, Long> delay) {
        Statistics stat = new Statistics();

        long value = 0;
        int count = 0;
        int totalMs = 0;
        int below1ms = 0;
        int below2ms = 0;

        for (Map.Entry<String, Long> entry : delay.entrySet()) {
            value += entry.getValue();

            if (entry.getValue() < 2) {
                below1ms++;
            } else if (entry.getValue() < 3) {
                below2ms++;
            }

            count++;

        }

        System.out.println("Total: " + count);
        stat.setAverage((double)value / (double)count);
        Double till1ms = ((double)below1ms / (double)count)*100;
        stat.setTill1ms(till1ms);
        stat.setTill2ms((((double)below2ms / (double)count)*100) + till1ms);
        stat.setRequestPerSecond((double)count / ((double)value/1000));

        return stat;
    }
}

    class Statistics {
        private Double requestPerSecond;
        private Double average;
        private Double till1ms;
        private Double till2ms;

        public Double getRequestPerSecond() {
            return requestPerSecond;
        }

        public void setRequestPerSecond(Double requestPerSecond) {
            this.requestPerSecond = requestPerSecond;
        }

        public Double getAverage() {
            return average;
        }

        public void setAverage(Double average) {
            this.average = average;
        }

        public Double getTill1ms() {
            return till1ms;
        }

        public void setTill1ms(Double till1ms) {
            this.till1ms = till1ms;
        }

        public Double getTill2ms() {
            return till2ms;
        }

        public void setTill2ms(Double till2ms) {
            this.till2ms = till2ms;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Statistics{");
            sb.append("requestPerSecond=").append(requestPerSecond);
            sb.append(", average=").append(average);
            sb.append(", till1ms=").append(till1ms);
            sb.append(", till2ms=").append(till2ms);
            sb.append('}');
            return sb.toString();
        }
    }