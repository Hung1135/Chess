public class Move {
    public int fromX, fromY;
    public int toX, toY;

    public ChessPiece movedPiece;

    public Move(int fromX, int fromY, int toX, int toY, ChessPiece movedPiece) {
        this.fromX = fromX;
        this.fromY = fromY;
        this.toX = toX;
        this.toY = toY;
        this.movedPiece = movedPiece;
    }
}
