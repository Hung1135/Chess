// CellPanel.java (đã sửa typo và giữ nguyên đầy đủ)
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class CellPanel extends JPanel {
    private final Color blue = Color.BLUE;
    private final Color red = Color.RED;
    private final Color green = Color.GREEN;
    private final Color orange = new Color(255, 165, 0); // Màu cam cho vua bị chiếu

    public boolean isValidMove;
    public int x;
    public int y;

    public JLabel imageLabel; // là mỗi piece

    public ChessPiece currentChessPiece;

    public PieceColor originColor;

    public CellPanel(boolean isWhite, int x, int y) {
        this.x = x;
        this.y = y;
        isValidMove = false;
        originColor = (isWhite ? PieceColor.WHITE : PieceColor.BLACK);
        this.setBackground(isWhite ? Color.WHITE : Color.GRAY);
        imageLabel = new JLabel();
        imageLabel.setVerticalAlignment(JLabel.CENTER);
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        this.add(imageLabel);
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                GameFrame.Instance.centerpanel.onclickCellPanel(x, y);
            }
        });
    }

    public void addImage(ChessPiece piece) {
        currentChessPiece = piece;
        BufferedImage pieceImage = getBufferedImageFromFile(piece);
        Image image = pieceImage.getScaledInstance(60, 60, Image.SCALE_SMOOTH);
        imageLabel.setIcon(new ImageIcon(image));
        imageLabel.setVisible(true);
    }

    public void removePiece() {
        currentChessPiece = null;
        imageLabel.setVisible(false);
    }

    public void select() {
        this.setBackground(blue);
    }

    public void deselect() {
        this.setBackground(originColor == PieceColor.WHITE ? Color.WHITE : Color.GRAY);
        isValidMove = false;
    }

    private BufferedImage getBufferedImageFromFile(ChessPiece piece) {
        Path path = FileSystems.getDefault().getPath("").toAbsolutePath();
        String fileStr = path + "/piece/";
        if (piece.color == PieceColor.WHITE) {
            fileStr += "W_";
        } else {
            fileStr += "B_";
        }
        fileStr += piece.type.toString() + ".png";
        File file = new File(fileStr);
        try {
            return ImageIO.read(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setColor(boolean isMove) {
        isValidMove = true;
        if (isMove) {
            setBackground(green);
        } else {
            setBackground(red);
        }
    }

    public void setCheckColor() {
        this.setBackground(orange);
    }
}