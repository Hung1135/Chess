import javax.swing.*;
import java.awt.*;
import java.lang.classfile.constantpool.PackageEntry;

public class CenterPanel extends JPanel {
    private CellPanel[][] boardCell = new CellPanel[8][8];
    private BoardState boardState;
    private CellPanel selectedCell;
    //site
    public CenterPanel() {
        boardState = BoardState.NO_SELECT;
        this.setLayout(new GridLayout(8,8));
        boolean isWhite = true;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                CellPanel  cellPanel = new CellPanel(isWhite, i, j);
                if(i == 1 || i==6){
                    cellPanel.addImage( new ChessPiece( i == 1 ?PieceColor.BLACK: PieceColor.WHITE, PieceType.PAWN ));

                }if(i == 0 && (j==0 || j==7)){
                    cellPanel.addImage( new ChessPiece( PieceColor.BLACK, PieceType.ROOK ));
                }if(i == 7 && (j==0 || j==7)){
                    cellPanel.addImage( new ChessPiece( PieceColor.WHITE, PieceType.ROOK ));
                }
                if(i == 0 && (j==1 || j==6)){
                    cellPanel.addImage( new ChessPiece( PieceColor.BLACK, PieceType.KNIGHT ));
                }if(i == 7 && (j==1 || j==6)){
                    cellPanel.addImage( new ChessPiece( PieceColor.WHITE, PieceType.KNIGHT ));
                }
                if(i == 0 && (j==2 || j==5)){
                    cellPanel.addImage( new ChessPiece( PieceColor.BLACK, PieceType.BISHOP ));
                }if(i == 7 && (j==2 || j==5)){
                    cellPanel.addImage( new ChessPiece( PieceColor.WHITE, PieceType.BISHOP ));
                }
                if(i == 0 && (j==4)){
                    cellPanel.addImage( new ChessPiece( PieceColor.BLACK, PieceType.KING ));
                }if(i == 7 && (j==4)){
                    cellPanel.addImage( new ChessPiece( PieceColor.WHITE, PieceType.KING ));
                }
                if(i == 0 && (j==3)){
                    cellPanel.addImage( new ChessPiece( PieceColor.BLACK, PieceType.QUEEN ));
                }if(i == 7 && (j==3)){
                    cellPanel.addImage( new ChessPiece( PieceColor.WHITE, PieceType.QUEEN ));
                }


                this.add(cellPanel);
                boardCell[i][j] = cellPanel;
                isWhite = !isWhite;
            }
            isWhite = !isWhite;
        }
        selectedCell = null;
    }


    public void onclickCellPanel(int x, int y) {
//        System.out.println(x  +" "+y);

        CellPanel clickedCellPannel = boardCell[x][y];
       // System.out.println(clickedCellPannel.currnetChessPiece.type + ""+ clickedCellPannel.currnetChessPiece.color);

        clickedCellPannel.select();

        if (boardState == BoardState.NO_SELECT) {
            deSelectCellPanelAll();
            System.out.println(clickedCellPannel.currnetChessPiece);
            //neu kh co check luc co piece
            if (clickedCellPannel.currnetChessPiece != null) {
                switch (clickedCellPannel.currnetChessPiece.type){
                    case PAWN:
                        PawnCheck(x,y);
                        break;
                    case KING:
                        break;


                }
                selectedCell = clickedCellPannel;
                boardState = BoardState.PIECE_SELECT;

            }else{//neu kh co piece

            }
        }else if (boardState  == BoardState.PIECE_SELECT){//neu no dc select thi sao
            System.out.println(BoardState.PIECE_SELECT);
            if (boardCell[x][y].isValidMove) {
                //thi move
                clickedCellPannel.addImage(selectedCell.currnetChessPiece);
                selectedCell.removePiece();
                selectedCell = null;
                //va chuyen trang thai
                boardState = BoardState.NO_SELECT;
                deSelectCellPanelAll();
            }else{
                deSelectCellPanelAll();//thi bo select
            }
        }
    }


    public void deSelectCellPanelAll() {
       for (int i = 0; i < 8; i++) {
           for (int j = 0; j < 8; j++) {
               boardCell[i][j].deselect();
           }
       }
    }


    private void PawnCheck(int x, int y) {
        ChessPiece thisPiece = boardCell[x][y].currnetChessPiece;
        //System.out.println(thisPiece);
        if (thisPiece.color == PieceColor.WHITE) {
            int maxStep = (x==6? 2:1);
            for (int i = x-1; i >= x-maxStep; i--) {
                if(!checkValidMove(i,y)) break;
                ChessPiece chessPiece = boardCell[i][y].currnetChessPiece;
                if (chessPiece !=null) { //bị chặn đầu
                    break;
                }else{
                    boardCell[i][y].setColor(true);
                }
            }
            //ăn chéo trái
            if (checkValidMove(x-1,y-1)) {
                CellPanel cellPanel = boardCell[x-1][y-1];
                if (cellPanel.currnetChessPiece !=null) {
                    if (cellPanel.currnetChessPiece.color != thisPiece.color) {
                        cellPanel.setColor(false);
                    }
                }
            }
            //ăn chéo phải
            if (checkValidMove(x-1,y+1)) {
                CellPanel cellPanel = boardCell[x-1][y+1];
                if (cellPanel.currnetChessPiece !=null) {
                    if (cellPanel.currnetChessPiece.color != thisPiece.color) {
                        cellPanel.setColor(false);
                    }
                }
            }
        }else{ //con đen
            int maxStep = (x==1? 2:1);
            for (int i = x+1; i <= x+maxStep; i++) {
                if(!checkValidMove(i,y)) break;
                ChessPiece chessPiece = boardCell[i][y].currnetChessPiece;
                if (chessPiece !=null) { //bị chặn đầu
                    break;
                }else{
                    boardCell[i][y].setColor(true);
                }
            }
            //ăn chéo trái
            if (checkValidMove(x+1,y-1)) {
                CellPanel cellPanel = boardCell[x+1][y-1];
                if (cellPanel.currnetChessPiece !=null) {
                    if (cellPanel.currnetChessPiece.color != thisPiece.color) {
                        cellPanel.setColor(false);
                    }
                }
            }
            //ăn chéo phải
            if (checkValidMove(x+1,y+1)) {
                CellPanel cellPanel = boardCell[x+1][y+1];
                if (cellPanel.currnetChessPiece !=null) {
                    if (cellPanel.currnetChessPiece.color != thisPiece.color) {
                        cellPanel.setColor(false);
                    }
                }
            }
        }
    }


    //check toa do hieu luc trong ban
    public boolean checkValidMove(int n) {
        return (n >=0 && n <= 7);
    }
    public boolean checkValidMove(int x, int y) {
        return checkValidMove(x) && checkValidMove(y);
    }
}
