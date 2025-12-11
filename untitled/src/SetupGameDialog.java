import javax.swing.*;
import java.awt.*;

public class SetupGameDialog extends JDialog {
    private JComboBox<String> whitePlayerCombo;
    private JComboBox<String> blackPlayerCombo;
    private JSpinner depthSpinner;

    public SetupGameDialog(Window parent) {
        super(parent, "Setup Game", ModalityType.APPLICATION_MODAL);
        setSize(300, 200);
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        whitePlayerCombo = new JComboBox<>(new String[]{"Human", "Computer"});
        blackPlayerCombo = new JComboBox<>(new String[]{"Human", "Computer"});
        depthSpinner = new JSpinner(new SpinnerNumberModel(2, 1, 10, 1));

        formPanel.add(new JLabel("White Player:"));
        formPanel.add(whitePlayerCombo);
        formPanel.add(new JLabel("Black Player:"));
        formPanel.add(blackPlayerCombo);
        formPanel.add(new JLabel("Search Depth:"));
        formPanel.add(depthSpinner);

        JPanel buttonPanel = new JPanel();
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");

        okButton.addActionListener(e -> {
            System.out.println("===== GAME SETUP =====");
            System.out.println("White: " + whitePlayerCombo.getSelectedItem());
            System.out.println("Black: " + blackPlayerCombo.getSelectedItem());
            System.out.println("Depth: " + depthSpinner.getValue());
            dispose();
        });

        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}