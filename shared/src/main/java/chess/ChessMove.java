package chess;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {
    private final ChessPosition startpos, endpos;
    private final ChessPiece.PieceType promotionPiece;

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        startpos = startPosition;
        endpos = endPosition;
        this.promotionPiece = promotionPiece;
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return startpos;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return endpos;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        return promotionPiece;
    }

    public String toString() {
        String piecestr = "e";
        if (null != promotionPiece) {
            piecestr = promotionPiece.toString();
        }
        return piecestr + " " + startpos.toString() + " " + endpos.toString();
    }

    public boolean equals(Object other) {
        if (this == other) return true;
        if (null == other || !getClass().equals(other.getClass())) return false;
	    return toString().equals(other.toString());
    }

    public int hashCode() {
        return toString().hashCode();
    }
}
