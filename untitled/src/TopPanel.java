import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class TopPanel extends JPanel {
    public TopPanel() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Menu");

        JMenuItem setupItem = new JMenuItem("Setup Game");
        setupItem.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(this);
            SetupGameDialog dialog = new SetupGameDialog(window);
            dialog.setLocationRelativeTo(window);
            dialog.setVisible(true);
        });

        menu.add(setupItem);
        menuBar.add(menu);
        add(menuBar);
    }
}