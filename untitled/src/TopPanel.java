import javax.swing.*;
import java.awt.*;
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
                        "Đã xóa tất cả log!\n\nLog mới sẽ được ghi vào:\n• minimax_log.txt\n• alphabeta_log.txt",
                        "Logs Cleared",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        });

        menu.add(setupItem);
        menu.addSeparator();
        menu.add(clearLogsItem);

        menuBar.add(menu);
        add(menuBar);
    }
}