import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameFrame extends JFrame {
    public static GameFrame Instance;

    public TopPanel toppanel;
    public CenterPanel centerpanel;


    public GameFrame() {
        Instance = this;
        this.setTitle("Chess");
        this.setSize(650, 700);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocation(200, 100);
        this.setVisible(true);

        //menu
        toppanel = new TopPanel();
        //bàn cờ
        centerpanel = new CenterPanel();

        this.getContentPane().add(toppanel, BorderLayout.NORTH);
        this.getContentPane().add(centerpanel, BorderLayout.CENTER);
        this.revalidate();
        this.repaint();
    }
}
