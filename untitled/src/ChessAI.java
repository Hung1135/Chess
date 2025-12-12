import java.util.List;

public class ChessAI {
    private PieceColor aiColor;
    private Move bestMove;

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

    public int heuristic(GameState state) {
        ChessPiece[][] board = state.getBoard();
        int score = 0;

        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                ChessPiece p = board[x][y];
                if (p == null) continue;

                int value = getPieceValue(p);

                if (p.color == PieceColor.WHITE) {
                    score += value;   // cộng cho trắng
                } else {
                    score -= value;   // trừ cho đen
                }
            }
        }

        return score;
    }

    public int minimax(boolean maxmin, GameState state, int depth) {
        // cơ sở
        // Nếu đã tới độ sâu tối đa hoặc ván cờ đã kết thúc -> trả về heuristic
        if (depth == 0 || state.isOver()) {
            return heuristic(state);
        }

        // node max
        //temp điểm tốt nhất ta thấy cho hiện tại
        //value điểm mà ta sẽ nhận được nếu chọn node con này
        if (maxmin == true) { // bên MAX
            int temp = Integer.MIN_VALUE; // tương đương -vô cực

            for (GameState child : state.generateChildStates()) {
            // đệ quy
                int value = minimax(false, child, depth - 1);

                if (value > temp) {
                    temp = value;
                }
            }

            return temp;
        }

        // node min
        if (maxmin == false) {
            int temp = Integer.MAX_VALUE;

            for (GameState child : state.generateChildStates()) {

                int value = minimax(true, child, depth - 1);

                if (value < temp) {
                    temp = value;
                }
            }

            return temp;
        }
        return heuristic(state);
    }
    private int getPieceValue(ChessPiece p) {
        switch (p.type) {
            case PAWN:
                return 100;
            case KNIGHT:
                return 300;
            case BISHOP:
                return 300;
            case ROOK:
                return 500;
            case QUEEN:
                return 900;
            case KING:
                return 10000;
            default:
                return 0;
        }
    }

    public int alphaBeta(boolean maxmin, GameState state, int depth,
                         int alpha, int beta) {

        // Cơ sở
        if (depth == 0 || state.isOver()) {
            return heuristic(state);
        }

        // node max
        if (maxmin == true) {
            int temp = Integer.MIN_VALUE;

            for (GameState child : state.generateChildStates()) {
                int value = alphaBeta(false, child, depth - 1, alpha, beta);

                if (value > temp) {
                    temp = value;
                }
                // cập nhật alpha (giá trị tốt nhất mà MAX đã đạt được)
                if (value > alpha) {
                    alpha = value;
                }

                // nếu beta <= alpha -> cắt tỉa, không cần xét tiếp các child còn lại
                if (beta <= alpha) {
                    break;
                }
            }

            return temp;
        }

        // node min
        else {
            int temp = Integer.MAX_VALUE;

            for (GameState child : state.generateChildStates()) {
                int value = alphaBeta(true, child, depth - 1, alpha, beta);

                if (value < temp) {
                    temp = value;
                }
                if (value < beta) {
                    beta = value;
                }
                // nếu beta <= alpha -> cắt tỉa
                if (beta <= alpha) {
                    break;
                }
            }

            return temp;
        }
    }

    public Move findBestMove(GameState root, int depth) {
        bestMove = null;

        // Lấy danh sách tất cả nước đi hợp lệ
        List<Move> moves = root.generateAllLegalMoves(aiColor);

        if (moves.isEmpty()) {
            return null;
        }
        // Nếu AI là bên MAX (ví dụ heuristic >0 có lợi cho WHITE)
        if (aiColor == PieceColor.WHITE) {
            int bestValue = Integer.MIN_VALUE;

            for (Move m : moves) {
                // Tạo state con bằng cách clone và apply move
                GameState child = root.clone();
                child.applyMove(m);
                child.switchTurn(); // đổi lượt cho đối thủ

                // Gọi minimaxAlphaBeta cho node con
                int value = alphaBeta(
                        false,             // sau khi AI (MAX) đi xong -> tới lượt MIN
                        child,
                        depth - 1,
                        Integer.MIN_VALUE, // alpha ban đầu
                        Integer.MAX_VALUE  // beta ban đầu
                );

                if (value > bestValue) {
                    bestValue = value;
                    bestMove = m;
                }
            }

        } else { // AI là bên MIN (BLACK)
            int bestValue = Integer.MAX_VALUE;

            for (Move m : moves) {
                GameState child = root.clone();
                child.applyMove(m);
                child.switchTurn(); // tới lượt đối thủ (MAX)

                int value = alphaBeta(
                        true,              // sau khi MIN đi -> tới lượt MAX
                        child,
                        depth - 1,
                        Integer.MIN_VALUE,
                        Integer.MAX_VALUE
                );

                if (value < bestValue) {
                    bestValue = value;
                    bestMove = m;
                }
            }
        }

        return bestMove;
    }


}
