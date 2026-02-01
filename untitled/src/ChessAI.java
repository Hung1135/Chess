import java.util.List;
import statistic.PerformanceLogger;
import statistic.PerformanceTracker;

public class ChessAI {
    private PieceColor aiColor;
    private Move bestMove;
    private int depth = 3;
    private int moveCounter = 0; // Đếm số nước đi
    private String algorithmType = "AlphaBeta"; // "Minimax" hoặc "AlphaBeta"

    public Move getBestMove() {
        return bestMove;
    }

    public ChessAI(PieceColor aiColor) {
        this.aiColor = aiColor;
    }

    public PieceColor getAiColor() {
        return aiColor;
    }

    public void setAiColor(PieceColor aiColor) {
        this.aiColor = aiColor;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getDepth() {
        return depth;
    }

    /**
     * Chọn thuật toán: "Minimax" hoặc "AlphaBeta"
     */
    public void setAlgorithmType(String algorithmType) {
        this.algorithmType = algorithmType;
    }

    public String getAlgorithmType() {
        return algorithmType;
    }

    public int heuristic(GameState state) {
        int score = 0;
        score += materialScore(state);
        score += centerScore(state);
        score += bishopPairScore(state);
        score += mobilityScore(state);

        return score;
    }

    private int getPieceValue(ChessPiece p) {
        switch (p.type) {
            case PAWN: return 100;
            case KNIGHT: return 300;
            case BISHOP: return 300;
            case ROOK: return 500;
            case QUEEN: return 900;
            case KING: return 10000;
            default: return 0;
        }
    }

    /**
     * Minimax thuần túy (không cắt tỉa)
     */
    public int minimax(boolean maxmin, GameState state, int depth) {
        if (depth == 0 || state.isOver()) {
            return heuristic(state);
        }

        if (maxmin) {
            int maxEval = Integer.MIN_VALUE;
            for (GameState child : state.generateChildStates()) {
                int eval = minimax(false, child, depth - 1);
                maxEval = Math.max(maxEval, eval);
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (GameState child : state.generateChildStates()) {
                int eval = minimax(true, child, depth - 1);
                minEval = Math.min(minEval, eval);
            }
            return minEval;
        }
    }

    /**
     * Alpha-Beta Pruning
     */
    public int alphaBeta(boolean maxmin, GameState state, int depth, int alpha, int beta) {
        if (depth == 0 || state.isOver()) {
            return heuristic(state);
        }

        if (maxmin) {
            int maxEval = Integer.MIN_VALUE;

            for (GameState child : state.generateChildStates()) {
                int eval = alphaBeta(false, child, depth - 1, alpha, beta);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);

                if (beta <= alpha) {
                    break; // Beta cutoff
                }
            }

            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;

            for (GameState child : state.generateChildStates()) {
                int eval = alphaBeta(true, child, depth - 1, alpha, beta);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);

                if (beta <= alpha) {
                    break; // Alpha cutoff
                }
            }

            return minEval;
        }
    }

    /**
     * Tìm nước đi tốt nhất với logging
     */
    public Move findBestMove(GameState root, int depth) {
        bestMove = null;
        moveCounter++;

        // Tạo tracker để đo hiệu suất
        PerformanceTracker tracker = new PerformanceTracker();
        tracker.start();

        List<Move> moves = root.generateAllLegalMoves(aiColor);

        if (moves.isEmpty()) {
            return null;
        }

        // Chọn thuật toán
        if (algorithmType.equalsIgnoreCase("Minimax")) {
            bestMove = findBestMoveWithMinimax(root, moves, depth);
        } else {
            bestMove = findBestMoveWithAlphaBeta(root, moves, depth);
        }

        // Lấy kết quả đo
        PerformanceTracker.PerformanceResult result = tracker.stop();

        // Log hiệu suất
        PerformanceLogger.logPerformance(
                algorithmType,
                result.timeSeconds,
                result.memoryMB,
                depth,
                moveCounter
        );

        return bestMove;
    }

