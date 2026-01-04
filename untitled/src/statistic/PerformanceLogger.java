package statistic;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;
import java.util.logging.Level;

public class PerformanceLogger {
    private static final Logger logger = Logger.getLogger(PerformanceLogger.class.getName());
    private static final String MINIMAX_FILE = "minimax_log.txt";
    private static final String ALPHABETA_FILE = "alphabeta_log.txt";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a");

    /**
     * Log hiệu suất của thuật toán
     * @param algorithmType "Minimax" hoặc "AlphaBeta"
     * @param timeSeconds thời gian thực thi (giây)
     * @param memoryMB bộ nhớ sử dụng (MB)
     * @param depth độ sâu tìm kiếm
     * @param moveNumber số nước đi
     */
    public static void logPerformance(String algorithmType, double timeSeconds, double memoryMB, int depth, int moveNumber) {
        String fileName = algorithmType.equalsIgnoreCase("Minimax") ? MINIMAX_FILE : ALPHABETA_FILE;
        String timestamp = dateFormat.format(new Date());

        // Log ra console
        String consoleMessage = String.format(
                "Method: %s | Move: %d | Depth: %d | Time: %.6f seconds | Memory: %.2f MB",
                algorithmType, moveNumber, depth, timeSeconds, memoryMB
        );
        logger.log(Level.INFO, consoleMessage);

        // Ghi vào file
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName, true))) {
            writer.println(timestamp + " statistic.PerformanceLogger logPerformance");
            writer.println("INFO: Method: " + algorithmType + " | Move: " + moveNumber +
                    " | Depth: " + depth +
                    " | Time: " + String.format("%.6f", timeSeconds) + " seconds" +
                    " | Memory: " + String.format("%.2f", memoryMB) + " MB");
            writer.println();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Không thể ghi log vào file: " + fileName, e);
        }
    }

    /**
     * Xóa nội dung file log cũ để bắt đầu session mới
     */
    public static void clearLogs() {
        clearLog(MINIMAX_FILE);
        clearLog(ALPHABETA_FILE);
    }

    private static void clearLog(String fileName) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName, false))) {
            writer.println("=== Performance Log Started ===");
            writer.println("Timestamp: " + dateFormat.format(new Date()));
            writer.println("================================");
            writer.println();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Không thể xóa file log: " + fileName, e);
        }
    }

    /**
     * Tạo báo cáo tổng hợp so sánh
     */
    public static void generateComparisonReport() {
        String reportFile = "comparison_report.txt";

        try (PrintWriter writer = new PrintWriter(new FileWriter(reportFile, false))) {
            writer.println("╔════════════════════════════════════════════════════════════════╗");
            writer.println("║          MINIMAX vs ALPHA-BETA COMPARISON REPORT               ║");
            writer.println("╚════════════════════════════════════════════════════════════════╝");
            writer.println();
            writer.println("Report Generated: " + dateFormat.format(new Date()));
            writer.println();
            writer.println("────────────────────────────────────────────────────────────────");
            writer.println("Xem chi tiết:");
            writer.println("  • Minimax logs: " + MINIMAX_FILE);
            writer.println("  • Alpha-Beta logs: " + ALPHABETA_FILE);
            writer.println("────────────────────────────────────────────────────────────────");

            logger.info("Báo cáo so sánh đã được tạo: " + reportFile);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Không thể tạo báo cáo so sánh", e);
        }
    }
}