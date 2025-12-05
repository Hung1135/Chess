import javax.swing.*;
import java.awt.*;
import java.lang.classfile.constantpool.PackageEntry;

public class CenterPanel extends JPanel {
    private CellPanel[][] boardCell = new CellPanel[8][8];
    private BoardState boardState;
    private CellPanel selectedCell;
    private PieceColor currentTurn = PieceColor.WHITE;
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

        CellPanel clickedCellPannel = boardCell[x][y];
        ChessPiece piece = clickedCellPannel.currnetChessPiece;

        if (boardState == BoardState.NO_SELECT) {
            deSelectCellPanelAll();
            System.out.println(piece);
            if (piece == null) {
                return;
            }
            if (piece.color != currentTurn) {
                System.out.println("Chưa tới lượt bên " + piece.color);
                return;
            }
            clickedCellPannel.select(); // tô xanh ô đang chọn

            switch (piece.type) {
                case PAWN:
                    PawnCheck(x, y);
                    break;
                case KNIGHT:
                    KnightCheck(x, y);
                    break;
                case ROOK:
                    RookCheck(x, y);
                    break;
                case BISHOP:
                    BishopCheck(x, y);
                    break;
                case QUEEN:
                    QueenCheck(x, y);
                    break;
                case KING:
                    KingCheck(x, y);
                    break;
            }

            selectedCell = clickedCellPannel;
            boardState = BoardState.PIECE_SELECT;

        } else if (boardState == BoardState.PIECE_SELECT) {
            System.out.println(BoardState.PIECE_SELECT);
            if (boardCell[x][y].isValidMove) {

                int toX = x;
                int toY = y;

                // Quân đang di chuyển
                ChessPiece movingPiece = selectedCell.currnetChessPiece;

                // 👉 GỌI HÀM PHONG HẬU (nếu cần)
                ChessPiece pieceAfterMove = handlePawnPromotionIfNeeded(movingPiece, toX);

                // Nếu phong hậu thì pieceAfterMove sẽ là HẬU mới
                // Nếu không phong thì pieceAfterMove = movingPiece ban đầu

                // Đặt quân (tốt hoặc hậu) lên ô đích
                clickedCellPannel.addImage(pieceAfterMove);

                // Xóa quân ở ô cũ
                selectedCell.removePiece();
                selectedCell = null;

                // Chuyển trạng thái
                boardState = BoardState.NO_SELECT;
                deSelectCellPanelAll();

                // Nếu bạn có đổi lượt thì giữ lại:
                currentTurn = (currentTurn == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;
                System.out.println("Tới lượt: " + currentTurn);

            } else {
                deSelectCellPanelAll();//thi bo select
                selectedCell = null;
                boardState = BoardState.NO_SELECT;
            }
        }
    }

