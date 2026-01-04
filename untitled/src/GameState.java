import java.util.ArrayList;
import java.util.List;

public class GameState implements Cloneable {
    private ChessPiece[][] board;
    private PieceColor currentTurn;

    public GameState(ChessPiece[][] board, PieceColor currentTurn) {
        this.board = new ChessPiece[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessPiece p = board[i][j];
                if (p != null) {
                    this.board[i][j] = new ChessPiece(p.color, p.type);
                } else {
                    this.board[i][j] = null;
                }
            }
        }
        this.currentTurn = currentTurn;
    }

    // SỬA: Constructor này đang trống, cần khởi tạo giống constructor trên
    public GameState(ChessPiece[][] board) {
        this(board, PieceColor.WHITE); // mặc định là lượt trắng
    }

    public PieceColor getCurrentTurn() {
        return currentTurn;
    }

    public void switchTurn() {
        currentTurn = (currentTurn == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;
    }

    public ChessPiece[][] getBoard() {
        return board;
    }

    @Override
    public GameState clone() {
        return new GameState(this.board, this.currentTurn);
    }

    public void applyMove(Move move) {
        ChessPiece p = board[move.fromX][move.fromY];
        if (p != null) {
            board[move.toX][move.toY] = new ChessPiece(p.color, p.type);
            board[move.fromX][move.fromY] = null;
        }
    }

    // THÊM: Kiểm tra xem vua có bị chiếu không
    public boolean isKingInCheck(PieceColor kingColor) {
        int[] kingPos = findKingPosition(kingColor);
        if (kingPos == null) return false;

        PieceColor enemyColor = (kingColor == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;
        return isSquareUnderAttack(kingPos[0], kingPos[1], enemyColor);
    }

    // THÊM: Tìm vị trí vua
    private int[] findKingPosition(PieceColor color) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessPiece p = board[i][j];
                if (p != null && p.type == PieceType.KING && p.color == color) {
                    return new int[]{i, j};
                }
            }
        }
        return null;
    }

    // THÊM: Kiểm tra ô có bị tấn công không
    private boolean isSquareUnderAttack(int x, int y, PieceColor attackerColor) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessPiece p = board[i][j];
                if (p == null || p.color != attackerColor) continue;

                if (canPieceAttackSquare(i, j, x, y, p)) {
                    return true;
                }
            }
        }
        return false;
    }

    // THÊM: Kiểm tra quân có thể tấn công ô không
    private boolean canPieceAttackSquare(int fromX, int fromY, int toX, int toY, ChessPiece piece) {
        if (fromX == toX && fromY == toY) return false;

        switch (piece.type) {
            case PAWN:
                return canPawnAttack(fromX, fromY, toX, toY, piece.color);
            case KNIGHT:
                int dx = Math.abs(fromX - toX);
                int dy = Math.abs(fromY - toY);
                return (dx == 2 && dy == 1) || (dx == 1 && dy == 2);
            case ROOK:
                return canRookAttack(fromX, fromY, toX, toY);
            case BISHOP:
                return canBishopAttack(fromX, fromY, toX, toY);
            case QUEEN:
                return canRookAttack(fromX, fromY, toX, toY) || canBishopAttack(fromX, fromY, toX, toY);
            case KING:
                dx = Math.abs(fromX - toX);
                dy = Math.abs(fromY - toY);
                return dx <= 1 && dy <= 1;
            default:
                return false;
        }
    }

    private boolean canPawnAttack(int fromX, int fromY, int toX, int toY, PieceColor color) {
        if (color == PieceColor.WHITE) {
            return (fromX - toX == 1) && Math.abs(fromY - toY) == 1;
        } else {
            return (toX - fromX == 1) && Math.abs(fromY - toY) == 1;
        }
    }

    private boolean canRookAttack(int fromX, int fromY, int toX, int toY) {
        if (fromX != toX && fromY != toY) return false;

        if (fromX == toX) {
            int start = Math.min(fromY, toY) + 1;
            int end = Math.max(fromY, toY);
            for (int y = start; y < end; y++) {
                if (board[fromX][y] != null) return false;
            }
        } else {
            int start = Math.min(fromX, toX) + 1;
            int end = Math.max(fromX, toX);
            for (int x = start; x < end; x++) {
                if (board[x][fromY] != null) return false;
            }
        }
        return true;
    }

    private boolean canBishopAttack(int fromX, int fromY, int toX, int toY) {
        if (Math.abs(fromX - toX) != Math.abs(fromY - toY)) return false;

        int dx = (toX > fromX) ? 1 : -1;
        int dy = (toY > fromY) ? 1 : -1;
        int x = fromX + dx;
        int y = fromY + dy;

        while (x != toX && y != toY) {
            if (board[x][y] != null) return false;
            x += dx;
            y += dy;
        }
        return true;
    }

    // SỬA: isOver() cần kiểm tra cả checkmate
    public boolean isOver() {
        List<Move> moves = generateAllLegalMoves(currentTurn);
        return moves.isEmpty();
    }

    public List<GameState> generateChildStates() {
        List<GameState> children = new ArrayList<>();
        List<Move> moves = generateAllLegalMoves(currentTurn);
        for (Move m : moves) {
            GameState newState = this.clone();
            newState.applyMove(m);
            newState.switchTurn();
            children.add(newState);
        }
        return children;
    }

    // SỬA: generateAllLegalMoves cần lọc các nước đi làm vua bị chiếu
    public List<Move> generateAllLegalMoves(PieceColor side) {
        List<Move> allMoves = new ArrayList<>();

        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                ChessPiece p = board[x][y];
                if (p == null || p.color != side) continue;

                switch (p.type) {
                    case KNIGHT: addKnightMoves(x, y, p, allMoves); break;
                    case ROOK: addRookMoves(x, y, p, allMoves); break;
                    case BISHOP: addBishopMoves(x, y, p, allMoves); break;
                    case QUEEN: addQueenMoves(x, y, p, allMoves); break;
                    case KING: addKingMoves(x, y, p, allMoves); break;
                    case PAWN: addPawnMoves(x, y, p, allMoves); break;
                }
            }
        }

        // LỌC: Chỉ giữ lại các nước đi không làm vua bị chiếu
        List<Move> legalMoves = new ArrayList<>();
        for (Move m : allMoves) {
            if (isMoveSafe(m, side)) {
                legalMoves.add(m);
            }
        }

        return legalMoves;
    }

    // THÊM: Kiểm tra nước đi có an toàn không (không làm vua bị chiếu)
    private boolean isMoveSafe(Move move, PieceColor side) {
        // Lưu trạng thái
        ChessPiece captured = board[move.toX][move.toY];

        // Thực hiện nước đi
        board[move.toX][move.toY] = move.movedPiece;
        board[move.fromX][move.fromY] = null;

        // Kiểm tra vua có bị chiếu không
        boolean safe = !isKingInCheck(side);

        // Hoàn tác
        board[move.fromX][move.fromY] = move.movedPiece;
        board[move.toX][move.toY] = captured;

        return safe;
    }

    // Các phương thức add moves giữ nguyên như code gốc...
    private void addQueenMoves(int x, int y, ChessPiece p, List<Move> moves) {
        addRookMoves(x, y, p, moves);
        addBishopMoves(x, y, p, moves);
    }

    private void addKnightMoves(int x, int y, ChessPiece piece, List<Move> moves) {
        int[][] knightMoves = {
                {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
                {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
        };

        for (int i = 0; i < knightMoves.length; i++) {
            int newX = x + knightMoves[i][0];
            int newY = y + knightMoves[i][1];

            if (!isInsideBoard(newX, newY)) continue;
            ChessPiece target = board[newX][newY];

            if (target != null && target.color == piece.color) continue;

            Move m = new Move(x, y, newX, newY, piece);
            moves.add(m);
        }
    }

    private boolean isInsideBoard(int x, int y) {
        return x >= 0 && x < 8 && y >= 0 && y < 8;
    }

    private void addRookMoves(int x, int y, ChessPiece piece, List<Move> moves) {
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        for (int d = 0; d < directions.length; d++) {
            int dx = directions[d][0];
            int dy = directions[d][1];

            for (int step = 1; step < 8; step++) {
                int newX = x + dx * step;
                int newY = y + dy * step;

                if (!isInsideBoard(newX, newY)) break;

                ChessPiece target = board[newX][newY];

                if (target == null) {
                    moves.add(new Move(x, y, newX, newY, piece));
                } else {
                    if (target.color != piece.color) {
                        moves.add(new Move(x, y, newX, newY, piece));
                    }
                    break;
                }
            }
        }
    }

    private void addBishopMoves(int x, int y, ChessPiece piece, List<Move> moves) {
        int[][] directions = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};

        for (int d = 0; d < directions.length; d++) {
            int dx = directions[d][0];
            int dy = directions[d][1];

            for (int step = 1; step < 8; step++) {
                int newX = x + dx * step;
                int newY = y + dy * step;

                if (!isInsideBoard(newX, newY)) break;

                ChessPiece target = board[newX][newY];

                if (target == null) {
                    moves.add(new Move(x, y, newX, newY, piece));
                } else {
                    if (target.color != piece.color) {
                        moves.add(new Move(x, y, newX, newY, piece));
                    }
                    break;
                }
            }
        }
    }

    private void addKingMoves(int x, int y, ChessPiece piece, List<Move> moves) {
        int[][] kingMoves = {
                {-1, -1}, {-1, 0}, {-1, 1},
                {0, -1}, {0, 1},
                {1, -1}, {1, 0}, {1, 1}
        };

        for (int i = 0; i < kingMoves.length; i++) {
            int newX = x + kingMoves[i][0];
            int newY = y + kingMoves[i][1];

            if (!isInsideBoard(newX, newY)) continue;

            ChessPiece target = board[newX][newY];
            if (target != null && target.color == piece.color) continue;

            moves.add(new Move(x, y, newX, newY, piece));
        }
    }

    private void addPawnMoves(int x, int y, ChessPiece piece, List<Move> moves) {
        if (piece.color == PieceColor.WHITE) {
            int forwardX = x - 1;

            if (isInsideBoard(forwardX, y) && board[forwardX][y] == null) {
                moves.add(new Move(x, y, forwardX, y, piece));

                if (x == 6) {
                    int doubleForwardX = x - 2;
                    if (isInsideBoard(doubleForwardX, y) && board[doubleForwardX][y] == null) {
                        moves.add(new Move(x, y, doubleForwardX, y, piece));
                    }
                }
            }

            int[] captureY = {y - 1, y + 1};
            for (int cy : captureY) {
                if (isInsideBoard(forwardX, cy)) {
                    ChessPiece target = board[forwardX][cy];
                    if (target != null && target.color != piece.color) {
                        moves.add(new Move(x, y, forwardX, cy, piece));
                    }
                }
            }

        } else {
            int forwardX = x + 1;

            if (isInsideBoard(forwardX, y) && board[forwardX][y] == null) {
                moves.add(new Move(x, y, forwardX, y, piece));

                if (x == 1) {
                    int doubleForwardX = x + 2;
                    if (isInsideBoard(doubleForwardX, y) && board[doubleForwardX][y] == null) {
                        moves.add(new Move(x, y, doubleForwardX, y, piece));
                    }
                }
            }

            int[] captureY = {y - 1, y + 1};
            for (int cy : captureY) {
                if (isInsideBoard(forwardX, cy)) {
                    ChessPiece target = board[forwardX][cy];
                    if (target != null && target.color != piece.color) {
                        moves.add(new Move(x, y, forwardX, cy, piece));
                    }
                }
            }
        }
    }
}





