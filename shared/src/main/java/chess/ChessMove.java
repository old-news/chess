package chess;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {
    private ChessPosition startpos, endpos;
    private ChessPiece.PieceType movingPiece;

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        startpos = startPosition;
        endpos = endPosition;
        movingPiece = promotionPiece;
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
        if (movingPiece != ChessPiece.PieceType.PAWN) {
            return null;
        }
        if (endpos.getRow() > startpos.getRow() && endpos.getRow() == 8) {
            return ChessPiece.PieceType.QUEEN;
        } else if (endpos.getRow() < startpos.getRow() && endpos.getRow() == 1) {
            return ChessPiece.PieceType.QUEEN;
        }
        return null;
    }
}