        private void QueenCheck(int x, int y) {
        BishopCheck(x, y);
        RookCheck(x, y);
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
    private void KnightCheck(int x, int y) {
        ChessPiece thisPiece = boardCell[x][y].currnetChessPiece;
        if (thisPiece == null) return;

        // 8 hướng đi của quân Mã: (±2, ±1) và (±1, ±2)
        int[][] knightMoves = {
                { 2,  1},
                { 2, -1},
                {-2,  1},
                {-2, -1},
                { 1,  2},
                { 1, -2},
                {-1,  2},
                {-1, -2}
        };

        for (int i = 0; i < knightMoves.length; i++) {
            int newX = x + knightMoves[i][0];
            int newY = y + knightMoves[i][1];

            // 1) Kiểm tra ô mới có nằm trong bàn cờ không
            if (!checkValidMove(newX, newY)) continue;

            CellPanel targetCell = boardCell[newX][newY];
            ChessPiece targetPiece = targetCell.currnetChessPiece;

            // 2) Nếu ô trống -> là nước đi hợp lệ (màu xanh)
            if (targetPiece == null) {
                targetCell.setColor(true); // true = ô di chuyển (blue)
            } else {
                // 3) Nếu có quân đối phương -> có thể ăn (màu đỏ)
                if (targetPiece.color != thisPiece.color) {
                    targetCell.setColor(false); // false = ô ăn (red)
                }
                // Nếu là quân cùng màu -> không làm gì (không được đi/ăn)
            }
        }
    }
    private void RookCheck(int x, int y) {
        ChessPiece thisPiece = boardCell[x][y].currnetChessPiece;
        if (thisPiece == null) return;

        // Xe đi 4 hướng: lên, xuống, trái, phải
        // Mỗi hướng là 1 cặp (dx, dy)
        int[][] directions = {
                {-1, 0}, // lên
                { 1, 0}, // xuống
                { 0,-1}, // trái
                { 0, 1}  // phải
        };

        // Duyệt từng hướng một
        for (int d = 0; d < directions.length; d++) {
            int dx = directions[d][0];
            int dy = directions[d][1];

            // Đi từng bước 1 ô mỗi lần trong hướng đó
            for (int step = 1; step < 8; step++) {
                int newX = x + dx * step;
                int newY = y + dy * step;

                // 1) Nếu ra ngoài bàn thì dừng lại ở hướng này
                if (!checkValidMove(newX, newY)) {
                    break;
                }

                CellPanel targetCell = boardCell[newX][newY];
                ChessPiece targetPiece = targetCell.currnetChessPiece;

                // 2) Nếu ô trống -> xe có thể đi tiếp qua ô này,
                //    và có thể tiếp tục đi xa hơn cùng hướng
                if (targetPiece == null) {
                    targetCell.setColor(true); // ô đi thường (xanh)
                } else {
                    // 3) Nếu là quân khác màu -> đây là ô ăn được
                    if (targetPiece.color != thisPiece.color) {
                        targetCell.setColor(false); // ô ăn (đỏ)
                    }
                    // 4) Dù cùng màu hay khác màu -> bị chặn, không đi xa hơn được
                    break;
                }
            }
        }
    }
    private void BishopCheck(int x, int y) {
        ChessPiece thisPiece = boardCell[x][y].currnetChessPiece;
        if (thisPiece == null) return;

        // Tượng đi 4 hướng chéo:
        // lên-trái, lên-phải, xuống-trái, xuống-phải
        int[][] directions = {
                {-1, -1}, // lên - trái
                {-1,  1}, // lên - phải
                { 1, -1}, // xuống - trái
                { 1,  1}  // xuống - phải
        };

        // Duyệt từng hướng chéo
        for (int d = 0; d < directions.length; d++) {
            int dx = directions[d][0];
            int dy = directions[d][1];

            // Đi từng bước 1 ô, tối đa 7 ô
            for (int step = 1; step < 8; step++) {
                int newX = x + dx * step;
                int newY = y + dy * step;

                // 1) Ra khỏi bàn -> dừng hướng này
                if (!checkValidMove(newX, newY)) {
                    break;
                }

                CellPanel targetCell = boardCell[newX][newY];
                ChessPiece targetPiece = targetCell.currnetChessPiece;

                if (targetPiece == null) {
                    // 2) Ô trống -> đi được, tô xanh, tiếp tục xa hơn
                    targetCell.setColor(true); // true = move thường (blue)
                } else {
                    // 3) Có quân -> nếu là quân địch thì ăn được
                    if (targetPiece.color != thisPiece.color) {
                        targetCell.setColor(false); // false = ô ăn (red)
                    }
                    // 4) Bị chặn, dù cùng màu hay khác màu -> không đi xa hơn được
                    break;
                }
            }
        }
    }
    private void KingCheck(int x, int y) {
        ChessPiece thisPiece = boardCell[x][y].currnetChessPiece;
        if (thisPiece == null) return;

        // 8 hướng xung quanh vua
        int[][] kingMoves = {
                {-1, -1}, // lên trái
                {-1, 0}, // lên
                {-1, 1}, // lên phải
                {0, -1}, // trái
                {0, 1}, // phải
                {1, -1}, // xuống trái
                {1, 0}, // xuống
                {1, 1}  // xuống phải
        };

        for (int i = 0; i < kingMoves.length; i++) {
            int newX = x + kingMoves[i][0];
            int newY = y + kingMoves[i][1];

            // kiểm tra còn trong bàn không
            if (!checkValidMove(newX, newY)) continue;

            CellPanel targetCell = boardCell[newX][newY];
            ChessPiece targetPiece = targetCell.currnetChessPiece;

            if (targetPiece == null) {
                // ô trống -> đi thường (màu xanh)
                targetCell.setColor(true);
            } else {
                // có quân -> chỉ được ăn quân khác màu
                if (targetPiece.color != thisPiece.color) {
                    targetCell.setColor(false); // ô ăn (màu đỏ)
                }
            }
        }


    }
    private ChessPiece handlePawnPromotionIfNeeded(ChessPiece piece, int toX) {
        if (piece == null) return null;

        // Chỉ quan tâm đến quân TỐT
        if (piece.type == PieceType.PAWN) {
            // Tốt TRẮNG đi lên trên, phong khi chạm hàng 0
            if (piece.color == PieceColor.WHITE && toX == 0) {
                return new ChessPiece(PieceColor.WHITE, PieceType.QUEEN);
            }
            // Tốt ĐEN đi xuống dưới, phong khi chạm hàng 7
            if (piece.color == PieceColor.BLACK && toX == 7) {
                return new ChessPiece(PieceColor.BLACK, PieceType.QUEEN);
            }
        }

        // Không phong: giữ nguyên quân
        return piece;
    }
    public boolean checkValidMove(int n) {
        return (n >=0 && n <= 7);
    }
    public boolean checkValidMove(int x, int y) {
        return checkValidMove(x) && checkValidMove(y);
    }
}
