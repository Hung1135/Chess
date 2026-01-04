package statistic;

import java.io.*;
import java.util.*;
import java.text.DecimalFormat;

public class ComparisonAnalyzer {
    private static final String MINIMAX_FILE = "minimax_log.txt";
    private static final String ALPHABETA_FILE = "alphabeta_log.txt";
    private static final String REPORT_FILE = "comparison_report.txt";
    private static final DecimalFormat df = new DecimalFormat("#.######");

    public static class Stats {
        double avgTime = 0;
        double avgMemory = 0;
        double minTime = Double.MAX_VALUE;
        double maxTime = 0;
        double minMemory = Double.MAX_VALUE;
        double maxMemory = 0;
        int count = 0;

        void addData(double time, double memory) {
            avgTime += time;
            avgMemory += memory;
            minTime = Math.min(minTime, time);
            maxTime = Math.max(maxTime, time);
            minMemory = Math.min(minMemory, memory);
            maxMemory = Math.max(maxMemory, memory);
            count++;
        }

        public void finalize() {
            if (count > 0) {
                avgTime /= count;
                avgMemory /= count;
            }
        }
    }

    /**
     * Đọc và phân tích file log
     */
    private static Stats analyzeLogFile(String filename) throws IOException {
        Stats stats = new Stats();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("Time:") && line.contains("Memory:")) {
                    // Parse dòng dạng: "INFO: Method: Minimax | Move: 1 | Depth: 3 | Time: 5.396569 seconds | Memory: 44.05 MB"
                    String[] parts = line.split("\\|");

                    double time = 0;
                    double memory = 0;

                    for (String part : parts) {
                        part = part.trim();
                        if (part.startsWith("Time:")) {
                            String timeStr = part.replace("Time:", "").replace("seconds", "").trim();
                            time = Double.parseDouble(timeStr);
                        } else if (part.startsWith("Memory:")) {
                            String memStr = part.replace("Memory:", "").replace("MB", "").trim();
                            memory = Double.parseDouble(memStr);
                        }
                    }

                    if (time > 0) {
                        stats.addData(time, memory);
                    }
                }
            }
        }

        stats.finalize();
        return stats;
    }

    /**
     * Tạo báo cáo so sánh chi tiết
     */
    public static void generateDetailedReport() {
        try {
            Stats minimaxStats = analyzeLogFile(MINIMAX_FILE);
            Stats alphabetaStats = analyzeLogFile(ALPHABETA_FILE);

            PrintWriter writer = new PrintWriter(new FileWriter(REPORT_FILE));

            writer.println("╔════════════════════════════════════════════════════════════════════╗");
            writer.println("║         MINIMAX vs ALPHA-BETA PERFORMANCE COMPARISON               ║");
            writer.println("╚════════════════════════════════════════════════════════════════════╝");
            writer.println();
            writer.println("Generated: " + new Date());
            writer.println();

            // MINIMAX STATISTICS
            writer.println("─────────────────────────────────────────────────────────────────────");
            writer.println("📊 MINIMAX ALGORITHM STATISTICS");
            writer.println("─────────────────────────────────────────────────────────────────────");
            writer.println("Total Moves Analyzed: " + minimaxStats.count);
            writer.println();
            writer.println("⏱️  TIME PERFORMANCE:");
            writer.println("   • Average Time:    " + df.format(minimaxStats.avgTime) + " seconds");
            writer.println("   • Minimum Time:    " + df.format(minimaxStats.minTime) + " seconds");
            writer.println("   • Maximum Time:    " + df.format(minimaxStats.maxTime) + " seconds");
            writer.println();
            writer.println("💾 MEMORY USAGE:");
            writer.println("   • Average Memory:  " + df.format(minimaxStats.avgMemory) + " MB");
            writer.println("   • Minimum Memory:  " + df.format(minimaxStats.minMemory) + " MB");
            writer.println("   • Maximum Memory:  " + df.format(minimaxStats.maxMemory) + " MB");
            writer.println();

            // ALPHA-BETA STATISTICS
            writer.println("─────────────────────────────────────────────────────────────────────");
            writer.println("📊 ALPHA-BETA PRUNING STATISTICS");
            writer.println("─────────────────────────────────────────────────────────────────────");
            writer.println("Total Moves Analyzed: " + alphabetaStats.count);
            writer.println();
            writer.println("⏱️  TIME PERFORMANCE:");
            writer.println("   • Average Time:    " + df.format(alphabetaStats.avgTime) + " seconds");
            writer.println("   • Minimum Time:    " + df.format(alphabetaStats.minTime) + " seconds");
            writer.println("   • Maximum Time:    " + df.format(alphabetaStats.maxTime) + " seconds");
            writer.println();
            writer.println("💾 MEMORY USAGE:");
            writer.println("   • Average Memory:  " + df.format(alphabetaStats.avgMemory) + " MB");
            writer.println("   • Minimum Memory:  " + df.format(alphabetaStats.minMemory) + " MB");
            writer.println("   • Maximum Memory:  " + df.format(alphabetaStats.maxMemory) + " MB");
            writer.println();

            // COMPARISON
            writer.println("═════════════════════════════════════════════════════════════════════");
            writer.println("🔍 COMPARATIVE ANALYSIS");
            writer.println("═════════════════════════════════════════════════════════════════════");

            if (minimaxStats.count > 0 && alphabetaStats.count > 0) {
                double timeImprovement = ((minimaxStats.avgTime - alphabetaStats.avgTime) / minimaxStats.avgTime) * 100;
                double memoryImprovement = ((minimaxStats.avgMemory - alphabetaStats.avgMemory) / minimaxStats.avgMemory) * 100;
                double speedup = minimaxStats.avgTime / alphabetaStats.avgTime;

                writer.println("⚡ SPEED IMPROVEMENT:");
                writer.println("   • Alpha-Beta is " + df.format(speedup) + "x FASTER than Minimax");
                writer.println("   • Time saved: " + df.format(timeImprovement) + "%");
                writer.println();

                writer.println("💾 MEMORY EFFICIENCY:");
                if (memoryImprovement > 0) {
                    writer.println("   • Alpha-Beta uses " + df.format(memoryImprovement) + "% LESS memory");
                } else {
                    writer.println("   • Alpha-Beta uses " + df.format(Math.abs(memoryImprovement)) + "% MORE memory");
                }
                writer.println();

                writer.println("📈 PERFORMANCE SUMMARY:");
                writer.println("   • Minimax Average:     " + df.format(minimaxStats.avgTime) + " seconds");
                writer.println("   • Alpha-Beta Average:  " + df.format(alphabetaStats.avgTime) + " seconds");
                writer.println("   • Difference:          " + df.format(minimaxStats.avgTime - alphabetaStats.avgTime) + " seconds");
                writer.println();

                writer.println("🏆 CONCLUSION:");
                if (timeImprovement > 50) {
                    writer.println("   Alpha-Beta Pruning shows EXCELLENT performance improvement!");
                    writer.println("   Recommended for production use.");
                } else if (timeImprovement > 20) {
                    writer.println("   Alpha-Beta Pruning shows GOOD performance improvement.");
                } else {
                    writer.println("   Alpha-Beta Pruning shows MODERATE improvement.");
                }
            } else {
                writer.println("⚠️  Insufficient data for comparison.");
                writer.println("   Please run more games with both algorithms.");
            }

            writer.println();
            writer.println("═════════════════════════════════════════════════════════════════════");
            writer.println("📁 Detailed logs available in:");
            writer.println("   • " + MINIMAX_FILE);
            writer.println("   • " + ALPHABETA_FILE);
            writer.println("═════════════════════════════════════════════════════════════════════");

            writer.close();

            System.out.println("✅ Báo cáo so sánh đã được tạo: " + REPORT_FILE);

        } catch (IOException e) {
            System.err.println("❌ Lỗi khi tạo báo cáo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Main method để test
     */
    public static void main(String[] args) {
        generateDetailedReport();
    }
}