package chess;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final ChessGame.TeamColor color;
    private final ChessPiece.PieceType type;
    private ChessMove lastMove;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        color = pieceColor;
        this.type = type;
        lastMove = null;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return color;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        var calculator = new ChessMoveCalculator(board);
        return calculator.getMoves(myPosition);
    }

    public void setLastMove(ChessMove lastmove) {
        this.lastMove = lastmove;
    }

    public String toString() {
        String retval = "";
        switch (type) {
            case PAWN:
                retval = "p";
                break;
            case ROOK:
                retval = "r";
                break;
            case KNIGHT:
                retval = "n";
                break;
            case BISHOP:
                retval = "b";
                break;
            case QUEEN:
                retval = "q";
                break;
            case KING:
                retval = "k";
                break;
        }
        if (color == ChessGame.TeamColor.WHITE) {
            retval = retval.toUpperCase();
        }
        return retval;
    }

    public boolean equals(Object other) {
        if (this == other) return true;
        if (null == other) return false;
        if (!getClass().equals(other.getClass())) return false;
        return toString().equals(other.toString());
    }

    public int hashCode() {
        return toString().hashCode();
    }
}
