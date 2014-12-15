package dataStructures.map;

import java.util.Date;
import java.util.List;

public class ExecutionTiming {
    private String label;
    private Date startTime;
    private Date endTime;
    private long totalTimeInMilliseconds;

    public ExecutionTiming(String label, Date startTime, Date endTime) {
        this.label = label;
        this.startTime = startTime;
        this.endTime = endTime;
        this.totalTimeInMilliseconds = endTime.getTime() - startTime.getTime();
    }

    public static void printTimings(List<ExecutionTiming> executionTimingList){
        for (ExecutionTiming executionTiming : executionTimingList) {
            System.out.println(executionTiming.toString());
        }
    }

    public String getLabel() {
        return label;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public long getTotalTimeInMilliseconds() {
        return totalTimeInMilliseconds;
    }

    @Override
    public String toString() {
        return "ExecutionTiming{" +
                "label='" + getLabel() + '\'' +
                ", startTime=" + getStartTime() +
                ", endTime=" + getEndTime() +
                ", totalTimeInMilliseconds=" + getTotalTimeInMilliseconds() +
                '}';
    }
}