    /**
     * Tìm nước đi tốt nhất bằng Minimax
     */
    private Move findBestMoveWithMinimax(GameState root, List<Move> moves, int depth) {
        Move best = null;

        if (aiColor == PieceColor.WHITE) {
            int bestValue = Integer.MIN_VALUE;

            for (Move m : moves) {
                GameState child = root.clone();
                child.applyMove(m);
                child.switchTurn();

                int value = minimax(false, child, depth - 1);

                if (value > bestValue) {
                    bestValue = value;
                    best = m;
                }
            }
        } else {
            int bestValue = Integer.MAX_VALUE;

            for (Move m : moves) {
                GameState child = root.clone();
                child.applyMove(m);
                child.switchTurn();

                int value = minimax(true, child, depth - 1);

                if (value < bestValue) {
                    bestValue = value;
                    best = m;
                }
            }
        }

        return best;
    }

    /**
     * Tìm nước đi tốt nhất bằng Alpha-Beta
     */
    private Move findBestMoveWithAlphaBeta(GameState root, List<Move> moves, int depth) {
        Move best = null;

        if (aiColor == PieceColor.WHITE) {
            int bestValue = Integer.MIN_VALUE;

            for (Move m : moves) {
                GameState child = root.clone();
                child.applyMove(m);
                child.switchTurn();

                int value = alphaBeta(false, child, depth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE);

                if (value > bestValue) {
                    bestValue = value;
                    best = m;
                }
            }
        } else {
            int bestValue = Integer.MAX_VALUE;

            for (Move m : moves) {
                GameState child = root.clone();
                child.applyMove(m);
                child.switchTurn();

                int value = alphaBeta(true, child, depth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE);

                if (value < bestValue) {
                    bestValue = value;
                    best = m;
                }
            }
        }

        return best;
    }

    /**
     * Reset counter khi bắt đầu game mới
     */
    public void resetMoveCounter() {
        moveCounter = 0;
    }
    private int materialScore(GameState state) {
        ChessPiece[][] board = state.getBoard();
        int s = 0;
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                ChessPiece p = board[x][y];
                if (p == null) continue;
                int sign = (p.color == PieceColor.WHITE) ? 1 : -1;
                s += sign * getPieceValue(p);
            }
        }
        return s;
    }
    private int centerScore(GameState state) {
        final int W_CENTER = 3;
        ChessPiece[][] board = state.getBoard();
        int s = 0;

        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                ChessPiece p = board[x][y];
                if (p == null) continue;
                if (p.type != PieceType.KNIGHT && p.type != PieceType.BISHOP) continue;

                int sign = (p.color == PieceColor.WHITE) ? 1 : -1;
                int d = distToCenter(x, y);
                s += sign * (4 - d) * W_CENTER;
            }
        }
        return s;
    }
    private int mobilityScore(GameState state) {
        final int W_MOBILITY = 2;
        int whiteMoves = state.generateAllLegalMoves(PieceColor.WHITE).size();
        int blackMoves = state.generateAllLegalMoves(PieceColor.BLACK).size();
        return (whiteMoves - blackMoves) * W_MOBILITY;
    }
    private int distToCenter(int x, int y) {
        int d1 = Math.abs(x - 3) + Math.abs(y - 3);
        int d2 = Math.abs(x - 3) + Math.abs(y - 4);
        int d3 = Math.abs(x - 4) + Math.abs(y - 3);
        int d4 = Math.abs(x - 4) + Math.abs(y - 4);
        return Math.min(Math.min(d1, d2), Math.min(d3, d4));
    }
    private int bishopPairScore(GameState state) {
        final int W_BISHOP_PAIR = 30;
        ChessPiece[][] board = state.getBoard();

        int whiteB = 0, blackB = 0;
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                ChessPiece p = board[x][y];
                if (p == null) continue;
                if (p.type != PieceType.BISHOP) continue;

                if (p.color == PieceColor.WHITE) whiteB++;
                else blackB++;
            }
        }

        int score = 0;
        if (whiteB >= 2) score += W_BISHOP_PAIR;
        if (blackB >= 2) score -= W_BISHOP_PAIR;
        return score;
    }

}