import javax.swing.*;
import java.awt.*;
import statistic.ComparisonAnalyzer;
import statistic.PerformanceLogger;

public class TopPanel extends JPanel {

    public TopPanel() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Menu");

        // Setup Game
        JMenuItem setupItem = new JMenuItem("Setup Game");
        setupItem.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(this);
            SetupGameDialog dialog = new SetupGameDialog(window);
            dialog.setLocationRelativeTo(window);
            dialog.setVisible(true);
        });

        // Generate Report
        JMenuItem reportItem = new JMenuItem("Generate Performance Report");
        reportItem.addActionListener(e -> {
            ComparisonAnalyzer.generateDetailedReport();
            JOptionPane.showMessageDialog(
                    this,
                    "Báo cáo đã được tạo!\n\n" +
                            "Xem file:\n" +
                            "• comparison_report.txt - Báo cáo tổng hợp\n" +
                            "• minimax_log.txt - Log Minimax\n" +
                            "• alphabeta_log.txt - Log Alpha-Beta",
                    "Report Generated",
                    JOptionPane.INFORMATION_MESSAGE
            );
        });

        // Clear Logs
        JMenuItem clearLogsItem = new JMenuItem("Clear Performance Logs");
        clearLogsItem.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Xóa tất cả log hiệu suất?",
                    "Confirm Clear",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                PerformanceLogger.clearLogs();
                JOptionPane.showMessageDialog(
                        this,
                        "Đã xóa tất cả log!",
                        "Logs Cleared",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        });

        // About
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                    this,
                    "Chess Game with AI Comparison\n\n" +
                            "Thuật toán:\n" +
                            "• Minimax - Brute force search\n" +
                            "• Alpha-Beta Pruning - Optimized search\n\n" +
                            "Performance logs tự động được ghi vào:\n" +
                            "• minimax_log.txt\n" +
                            "• alphabeta_log.txt",
                    "About",
                    JOptionPane.INFORMATION_MESSAGE
            );
        });

        menu.add(setupItem);
        menu.addSeparator();
        menu.add(reportItem);
        menu.add(clearLogsItem);
        menu.addSeparator();
        menu.add(aboutItem);

        menuBar.add(menu);
        add(menuBar);
    }
}