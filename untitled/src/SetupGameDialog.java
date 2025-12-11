import javax.swing.*;
import java.awt.*;

public class SetupGameDialog extends JDialog {
    private final JComboBox<String> whiteCombo = new JComboBox<>(new String[]{"Human", "Computer"});
    private final JComboBox<String> blackCombo = new JComboBox<>(new String[]{"Human", "Computer"});
    private final JSpinner depthSpinner = new JSpinner(new SpinnerNumberModel(3, 1, 10, 1));

    public SetupGameDialog(Window parent) {
        super(parent, "Setup Game", ModalityType.APPLICATION_MODAL);
        setSize(340, 220);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("White Player:"));
        panel.add(whiteCombo);
        panel.add(new JLabel("Black Player:"));
        panel.add(blackCombo);
        panel.add(new JLabel("AI Search Depth:"));
        panel.add(depthSpinner);

        JPanel buttons = new JPanel(new FlowLayout());
        JButton ok = new JButton("Start Game");
        JButton cancel = new JButton("Cancel");

        ok.addActionListener(e -> {
            String white = (String) whiteCombo.getSelectedItem();
            String black = (String) blackCombo.getSelectedItem();
            int depth = (Integer) depthSpinner.getValue();

            GameFrame.Instance.centerpanel.setPlayers(white, black, depth);
            dispose();
        });
        cancel.addActionListener(e -> dispose());

        buttons.add(ok);
        buttons.add(cancel);

        add(panel, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
    }
}