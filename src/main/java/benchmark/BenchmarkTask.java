package benchmark;

public class BenchmarkTask {
    private MethodType methodType = MethodType.PUT;
    private int clientPoolSize = 1;
    private int threadPoolSize = 1;
    private int mapSize = 100000;
    private int itemSizeInKB = 3;
    private String mapName = "test-map";
    private int operationCount = 1;
    private int executionTimeInMinutes = 1;

    public MethodType getMethodType() {
        return methodType;
    }

    public void setMethodType(MethodType methodType) {
        this.methodType = methodType;
    }

    public int getClientPoolSize() {
        return clientPoolSize;
    }

    public void setClientPoolSize(int clientPoolSize) {
        this.clientPoolSize = clientPoolSize;
    }

    public int getThreadPoolSize() {
        return threadPoolSize;
    }

    public void setThreadPoolSize(int threadPoolSize) {
        this.threadPoolSize = threadPoolSize;
    }

    public int getMapSize() {
        return mapSize;
    }

    public void setMapSize(int mapSize) {
        this.mapSize = mapSize;
    }

    public int getItemSizeInKB() {
        return itemSizeInKB;
    }

    public void setItemSizeInKB(int itemSizeInKB) {
        this.itemSizeInKB = itemSizeInKB;
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public int getOperationCount() {
        return operationCount;
    }

    public void setOperationCount(int operationCount) {
        this.operationCount = operationCount;
    }

    public int getExecutionTimeInMinutes() {
        return executionTimeInMinutes;
    }

    public void setExecutionTimeInMinutes(int executionTimeInMinutes) {
        this.executionTimeInMinutes = executionTimeInMinutes;
    }
}
