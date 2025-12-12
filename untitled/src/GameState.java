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

    public GameState(ChessPiece[][] board) {
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
        board[move.toX][move.toY] = move.movedPiece;
        board[move.fromX][move.fromY] = null;
    }

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
            newState.switchTurn();  // đổi lượt cho state con
            children.add(newState);
        }

        return children;
    }

    public List<Move> generateAllLegalMoves(PieceColor side) {
        List<Move> moves = new ArrayList<>();
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                ChessPiece p = board[x][y];
                if (p == null) continue;
                if (p.color != side) continue;      // không phải quân của bên 'side' thì bỏ
                switch (p.type) {
                    case KNIGHT:
                        addKnightMoves(x, y, p, moves);
                        break;
                    case ROOK:
                        addRookMoves(x, y, p, moves);
                        break;
                    case BISHOP:
                        addBishopMoves(x, y, p, moves);
                        break;
                    case QUEEN:
                        addQueenMoves(x, y, p, moves);
                        break;
                    case KING:
                        addKingMoves(x, y, p, moves);
                        break;
                    case PAWN:
                        addPawnMoves(x, y, p, moves);
                        break;

                    default:
                        break;
                }
            }
        }


        return moves;
    }

    private void addQueenMoves(int x, int y, ChessPiece p, List<Move> moves) {
        addRookMoves(x, y, p, moves);
        addBishopMoves(x, y, p, moves);
    }

    private void addKnightMoves(int x, int y, ChessPiece piece, List<Move> moves) {
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

            if (!isInsideBoard(newX, newY)) continue;
            ChessPiece target = board[newX][newY];

            if (target != null && target.color == piece.color) {
                continue;
            }
            // 3) Nếu ô trống hoặc có quân đối phương -> tạo Move
            Move m = new Move(x, y, newX, newY, piece);
            moves.add(m);
        }
    }


    // giống checkValidMove bên CenterPanel
    private boolean isInsideBoard(int x, int y) {
        return x >= 0 && x < 8 && y >= 0 && y < 8;
    }
    private void addRookMoves(int x, int y, ChessPiece piece, List<Move> moves) {
        // 4 hướng đi của Xe: lên, xuống, trái, phải
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

            for (int step = 1; step < 8; step++) {
                int newX = x + dx * step;
                int newY = y + dy * step;

                if (!isInsideBoard(newX, newY)) {
                    break;
                }

                ChessPiece target = board[newX][newY];

                // 2) Nếu ô trống: có thể đi tiếp, tạo 1 Move
                if (target == null) {
                    Move m = new Move(x, y, newX, newY, piece);
                    moves.add(m);
                    // không break, vì có thể đi xa hơn nữa
                } else {
                    if (target.color == piece.color) {
                        break;
                    }

                    Move m = new Move(x, y, newX, newY, piece);
                    moves.add(m);
                    break;
                }
            }
        }
    }
    private void addBishopMoves(int x, int y, ChessPiece piece, List<Move> moves) {

        int[][] directions = {
                {-1, -1}, // lên - trái
                {-1,  1}, // lên - phải
                { 1, -1}, // xuống - trái
                { 1,  1}  // xuống - phải
        };

        // duyệt từng hướng
        for (int d = 0; d < directions.length; d++) {
            int dx = directions[d][0];
            int dy = directions[d][1];

            // đi xa tối đa 7 ô theo hướng đó
            for (int step = 1; step < 8; step++) {
                int newX = x + dx * step;
                int newY = y + dy * step;

                if (!isInsideBoard(newX, newY)) {
                    break;
                }

                ChessPiece target = board[newX][newY];

                if (target == null) {
                    Move m = new Move(x, y, newX, newY, piece);
                    moves.add(m);
                    // không break, vì tượng có thể đi xa hơn nữa cùng hướng
                } else {
                    if (target.color == piece.color) {
                        break;
                    }

                    Move m = new Move(x, y, newX, newY, piece);
                    moves.add(m);
                    break;
                }
            }
        }
    }
    private void addKingMoves(int x, int y, ChessPiece piece, List<Move> moves) {
        int[][] kingMoves = {
                {-1, -1}, // lên trái
                {-1,  0}, // lên
                {-1,  1}, // lên phải
                { 0, -1}, // trái
                { 0,  1}, // phải
                { 1, -1}, // xuống trái
                { 1,  0}, // xuống
                { 1,  1}  // xuống phải
        };

        for (int i = 0; i < kingMoves.length; i++) {
            int newX = x + kingMoves[i][0];
            int newY = y + kingMoves[i][1];

            if (!isInsideBoard(newX, newY)) continue;

            ChessPiece target = board[newX][newY];
            if (target != null && target.color == piece.color) {
                continue;
            }
            Move m = new Move(x, y, newX, newY, piece);
            moves.add(m);
        }
    }
    private void addPawnMoves(int x, int y, ChessPiece piece, List<Move> moves) {
        // Tốt trắng đi lên (giảm x), tốt đen đi xuống (tăng x)
        if (piece.color == PieceColor.WHITE) {
            int forwardX = x - 1;

            // 1. Đi thẳng 1 ô
            if (isInsideBoard(forwardX, y) && board[forwardX][y] == null) {
                moves.add(new Move(x, y, forwardX, y, piece));

                // 2. Đi thẳng 2 ô nếu đang ở hàng xuất phát (x == 6) và cả 2 ô đều trống
                if (x == 6) {
                    int doubleForwardX = x - 2;
                    if (isInsideBoard(doubleForwardX, y)
                            && board[doubleForwardX][y] == null) {
                        moves.add(new Move(x, y, doubleForwardX, y, piece));
                    }
                }
            }

            // 3. Ăn chéo trái: (x-1, y-1)
            int captureLeftX = x - 1;
            int captureLeftY = y - 1;
            if (isInsideBoard(captureLeftX, captureLeftY)) {
                ChessPiece target = board[captureLeftX][captureLeftY];
                if (target != null && target.color != piece.color) {
                    moves.add(new Move(x, y, captureLeftX, captureLeftY, piece));
                }
            }

            // 4. Ăn chéo phải: (x-1, y+1)
            int captureRightX = x - 1;
            int captureRightY = y + 1;
            if (isInsideBoard(captureRightX, captureRightY)) {
                ChessPiece target = board[captureRightX][captureRightY];
                if (target != null && target.color != piece.color) {
                    moves.add(new Move(x, y, captureRightX, captureRightY, piece));
                }
            }

        } else { // ===== TỐT ĐEN =====
            int forwardX = x + 1;

            // 1. Đi thẳng 1 ô
            if (isInsideBoard(forwardX, y) && board[forwardX][y] == null) {
                moves.add(new Move(x, y, forwardX, y, piece));

                // 2. Đi thẳng 2 ô nếu đang ở hàng xuất phát (x == 1) và cả 2 ô đều trống
                if (x == 1) {
                    int doubleForwardX = x + 2;
                    if (isInsideBoard(doubleForwardX, y)
                            && board[doubleForwardX][y] == null) {
                        moves.add(new Move(x, y, doubleForwardX, y, piece));
                    }
                }
            }

            // 3. Ăn chéo trái: (x+1, y-1)
            int captureLeftX = x + 1;
            int captureLeftY = y - 1;
            if (isInsideBoard(captureLeftX, captureLeftY)) {
                ChessPiece target = board[captureLeftX][captureLeftY];
                if (target != null && target.color != piece.color) {
                    moves.add(new Move(x, y, captureLeftX, captureLeftY, piece));
                }
            }

            // 4. Ăn chéo phải: (x+1, y+1)
            int captureRightX = x + 1;
            int captureRightY = y + 1;
            if (isInsideBoard(captureRightX, captureRightY)) {
                ChessPiece target = board[captureRightX][captureRightY];
                if (target != null && target.color != piece.color) {
                    moves.add(new Move(x, y, captureRightX, captureRightY, piece));
                }
            }
        }
    }







}
