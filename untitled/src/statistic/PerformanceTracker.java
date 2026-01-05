package statistic;

public class PerformanceTracker {
    private long startTime;
    private long startMemory;
    private Runtime runtime;

    public PerformanceTracker() {
        this.runtime = Runtime.getRuntime();
    }

    //bắt đầu đo hiệu suất
    public void start() {
        // Gọi garbage collector trước khi đo
        runtime.gc();

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        startMemory = runtime.totalMemory() - runtime.freeMemory();
        startTime = System.nanoTime();
    }

    //Kết thúc đo và trả về thời gian (giây)
    public double getElapsedTimeSeconds() {
        long endTime = System.nanoTime();
        return (endTime - startTime) / 1_000_000_000.0;
    }

//    Lấy bộ nhớ sử dụng (MB)
    public double getMemoryUsedMB() {
        long endMemory = runtime.totalMemory() - runtime.freeMemory();
        return (endMemory - startMemory) / (1024.0 * 1024.0);
    }

    //Lấy cả thời gian và bộ nhớ
    public PerformanceResult stop() {
        double time = getElapsedTimeSeconds();
        double memory = getMemoryUsedMB();
        return new PerformanceResult(time, memory);
    }

    /**
     * Class để lưu kết quả đo
     */
    public static class PerformanceResult {
        public final double timeSeconds;
        public final double memoryMB;

        public PerformanceResult(double timeSeconds, double memoryMB) {
            this.timeSeconds = timeSeconds;
            this.memoryMB = memoryMB;
        }

        @Override
        public String toString() {
            return String.format("Time: %.6f s, Memory: %.2f MB", timeSeconds, memoryMB);
        }
    }
}