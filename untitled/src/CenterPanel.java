import javax.swing.*;
import java.awt.*;
import java.lang.classfile.constantpool.PackageEntry;

public class CenterPanel extends JPanel {
    private CellPanel[][] boardCell = new CellPanel[8][8];
    private BoardState boardState;
    private CellPanel selectedCell;
    private PieceColor currentTurn = PieceColor.WHITE;

    // En Passant: Lưu vị trí tốt vừa nhảy 2 ô ở nước đi trước
    private int[] lastPawnDoubleMove = null; // [x, y] của tốt vừa nhảy 2 ô

    // site
    public CenterPanel() {
        boardState = BoardState.NO_SELECT;
        this.setLayout(new GridLayout(8, 8));
        boolean isWhite = true;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                CellPanel cellPanel = new CellPanel(isWhite, i, j);
                if (i == 1 || i == 6) {
                    cellPanel.addImage(new ChessPiece(i == 1 ? PieceColor.BLACK : PieceColor.WHITE, PieceType.PAWN));

                }
                if (i == 0 && (j == 0 || j == 7)) {
                    cellPanel.addImage(new ChessPiece(PieceColor.BLACK, PieceType.ROOK));
                }
                if (i == 7 && (j == 0 || j == 7)) {
                    cellPanel.addImage(new ChessPiece(PieceColor.WHITE, PieceType.ROOK));
                }
                if (i == 0 && (j == 1 || j == 6)) {
                    cellPanel.addImage(new ChessPiece(PieceColor.BLACK, PieceType.KNIGHT));
                }
                if (i == 7 && (j == 1 || j == 6)) {
                    cellPanel.addImage(new ChessPiece(PieceColor.WHITE, PieceType.KNIGHT));
                }
                if (i == 0 && (j == 2 || j == 5)) {
                    cellPanel.addImage(new ChessPiece(PieceColor.BLACK, PieceType.BISHOP));
                }
                if (i == 7 && (j == 2 || j == 5)) {
                    cellPanel.addImage(new ChessPiece(PieceColor.WHITE, PieceType.BISHOP));
                }
                if (i == 0 && (j == 4)) {
                    cellPanel.addImage(new ChessPiece(PieceColor.BLACK, PieceType.KING));
                }
                if (i == 7 && (j == 4)) {
                    cellPanel.addImage(new ChessPiece(PieceColor.WHITE, PieceType.KING));
                }
                if (i == 0 && (j == 3)) {
                    cellPanel.addImage(new ChessPiece(PieceColor.BLACK, PieceType.QUEEN));
                }
                if (i == 7 && (j == 3)) {
                    cellPanel.addImage(new ChessPiece(PieceColor.WHITE, PieceType.QUEEN));
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
            // Highlight vua nếu đang bị chiếu
            highlightKingInCheck();
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

                int fromX = selectedCell.x;
                int fromY = selectedCell.y;
                int toX = x;
                int toY = y;
                ChessPiece movingPiece = selectedCell.currnetChessPiece;

                // Xử lý En Passant: Xóa tốt bị bắt qua đường
                if (movingPiece.type == PieceType.PAWN && lastPawnDoubleMove != null) {
                    // Kiểm tra nếu đi chuyển chéo đến ô trống (en passant)
                    if (Math.abs(toY - fromY) == 1 && boardCell[toX][toY].currnetChessPiece == null) {
                        // Kiểm tra nếu đang bắt tốt qua đường
                        if (lastPawnDoubleMove[1] == toY) {
                            if ((movingPiece.color == PieceColor.WHITE && lastPawnDoubleMove[0] == fromX
                                    && toX == fromX - 1) ||
                                    (movingPiece.color == PieceColor.BLACK && lastPawnDoubleMove[0] == fromX
                                            && toX == fromX + 1)) {
                                // Xóa tốt bị bắt qua đường
                                boardCell[lastPawnDoubleMove[0]][lastPawnDoubleMove[1]].removePiece();
                            }
                        }
                    }
                }

                ChessPiece pieceAfterMove = handlePawnPromotionIfNeeded(movingPiece, toX);

                clickedCellPannel.addImage(pieceAfterMove);

                // Xóa quân ở ô cũ
                selectedCell.removePiece();

                // Cập nhật lastPawnDoubleMove nếu tốt vừa nhảy 2 ô
                if (movingPiece.type == PieceType.PAWN && Math.abs(toX - fromX) == 2) {
                    lastPawnDoubleMove = new int[] { toX, toY };
                } else {
                    // Xóa lastPawnDoubleMove nếu không phải tốt nhảy 2 ô
                    lastPawnDoubleMove = null;
                }

                selectedCell = null;

                // Chuyển trạng thái
                boardState = BoardState.NO_SELECT;
                deSelectCellPanelAll();

                // Nếu bạn có đổi lượt thì giữ lại:
                currentTurn = (currentTurn == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;
                System.out.println("Tới lượt: " + currentTurn);

                // Highlight vua nếu bị chiếu sau khi đổi lượt
                highlightKingInCheck();

            } else {
                deSelectCellPanelAll();// thi bo select
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
        if (thisPiece.color == PieceColor.WHITE) {
            int maxStep = (x == 6 ? 2 : 1);
            for (int i = x - 1; i >= x - maxStep; i--) {
                if (!checkValidMove(i, y))
                    break;
                ChessPiece chessPiece = boardCell[i][y].currnetChessPiece;
                if (chessPiece != null) { // bị chặn đầu
                    break;
                } else {
                    // Chỉ highlight nếu nước đi an toàn (không làm vua bị chiếu)
                    if (isMoveSafe(x, y, i, y)) {
                        boardCell[i][y].setColor(true);
                    }
                }
            }
            // ăn chéo trái
            if (checkValidMove(x - 1, y - 1)) {
                CellPanel cellPanel = boardCell[x - 1][y - 1];
                if (cellPanel.currnetChessPiece != null) {
                    if (cellPanel.currnetChessPiece.color != thisPiece.color) {
                        // Chỉ highlight nếu nước đi an toàn
                        if (isMoveSafe(x, y, x - 1, y - 1)) {
                            cellPanel.setColor(false);
                        }
                    }
                }
            }
            // ăn chéo phải
            if (checkValidMove(x - 1, y + 1)) {
                CellPanel cellPanel = boardCell[x - 1][y + 1];
                if (cellPanel.currnetChessPiece != null) {
                    if (cellPanel.currnetChessPiece.color != thisPiece.color) {
                        // Chỉ highlight nếu nước đi an toàn
                        if (isMoveSafe(x, y, x - 1, y + 1)) {
                            cellPanel.setColor(false);
                        }
                    }
                }
            }

            // En Passant cho quân trắng (WHITE)
            if (x == 3 && lastPawnDoubleMove != null && lastPawnDoubleMove[0] == 3) {
                // Tốt trắng ở hàng 3 (index 3), tốt đen vừa nhảy từ 1 -> 3
                if (Math.abs(lastPawnDoubleMove[1] - y) == 1) {
                    // Tốt đen ở bên cạnh
                    ChessPiece adjacentPiece = boardCell[3][lastPawnDoubleMove[1]].currnetChessPiece;
                    if (adjacentPiece != null && adjacentPiece.type == PieceType.PAWN
                            && adjacentPiece.color == PieceColor.BLACK) {
                        // Highlight ô en passant
                        int enPassantX = 2; // Đi lên hàng 2
                        int enPassantY = lastPawnDoubleMove[1];
                        if (checkValidMove(enPassantX, enPassantY) && isMoveSafe(x, y, enPassantX, enPassantY)) {
                            boardCell[enPassantX][enPassantY].setColor(false); // Đỏ = nước ăn
                        }
                    }
                }
            }
        } else { // con đen
            int maxStep = (x == 1 ? 2 : 1);
            for (int i = x + 1; i <= x + maxStep; i++) {
                if (!checkValidMove(i, y))
                    break;
                ChessPiece chessPiece = boardCell[i][y].currnetChessPiece;
                if (chessPiece != null) { // bị chặn đầu
                    break;
                } else {
                    // Chỉ highlight nếu nước đi an toàn
                    if (isMoveSafe(x, y, i, y)) {
                        boardCell[i][y].setColor(true);
                    }
                }
            }
            // ăn chéo trái
            if (checkValidMove(x + 1, y - 1)) {
                CellPanel cellPanel = boardCell[x + 1][y - 1];
                if (cellPanel.currnetChessPiece != null) {
                    if (cellPanel.currnetChessPiece.color != thisPiece.color) {
                        // Chỉ highlight nếu nước đi an toàn
                        if (isMoveSafe(x, y, x + 1, y - 1)) {
                            cellPanel.setColor(false);
                        }
                    }
                }
            }
            // ăn chéo phải
            if (checkValidMove(x + 1, y + 1)) {
                CellPanel cellPanel = boardCell[x + 1][y + 1];
                if (cellPanel.currnetChessPiece != null) {
                    if (cellPanel.currnetChessPiece.color != thisPiece.color) {
                        // Chỉ highlight nếu nước đi an toàn
                        if (isMoveSafe(x, y, x + 1, y + 1)) {
                            cellPanel.setColor(false);
                        }
                    }
                }
            }

            // En Passant cho quân đen (BLACK)
            if (x == 4 && lastPawnDoubleMove != null && lastPawnDoubleMove[0] == 4) {
                // Tốt đen ở hàng 4 (index 4), tốt trắng vừa nhảy từ 6 -> 4
                if (Math.abs(lastPawnDoubleMove[1] - y) == 1) {
                    // Tốt trắng ở bân cạnh
                    ChessPiece adjacentPiece = boardCell[4][lastPawnDoubleMove[1]].currnetChessPiece;
                    if (adjacentPiece != null && adjacentPiece.type == PieceType.PAWN
                            && adjacentPiece.color == PieceColor.WHITE) {
                        // Highlight ô en passant
                        int enPassantX = 5; // Đi xuống hàng 5
                        int enPassantY = lastPawnDoubleMove[1];
                        if (checkValidMove(enPassantX, enPassantY) && isMoveSafe(x, y, enPassantX, enPassantY)) {
                            boardCell[enPassantX][enPassantY].setColor(false); // Đỏ = nước ăn
                        }
                    }
                }
            }
        }
    }

    private void KnightCheck(int x, int y) {
        ChessPiece thisPiece = boardCell[x][y].currnetChessPiece;
        if (thisPiece == null)
            return;

        // 8 hướng đi của quân Mã: (±2, ±1) và (±1, ±2)
        int[][] knightMoves = {
                { 2, 1 },
                { 2, -1 },
                { -2, 1 },
                { -2, -1 },
                { 1, 2 },
                { 1, -2 },
                { -1, 2 },
                { -1, -2 }
        };

        for (int i = 0; i < knightMoves.length; i++) {
            int newX = x + knightMoves[i][0];
            int newY = y + knightMoves[i][1];

            // 1) Kiểm tra ô mới có nằm trong bàn cờ không
            if (!checkValidMove(newX, newY))
                continue;

            CellPanel targetCell = boardCell[newX][newY];
            ChessPiece targetPiece = targetCell.currnetChessPiece;

            if (targetPiece == null) {
                // Chỉ highlight nếu nước đi an toàn
                if (isMoveSafe(x, y, newX, newY)) {
                    targetCell.setColor(true);
                }
            } else {
                // Nếu có quân đối phương -> có thể ăn
                if (targetPiece.color != thisPiece.color) {
                    // Chỉ highlight nếu nước đi an toàn
                    if (isMoveSafe(x, y, newX, newY)) {
                        targetCell.setColor(false); // false = ô có thể ăn
                    }
                }
                // Nếu là quân cùng màu thì không làm gì cả
            }
        }
    }

    private void RookCheck(int x, int y) {
        ChessPiece thisPiece = boardCell[x][y].currnetChessPiece;
        if (thisPiece == null)
            return;

        int[][] directions = {
                { -1, 0 }, // lên
                { 1, 0 }, // xuống
                { 0, -1 }, // trái
                { 0, 1 } // phải
        };

        // Duyệt từng hướng một
        for (int d = 0; d < directions.length; d++) {
            int dx = directions[d][0];
            int dy = directions[d][1];

            for (int step = 1; step < 8; step++) {
                int newX = x + dx * step;
                int newY = y + dy * step;
                // kiểm tra xem nước đi có nằm trong bàn cờ không
                if (!checkValidMove(newX, newY)) {
                    break;
                }

                CellPanel targetCell = boardCell[newX][newY];
                ChessPiece targetPiece = targetCell.currnetChessPiece;

                if (targetPiece == null) {
                    // Chỉ highlight nếu nước đi an toàn
                    if (isMoveSafe(x, y, newX, newY)) {
                        targetCell.setColor(true);
                    }
                } else {
                    if (targetPiece.color != thisPiece.color) {
                        // Chỉ highlight nếu nước đi an toàn
                        if (isMoveSafe(x, y, newX, newY)) {
                            targetCell.setColor(false); // ô ăn (đỏ)
                        }
                    }
                    break;
                }
            }
        }
    }

    private void BishopCheck(int x, int y) {
        ChessPiece thisPiece = boardCell[x][y].currnetChessPiece;
        if (thisPiece == null)
            return;

        int[][] directions = {
                { -1, -1 }, // lên - trái
                { -1, 1 }, // lên - phải
                { 1, -1 }, // xuống - trái
                { 1, 1 } // xuống - phải
        };

        for (int d = 0; d < directions.length; d++) {
            int dx = directions[d][0];
            int dy = directions[d][1];

            for (int step = 1; step < 8; step++) {
                int newX = x + dx * step;
                int newY = y + dy * step;

                if (!checkValidMove(newX, newY)) {
                    break;
                }

                CellPanel targetCell = boardCell[newX][newY];
                ChessPiece targetPiece = targetCell.currnetChessPiece;

                if (targetPiece == null) {
                    // Chỉ highlight nếu nước đi an toàn
                    if (isMoveSafe(x, y, newX, newY)) {
                        targetCell.setColor(true);
                    }
                } else {
                    if (targetPiece.color != thisPiece.color) {
                        // Chỉ highlight nếu nước đi an toàn
                        if (isMoveSafe(x, y, newX, newY)) {
                            targetCell.setColor(false);
                        }
                    }
                    break;
                }
            }
        }
    }

    private void KingCheck(int x, int y) {
        ChessPiece thisPiece = boardCell[x][y].currnetChessPiece;
        if (thisPiece == null)
            return;

        // 8 hướng xung quanh vua
        int[][] kingMoves = {
                { -1, -1 }, // lên trái
                { -1, 0 }, // lên
                { -1, 1 }, // lên phải
                { 0, -1 }, // trái
                { 0, 1 }, // phải
                { 1, -1 }, // xuống trái
                { 1, 0 }, // xuống
                { 1, 1 } // xuống phải
        };

        for (int i = 0; i < kingMoves.length; i++) {
            int newX = x + kingMoves[i][0];
            int newY = y + kingMoves[i][1];

            if (!checkValidMove(newX, newY))
                continue;

            CellPanel targetCell = boardCell[newX][newY];
            ChessPiece targetPiece = targetCell.currnetChessPiece;

            if (targetPiece == null) {
                // Chỉ highlight nếu nước đi an toàn (không bị chiếu)
                if (isMoveSafe(x, y, newX, newY)) {
                    targetCell.setColor(true);
                }
            } else {
                if (targetPiece.color != thisPiece.color) {
                    // Chỉ highlight nếu nước đi an toàn (không bị chiếu)
                    if (isMoveSafe(x, y, newX, newY)) {
                        targetCell.setColor(false);
                    }
                }
            }
        }

    }

    private ChessPiece handlePawnPromotionIfNeeded(ChessPiece piece, int toX) {
        if (piece == null)
            return null;

        // Chỉ quan tâm đến quân TỐT
        if (piece.type == PieceType.PAWN) {
            if (piece.color == PieceColor.WHITE && toX == 0) {
                return new ChessPiece(PieceColor.WHITE, PieceType.QUEEN);
            }
            if (piece.color == PieceColor.BLACK && toX == 7) {
                return new ChessPiece(PieceColor.BLACK, PieceType.QUEEN);
            }
        }

        return piece;
    }

    public boolean checkValidMove(int n) {
        return (n >= 0 && n <= 7);
    }

    public boolean checkValidMove(int x, int y) {
        return checkValidMove(x) && checkValidMove(y);
    }

    /**
     * Tìm vị trí vua của một màu
     */
    private int[] findKingPosition(PieceColor color) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessPiece piece = boardCell[i][j].currnetChessPiece;
                if (piece != null && piece.type == PieceType.KING && piece.color == color) {
                    return new int[] { i, j };
                }
            }
        }
        return null; // Không tìm thấy vua (không bao giờ xảy ra trong game bình thường)
    }

    /**
     * Kiểm tra xem một ô có đang bị quân đối phương tấn công không
     * 
     * @param x             tọa độ x của ô cần kiểm tra
     * @param y             tọa độ y của ô cần kiểm tra
     * @param attackerColor màu của quân tấn công
     */
    private boolean isSquareUnderAttack(int x, int y, PieceColor attackerColor) {
        // Duyệt qua tất cả các quân đối phương
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessPiece piece = boardCell[i][j].currnetChessPiece;
                if (piece == null || piece.color != attackerColor) {
                    continue; // Bỏ qua ô trống hoặc quân cùng màu
                }

                // Kiểm tra xem quân này có thể tấn công ô (x, y) không
                if (canPieceAttackSquare(i, j, x, y, piece)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Kiểm tra một quân có thể tấn công một ô không (không xét đến việc vua bị
     * chiếu)
     */
    private boolean canPieceAttackSquare(int fromX, int fromY, int toX, int toY, ChessPiece piece) {
        if (fromX == toX && fromY == toY) {
            return false; // Không thể tấn công chính mình
        }

        switch (piece.type) {
            case PAWN:
                return canPawnAttack(fromX, fromY, toX, toY, piece.color);
            case KNIGHT:
                return canKnightAttack(fromX, fromY, toX, toY);
            case ROOK:
                return canRookAttack(fromX, fromY, toX, toY);
            case BISHOP:
                return canBishopAttack(fromX, fromY, toX, toY);
            case QUEEN:
                return canQueenAttack(fromX, fromY, toX, toY);
            case KING:
                return canKingAttack(fromX, fromY, toX, toY);
            default:
                return false;
        }
    }

    private boolean canPawnAttack(int fromX, int fromY, int toX, int toY, PieceColor color) {
        // Tốt chỉ tấn công chéo 1 ô
        if (color == PieceColor.WHITE) {
            return (fromX - toX == 1) && Math.abs(fromY - toY) == 1;
        } else {
            return (toX - fromX == 1) && Math.abs(fromY - toY) == 1;
        }
    }

    private boolean canKnightAttack(int fromX, int fromY, int toX, int toY) {
        int dx = Math.abs(fromX - toX);
        int dy = Math.abs(fromY - toY);
        return (dx == 2 && dy == 1) || (dx == 1 && dy == 2);
    }

    private boolean canRookAttack(int fromX, int fromY, int toX, int toY) {
        // Xe đi theo hàng hoặc cột
        if (fromX != toX && fromY != toY) {
            return false;
        }

        // Kiểm tra không có quân chặn đường
        if (fromX == toX) { // Đi ngang
            int start = Math.min(fromY, toY) + 1;
            int end = Math.max(fromY, toY);
            for (int y = start; y < end; y++) {
                if (boardCell[fromX][y].currnetChessPiece != null) {
                    return false;
                }
            }
        } else { // Đi dọc
            int start = Math.min(fromX, toX) + 1;
            int end = Math.max(fromX, toX);
            for (int x = start; x < end; x++) {
                if (boardCell[x][fromY].currnetChessPiece != null) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean canBishopAttack(int fromX, int fromY, int toX, int toY) {
        // Tượng đi chéo
        if (Math.abs(fromX - toX) != Math.abs(fromY - toY)) {
            return false;
        }

        // Kiểm tra không có quân chặn đường
        int dx = (toX > fromX) ? 1 : -1;
        int dy = (toY > fromY) ? 1 : -1;
        int x = fromX + dx;
        int y = fromY + dy;

        while (x != toX && y != toY) {
            if (boardCell[x][y].currnetChessPiece != null) {
                return false;
            }
            x += dx;
            y += dy;
        }
        return true;
    }

    private boolean canQueenAttack(int fromX, int fromY, int toX, int toY) {
        // Hậu = Xe + Tượng
        return canRookAttack(fromX, fromY, toX, toY) || canBishopAttack(fromX, fromY, toX, toY);
    }

    private boolean canKingAttack(int fromX, int fromY, int toX, int toY) {
        // Vua chỉ đi 1 ô
        int dx = Math.abs(fromX - toX);
        int dy = Math.abs(fromY - toY);
        return dx <= 1 && dy <= 1;
    }

    /**
     * Kiểm tra vua của một màu có đang bị chiếu không
     */
    private boolean isKingInCheck(PieceColor kingColor) {
        int[] kingPos = findKingPosition(kingColor);
        if (kingPos == null) {
            return false;
        }

        PieceColor enemyColor = (kingColor == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;
        return isSquareUnderAttack(kingPos[0], kingPos[1], enemyColor);
    }

    /**
     * Simulate một nước đi và kiểm tra xem nước đi đó có làm vua bị chiếu không
     * 
     * @return true nếu nước đi hợp lệ (không làm vua bị chiếu), false nếu không hợp
     *         lệ
     */
    private boolean isMoveSafe(int fromX, int fromY, int toX, int toY) {
        // Lưu lại trạng thái ban đầu
        ChessPiece movingPiece = boardCell[fromX][fromY].currnetChessPiece;
        ChessPiece capturedPiece = boardCell[toX][toY].currnetChessPiece;

        // Thực hiện nước đi tạm thời
        boardCell[toX][toY].currnetChessPiece = movingPiece;
        boardCell[fromX][fromY].currnetChessPiece = null;

        // Kiểm tra vua có bị chiếu không
        boolean safe = !isKingInCheck(movingPiece.color);

        // Hoàn tác nước đi
        boardCell[fromX][fromY].currnetChessPiece = movingPiece;
        boardCell[toX][toY].currnetChessPiece = capturedPiece;

        return safe;
    }

    /**
     * Highlight vua đang bị chiếu bằng màu cam
     */
    private void highlightKingInCheck() {
        // Kiểm tra vua của người chơi hiện tại có bị chiếu không
        if (isKingInCheck(currentTurn)) {
            int[] kingPos = findKingPosition(currentTurn);
            if (kingPos != null) {
                boardCell[kingPos[0]][kingPos[1]].setCheckColor();
            }
        }
    }
}
