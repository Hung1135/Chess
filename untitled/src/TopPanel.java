import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TopPanel extends JPanel {

    public TopPanel() {
        JButton  button = new JButton();
        button.setText("Menu");
        button.setPreferredSize(new Dimension(100,30));
        //chưa làm menu
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("menu");
            }
        });
        this.add(button);


 }
}
