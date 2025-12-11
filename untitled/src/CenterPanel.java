import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CenterPanel extends JPanel {

    private final CellPanel[][] boardCell = new CellPanel[8][8];
    private BoardState boardState = BoardState.NO_SELECT;
    private CellPanel selectedCell;
    private PieceColor currentTurn = PieceColor.WHITE;

    // En Passant
    private int[] lastPawnDoubleMove = null;

    // Cấu hình người chơi
    private PlayerType whitePlayer = PlayerType.HUMAN;
    private PlayerType blackPlayer = PlayerType.HUMAN;
    private int searchDepth = 3;

    private enum PlayerType { HUMAN, COMPUTER }

    // --------------------------------------------------------------
    // Constructor
    // --------------------------------------------------------------
    public CenterPanel() {
        setLayout(new GridLayout(8, 8));
        boolean isWhite = true;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                CellPanel cell = new CellPanel(isWhite, i, j);
                placeInitialPieces(cell, i, j);
                add(cell);
                boardCell[i][j] = cell;
                isWhite = !isWhite;
            }
            isWhite = !isWhite;
        }
    }

    private void placeInitialPieces(CellPanel cell, int row, int col) {
        if (row == 1) cell.addImage(new ChessPiece(PieceColor.BLACK, PieceType.PAWN));
        if (row == 6) cell.addImage(new ChessPiece(PieceColor.WHITE, PieceType.PAWN));

        if (row == 0 || row == 7) {
            PieceColor color = (row == 0) ? PieceColor.BLACK : PieceColor.WHITE;
            if (col == 0 || col == 7) cell.addImage(new ChessPiece(color, PieceType.ROOK));
            if (col == 1 || col == 6) cell.addImage(new ChessPiece(color, PieceType.KNIGHT));
            if (col == 2 || col == 5) cell.addImage(new ChessPiece(color, PieceType.BISHOP));
            if (col == 3) cell.addImage(new ChessPiece(color, PieceType.QUEEN));
            if (col == 4) cell.addImage(new ChessPiece(color, PieceType.KING));
        }
    }

    // --------------------------------------------------------------
    // Gọi từ SetupGameDialog
    // --------------------------------------------------------------
    public void setPlayers(String white, String black, int depth) {
        whitePlayer = "Computer".equals(white) ? PlayerType.COMPUTER : PlayerType.HUMAN;
        blackPlayer = "Computer".equals(black) ? PlayerType.COMPUTER : PlayerType.HUMAN;
        searchDepth = depth;

        currentTurn = PieceColor.WHITE;
        lastPawnDoubleMove = null;
        deSelectCellPanelAll();
        boardState = BoardState.NO_SELECT;
        highlightKingInCheck();
    }

    // --------------------------------------------------------------
    // Click xử lý của người chơi
    // --------------------------------------------------------------
    public void onclickCellPanel(int x, int y) {
        // Nếu đang là lượt máy → chặn click người
        if ((currentTurn == PieceColor.WHITE && whitePlayer == PlayerType.COMPUTER) ||
                (currentTurn == PieceColor.BLACK && blackPlayer == PlayerType.COMPUTER)) {
            return;
        }

        CellPanel clicked = boardCell[x][y];
        ChessPiece piece = clicked.currentChessPiece;

        if (boardState == BoardState.NO_SELECT) {
            deSelectCellPanelAll();
            highlightKingInCheck();

            if (piece == null || piece.color != currentTurn) return;

            clicked.select();
            showLegalMoves(x, y);
            selectedCell = clicked;
            boardState = BoardState.PIECE_SELECT;

        } else { // PIECE_SELECT
            if (clicked.isValidMove) {
                executeMove(selectedCell.x, selectedCell.y, x, y);
                deSelectCellPanelAll();
                boardState = BoardState.NO_SELECT;
                highlightKingInCheck();

                // Nếu lượt tiếp theo là máy → tự động đi
                if ((currentTurn == PieceColor.WHITE && whitePlayer == PlayerType.COMPUTER) ||
                        (currentTurn == PieceColor.BLACK && blackPlayer == PlayerType.COMPUTER)) {
                    SwingUtilities.invokeLater(this::makeComputerMove);
                }
            } else {
                deSelectCellPanelAll();
                boardState = BoardState.NO_SELECT;
                onclickCellPanel(x, y); // chọn lại quân khác
            }
        }
    }

    // --------------------------------------------------------------
    // Thực hiện nước đi (của người hoặc máy)
    // --------------------------------------------------------------
    private void executeMove(int fromX, int fromY, int toX, int toY) {
        ChessPiece moving = boardCell[fromX][fromY].currentChessPiece;
        ChessPiece captured = boardCell[toX][toY].currentChessPiece;

        // Di chuyển quân
        boardCell[toX][toY].currentChessPiece = moving;
        boardCell[fromX][fromY].currentChessPiece = null;
        boardCell[toX][toY].addImage(moving);
        boardCell[fromX][fromY].removePiece();

        // Promotion (luôn lên Hậu)
        if (moving.type == PieceType.PAWN && (toX == 0 || toX == 7)) {
            moving.type = PieceType.QUEEN;
            boardCell[toX][toY].addImage(moving);
        }

        // En Passant
        if (moving.type == PieceType.PAWN && captured == null &&
                Math.abs(fromY - toY) == 1 && Math.abs(fromX - toX) == 1) {
            int removeX = (currentTurn == PieceColor.WHITE) ? toX + 1 : toX - 1;
            boardCell[removeX][toY].removePiece();
        }

        // Cập nhật double pawn move
        if (moving.type == PieceType.PAWN && Math.abs(fromX - toX) == 2) {
            lastPawnDoubleMove = new int[]{toX, toY};
        } else {
            lastPawnDoubleMove = null;
        }

        // Đổi lượt
        currentTurn = (currentTurn == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;
    }

    // --------------------------------------------------------------
    // Hiển thị các nước đi hợp lệ khi người chơi chọn quân
    // --------------------------------------------------------------
    private void showLegalMoves(int x, int y) {
        List<Move> moves = generateLegalMoves(x, y);
        for (Move m : moves) {
            boolean isCapture = boardCell[m.toX][m.toY].currentChessPiece != null;
            boardCell[m.toX][m.toY].setColor(!isCapture); // xanh nếu trống, đỏ nếu ăn
        }
    }

    private List<Move> generateLegalMoves(int x, int y) {
        ChessPiece p = boardCell[x][y].currentChessPiece;
        List<Move> list = new ArrayList<>();
        generateMovesForPiece(list, x, y, p);
        return list;
    }

    private void generateMovesForPiece(List<Move> list, int x, int y, ChessPiece p) {
        int dir = (p.color == PieceColor.WHITE) ? -1 : 1;

        switch (p.type) {
            case PAWN -> {
                int nx = x + dir;
                if (nx >= 0 && nx < 8 && boardCell[nx][y].currentChessPiece == null) {
                    if (isMoveSafe(x, y, nx, y)) list.add(new Move(x, y, nx, y));

                    // 2 ô đầu
                    if ((x == 6 && p.color == PieceColor.WHITE) || (x == 1 && p.color == PieceColor.BLACK)) {
                        int nx2 = x + 2 * dir;
                        if (boardCell[nx2][y].currentChessPiece == null && boardCell[nx][y].currentChessPiece == null)
                            if (isMoveSafe(x, y, nx2, y)) list.add(new Move(x, y, nx2, y));
                    }
                }
                // Ăn chéo + En Passant
                for (int dy : new int[]{-1, 1}) {
                    int ny = y + dy;
                    if (ny >= 0 && ny < 8 && nx >= 0 && nx < 8) {
                        ChessPiece target = boardCell[nx][ny].currentChessPiece;
                        if (target != null && target.color != p.color && isMoveSafe(x, y, nx, ny))
                            list.add(new Move(x, y, nx, ny));
                        else if (target == null && lastPawnDoubleMove != null &&
                                lastPawnDoubleMove[0] == nx && lastPawnDoubleMove[1] == ny)
                            if (isMoveSafe(x, y, nx, ny)) list.add(new Move(x, y, nx, ny));
                    }
                }
            }
            case KNIGHT -> {
                int[][] d = {{-2,-1},{-2,1},{-1,-2},{-1,2},{1,-2},{1,2},{2,-1},{2,1}};
                for (int[] dd : d) {
                    int nx = x + dd[0], ny = y + dd[1];
                    if (nx >= 0 && nx < 8 && ny >= 0 && ny < 8) {
                        ChessPiece t = boardCell[nx][ny].currentChessPiece;
                        if (t == null || t.color != p.color)
                            if (isMoveSafe(x, y, nx, ny)) list.add(new Move(x, y, nx, ny));
                    }
                }
            }
            case ROOK -> addLineMoves(list, x, y, p.color, new int[][]{{0,1},{0,-1},{1,0},{-1,0}});
            case BISHOP -> addLineMoves(list, x, y, p.color, new int[][]{{1,1},{1,-1},{-1,1},{-1,-1}});
            case QUEEN -> {
                addLineMoves(list, x, y, p.color, new int[][]{{0,1},{0,-1},{1,0},{-1,0}});
                addLineMoves(list, x, y, p.color, new int[][]{{1,1},{1,-1},{-1,1},{-1,-1}});
            }
            case KING -> {
                for (int dx = -1; dx <= 1; dx++) for (int dy = -1; dy <= 1; dy++) {
                    if (dx == 0 && dy == 0) continue;
                    int nx = x + dx, ny = y + dy;
                    if (nx >= 0 && nx < 8 && ny >= 0 && ny < 8) {
                        ChessPiece t = boardCell[nx][ny].currentChessPiece;
                        if (t == null || t.color != p.color)
                            if (isMoveSafe(x, y, nx, ny)) list.add(new Move(x, y, nx, ny));
                    }
                }
            }
        }
    }

    private void addLineMoves(List<Move> list, int x, int y, PieceColor color, int[][] dirs) {
        for (int[] d : dirs) {
            for (int step = 1; step < 8; step++) {
                int nx = x + step * d[0];
                int ny = y + step * d[1];
                if (nx < 0 || nx >= 8 || ny < 0 || ny >= 8) break;
                ChessPiece t = boardCell[nx][ny].currentChessPiece;
                if (t == null) {
                    if (isMoveSafe(x, y, nx, ny)) list.add(new Move(x, y, nx, ny));
                } else {
                    if (t.color != color && isMoveSafe(x, y, nx, ny)) list.add(new Move(x, y, nx, ny));
                    break;
                }
            }
        }
    }

    // --------------------------------------------------------------
    // AI – Minimax + Alpha-Beta
    // --------------------------------------------------------------
    private void makeComputerMove() {
        Move best = findBestMove(searchDepth, currentTurn == PieceColor.WHITE);
        if (best != null) {
            executeMove(best.fromX, best.fromY, best.toX, best.toY);
            deSelectCellPanelAll();
            highlightKingInCheck();
        }
    }

    private Move findBestMove(int depth, boolean maximizing) {
        Move best = null;
        int bestVal = maximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (Move m : getAllLegalMoves(currentTurn)) {
            makeMove(m);
            int val = minimax(depth - 1, !maximizing, Integer.MIN_VALUE, Integer.MAX_VALUE);
            undoMove(m);

            if (maximizing && val > bestVal || !maximizing && val < bestVal) {
                bestVal = val;
                best = m;
            }
        }
        return best;
    }

    private int minimax(int depth, boolean maximizing, int alpha, int beta) {
        if (depth == 0) return evaluateBoard();

        int best = maximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        PieceColor player = maximizing ? currentTurn : (currentTurn == PieceColor.WHITE ? PieceColor.BLACK : PieceColor.WHITE);

        for (Move m : getAllLegalMoves(player)) {
            makeMove(m);
            int val = minimax(depth - 1, !maximizing, alpha, beta);
            undoMove(m);

            if (maximizing) {
                best = Math.max(best, val);
                alpha = Math.max(alpha, best);
            } else {
                best = Math.min(best, val);
                beta = Math.min(beta, best);
            }
            if (beta <= alpha) break;
        }
        return best;
    }

    private List<Move> getAllLegalMoves(PieceColor color) {
        List<Move> all = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessPiece p = boardCell[i][j].currentChessPiece;
                if (p != null && p.color == color) {
                    generateMovesForPiece(all, i, j, p);
                }
            }
        }
        return all;
    }

    private void makeMove(Move m) {
        ChessPiece moving = boardCell[m.fromX][m.fromY].currentChessPiece;
        m.captured = boardCell[m.toX][m.toY].currentChessPiece;
        m.wasDoublePawn = (moving.type == PieceType.PAWN && Math.abs(m.fromX - m.toX) == 2);

        boardCell[m.toX][m.toY].currentChessPiece = moving;
        boardCell[m.fromX][m.fromY].currentChessPiece = null;

        if (moving.type == PieceType.PAWN && (m.toX == 0 || m.toX == 7)) {
            moving.type = PieceType.QUEEN;
        }
    }

    private void undoMove(Move m) {
        ChessPiece moving = boardCell[m.toX][m.toY].currentChessPiece;
        boardCell[m.fromX][m.fromY].currentChessPiece = moving;
        boardCell[m.toX][m.toY].currentChessPiece = m.captured;
        if (m.wasDoublePawn) lastPawnDoubleMove = new int[]{m.toX, m.toY};
        else if (lastPawnDoubleMove != null && lastPawnDoubleMove[0] == m.toX && lastPawnDoubleMove[1] == m.toY)
            lastPawnDoubleMove = null;
    }

    private int evaluateBoard() {
        int score = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessPiece p = boardCell[i][j].currentChessPiece;
                if (p != null) {
                    int value = switch (p.type) {
                        case PAWN -> 100;
                        case KNIGHT, BISHOP -> 320;
                        case ROOK -> 500;
                        case QUEEN -> 900;
                        case KING -> 20000;
                    };
                    score += (p.color == PieceColor.WHITE) ? value : -value;
                }
            }
        }
        return score;
    }

    // --------------------------------------------------------------
    // Kiểm tra chiếu, an toàn, v.v. (đã đầy đủ, không còn typo)
    // --------------------------------------------------------------
    private boolean isMoveSafe(int fromX, int fromY, int toX, int toY) {
        ChessPiece moving = boardCell[fromX][fromY].currentChessPiece;
        ChessPiece captured = boardCell[toX][toY].currentChessPiece;

        boardCell[toX][toY].currentChessPiece = moving;
        boardCell[fromX][fromY].currentChessPiece = null;

        boolean safe = !isKingInCheck(moving.color);

        boardCell[fromX][fromY].currentChessPiece = moving;
        boardCell[toX][toY].currentChessPiece = captured;
        return safe;
    }

    private boolean isKingInCheck(PieceColor kingColor) {
        int[] pos = findKingPosition(kingColor);
        if (pos == null) return false;
        return isSquareUnderAttack(pos[0], pos[1], kingColor == PieceColor.WHITE ? PieceColor.BLACK : PieceColor.WHITE);
    }

    private int[] findKingPosition(PieceColor color) {
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                if (boardCell[i][j].currentChessPiece != null &&
                        boardCell[i][j].currentChessPiece.type == PieceType.KING &&
                        boardCell[i][j].currentChessPiece.color == color)
                    return new int[]{i, j};
        return null;
    }

    private boolean isSquareUnderAttack(int x, int y, PieceColor attackerColor) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessPiece p = boardCell[i][j].currentChessPiece;
                if (p != null && p.color == attackerColor) {
                    if (canPieceAttack(p.type, i, j, x, y)) return true;
                }
            }
        }
        return false;
    }

    private boolean canPieceAttack(PieceType type, int fx, int fy, int tx, int ty) {
        return switch (type) {
            case PAWN -> canPawnAttack(fx, fy, tx, ty, boardCell[fx][fy].currentChessPiece.color);
            case KNIGHT -> canKnightAttack(fx, fy, tx, ty);
            case ROOK -> canRookAttack(fx, fy, tx, ty);
            case BISHOP -> canBishopAttack(fx, fy, tx, ty);
            case QUEEN -> canQueenAttack(fx, fy, tx, ty);
            case KING -> canKingAttack(fx, fy, tx, ty);
        };
    }

    private boolean canPawnAttack(int fx, int fy, int tx, int ty, PieceColor color) {
        int dir = (color == PieceColor.WHITE) ? -1 : 1;
        return (tx - fx == dir) && Math.abs(ty - fy) == 1;
    }

    private boolean canKnightAttack(int fx, int fy, int tx, int ty) {
        int dx = Math.abs(fx - tx), dy = Math.abs(fy - ty);
        return (dx == 2 && dy == 1) || (dx == 1 && dy == 2);
    }

    private boolean canRookAttack(int fx, int fy, int tx, int ty) {
        if (fx != tx && fy != ty) return false;
        int stepX = Integer.compare(tx, fx), stepY = Integer.compare(ty, fy);
        int x = fx + stepX, y = fy + stepY;
        while (x != tx || y != ty) {
            if (boardCell[x][y].currentChessPiece != null) return false;
            x += stepX; y += stepY;
        }
        return true;
    }

    private boolean canBishopAttack(int fx, int fy, int tx, int ty) {
        if (Math.abs(fx - tx) != Math.abs(fy - ty)) return false;
        int dx = Integer.compare(tx, fx), dy = Integer.compare(ty, fy);
        int x = fx + dx, y = fy + dy;
        while (x != tx) {
            if (boardCell[x][y].currentChessPiece != null) return false;
            x += dx; y += dy;
        }
        return true;
    }

    private boolean canQueenAttack(int fx, int fy, int tx, int ty) {
        return canRookAttack(fx, fy, tx, ty) || canBishopAttack(fx, fy, tx, ty);
    }

    private boolean canKingAttack(int fx, int fy, int tx, int ty) {
        return Math.abs(fx - tx) <= 1 && Math.abs(fy - ty) <= 1;
    }

    // --------------------------------------------------------------
    // Hỗ trợ UI
    // --------------------------------------------------------------
    private void deSelectCellPanelAll() {
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                boardCell[i][j].deselect();
    }

    private void highlightKingInCheck() {
        deSelectCellPanelAll();
        if (isKingInCheck(currentTurn)) {
            int[] pos = findKingPosition(currentTurn);
            if (pos != null) boardCell[pos[0]][pos[1]].setCheckColor();
        }
    }

    // Inner class Move cho Minimax
    private static class Move {
        int fromX, fromY, toX, toY;
        ChessPiece captured;
        boolean wasDoublePawn;

        Move(int fx, int fy, int tx, int ty) {
            fromX = fx; fromY = fy; toX = tx; toY = ty;
        }
    }
}