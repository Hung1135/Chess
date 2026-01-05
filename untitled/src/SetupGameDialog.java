import javax.swing.*;
import java.awt.*;

public class SetupGameDialog extends JDialog {
    private final JComboBox<String> blackCombo = new JComboBox<>(new String[]{"Computer", "Human"});
    private final JComboBox<String> whiteCombo = new JComboBox<>(new String[]{"Human", "Computer"});
    private final JSpinner depthSpinner = new JSpinner(new SpinnerNumberModel(3, 1, 6, 1));
    private final JComboBox<String> algorithmCombo = new JComboBox<>(new String[]{"AlphaBeta", "Minimax"});

    public SetupGameDialog(Window parent) {
        super(parent, "Setup Game", ModalityType.APPLICATION_MODAL);
        setSize(400, 280);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // Panel chính
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // White Player
        panel.add(new JLabel("White Player:"));
        panel.add(whiteCombo);

        // Black Player
        panel.add(new JLabel("Black Player:"));
        panel.add(blackCombo);

        // AI Depth
        panel.add(new JLabel("AI Search Depth:"));
        panel.add(depthSpinner);

        // Algorithm Selection
        panel.add(new JLabel("AI Algorithm:"));
        panel.add(algorithmCombo);

        // Tooltip
//        JLabel infoLabel = new JLabel("<html><i>Minimax: Chậm hơn nhưng dễ hiểu<br>AlphaBeta: Nhanh hơn, tối ưu</i></html>");
//        infoLabel.setFont(new Font("Arial", Font.ITALIC, 11));
//        panel.add(new JLabel());
//        panel.add(infoLabel);

        // Buttons
        JPanel buttons = new JPanel(new FlowLayout());
        JButton ok = new JButton("Start Game");
        JButton cancel = new JButton("Cancel");

        ok.addActionListener(e -> {
            String white = (String) whiteCombo.getSelectedItem();
            String black = (String) blackCombo.getSelectedItem();
            int depth = (Integer) depthSpinner.getValue();
            String algorithm = (String) algorithmCombo.getSelectedItem();

            GameFrame.Instance.centerpanel.setPlayers(white, black, depth, algorithm);
            dispose();
        });

        cancel.addActionListener(e -> dispose());

        buttons.add(ok);
        buttons.add(cancel);

        add(panel, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
    }
}