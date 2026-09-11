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
        List<ChessMove> moves = new ArrayList<>();
        switch (type) {
            case PAWN -> {
                moves = getPawnLegalMoves(board, myPosition);
            }
        }
        for (ChessPosition pos : positions) {
            moves.add(new ChessMove(myPosition, pos, null));
        }
        return moves;
    }

    private List<ChessMove> getPawnLegalMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessPosition> positions = new ArrayList<>();
        int rowDir = (color == ChessGame.TeamColor.WHITE) ? 1 : -1;
        ChessPosition frontPos = myPosition.plussed(rowDir, 0);
        if (null == board.getPiece(frontPos)) {
            positions.add(frontPos);
            ChessPosition front2Pos = myPosition.plussed(rowDir*2, 0);
            if (null == board.getPiece(front2Pos)) {
                if (rowDir == 1 && myPosition.getRow() == 2) {
                    positions.add(front2Pos);
                } else if (rowDir == -1 && myPosition.getRow() == 7) {
                    positions.add(front2Pos);
                }
            }
        }
        ChessPosition leftPos = myPosition.plussed(rowDir, -1);
        if (null != board.getPiece(leftPos) && color != board.getPiece(leftPos).getTeamColor()) {
            positions.add(leftPos);
        }
        ChessPosition rightPos = myPosition.plussed(rowDir, 1);
        if (null != board.getPiece(rightPos) && color != board.getPiece(rightPos).getTeamColor()) {
            positions.add(rightPos);
        }
        List<ChessMove> moves = new ArrayList<>();
        for (var pos : positions) {
            if (pos.getRow() < myPosition.getRow() && pos.getRow() == 1 || pos.getRow() > myPosition.getRow() && pos.getRow() == 8) {
                moves.add(new ChessMove(myPosition, pos, PieceType.QUEEN));
                moves.add(new ChessMove(myPosition, pos, PieceType.BISHOP));
                moves.add(new ChessMove(myPosition, pos, PieceType.ROOK));
                moves.add(new ChessMove(myPosition, pos, PieceType.KNIGHT));
            } else {
                moves.add(new ChessMove(myPosition, pos, null));
            }
        }
        return moves;
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
