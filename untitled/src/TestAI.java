import java.util.List;

public class TestAI {
    public static void main(String[] args) {
        ChessPiece[][] board = new ChessPiece[8][8];

        board[7][4] = new ChessPiece(PieceColor.WHITE, PieceType.KING);
        board[0][4] = new ChessPiece(PieceColor.BLACK, PieceType.KING);

        board[6][4] = new ChessPiece(PieceColor.WHITE, PieceType.QUEEN);
        board[1][4] = new ChessPiece(PieceColor.BLACK, PieceType.ROOK);

        GameState root = new GameState(board, PieceColor.WHITE);

        // In số nước đi hợp lệ của 2 bên
        List<Move> whiteMoves = root.generateAllLegalMoves(PieceColor.WHITE);
        List<Move> blackMoves = root.generateAllLegalMoves(PieceColor.BLACK);

        System.out.println("White moves = " + whiteMoves.size());
        System.out.println("Black moves = " + blackMoves.size());

        // Test minimax và alpha-beta
        ChessAI ai = new ChessAI(PieceColor.WHITE);
        int depth = 3;

        int mm = ai.minimax(true, root, depth);
        int ab = ai.alphaBeta(true, root, depth, Integer.MIN_VALUE, Integer.MAX_VALUE);

        System.out.println("Minimax value  = " + mm);
        System.out.println("AlphaBeta value= " + ab);
        System.out.println("Same result?   = " + (mm == ab));

        // Test findBestMove (nước đi AI chọn)
        Move best = ai.findBestMove(root, depth);

            System.out.println("Best move: (" + best.fromX + "," + best.fromY + ") -> (" + best.toX + "," + best.toY + ")");

    }
}

