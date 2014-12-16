package benchmark;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class BenchmarkStatistics {
    static final Logger LOG = LoggerFactory.getLogger(BenchmarkStatistics.class);

    private static Map<String, Long> executionTimeMap = new HashMap();

    private Double requestPerSecond;
    private Double average;
    private Double till1ms;
    private Double till2ms;

    public static BenchmarkStatistics createStatistics() {
        BenchmarkStatistics statistics = new BenchmarkStatistics();

        long value = 0;
        int count = 0;
        int totalMs = 0;
        int below1ms = 0;
        int below2ms = 0;

        for (Map.Entry<String, Long> entry : executionTimeMap.entrySet()) {
            value += entry.getValue();

            if (entry.getValue() < 2) {
                below1ms++;
            } else if (entry.getValue() < 3) {
                below2ms++;
            }

            count++;
        }

        LOG.info("Total: " + count);
        statistics.setAverage((double) value / (double) count);
        Double till1ms = ((double)below1ms / (double)count)*100;
        statistics.setTill1ms(till1ms);
        statistics.setTill2ms((((double) below2ms / (double) count) * 100) + till1ms);
        statistics.setRequestPerSecond((double) count / ((double) value / 1000));

        return statistics;
    }

    public static Map<String, Long> getExecutionTimeMap() {
        return executionTimeMap;
    }

    public static void setExecutionTimeMap(Map<String, Long> executionTimeMap) {
        BenchmarkStatistics.executionTimeMap = executionTimeMap;
    }

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
        sb.append("requestPerSecond=").append(getRequestPerSecond());
        sb.append(", average=").append(getAverage());
        sb.append(", till1ms=").append(getTill1ms());
        sb.append(", till2ms=").append(getTill1ms());
        sb.append('}');
        return sb.toString();
    }
}
