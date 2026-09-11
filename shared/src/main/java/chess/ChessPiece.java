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

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        color = pieceColor;
        this.type = type;
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
//        if (board.empty()) {
//            throw new Exception("tried to get moves on empty board");
//        }
        List<ChessPosition> positions = new ArrayList<>();
        switch(type) {
            case PAWN -> {
                positions = getPawnPotentialMovePositions(board, myPosition);
            }
        }
        List<ChessMove> moves = new ArrayList<>();
        for (ChessPosition pos : positions) {
            moves.add(new ChessMove(myPosition, pos, type));
        }
        return moves;
    }

    private List<ChessPosition> getPawnPotentialMovePositions(ChessBoard board, ChessPosition myPosition) {
        List<ChessPosition> positions = new ArrayList<>();
        int rowDir = (color == ChessGame.TeamColor.WHITE) ? 1 : -1;
        ChessPosition frontPos = myPosition.plussed(rowDir, 0);
        if (null == board.getPiece(frontPos)) {
            positions.add(frontPos);
        }
        ChessPosition leftPos = myPosition.plussed(rowDir, -1);
        if (null != board.getPiece(leftPos) && color != board.getPiece(leftPos).getTeamColor()) {
            positions.add(leftPos);
        }
        ChessPosition rightPos = myPosition.plussed(rowDir, 1);
        if (null != board.getPiece(rightPos) && color != board.getPiece(rightPos).getTeamColor()) {
            positions.add(rightPos);
        }
        return positions;
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
        return (int) toString().charAt(0);
    }
}
