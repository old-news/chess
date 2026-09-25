package chess;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard implements Cloneable {

	private ChessPiece[][] board = new ChessPiece[8][8];

    public ChessBoard() {
        return;
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        if (!position.isInBounds()) return;
        board[position.getRow()-1][position.getColumn()-1] = piece;
    }

    public ChessPiece removePiece(ChessPosition position) {
	    var target = getPiece(position);
	    board[position.getRow()-1][position.getColumn()-1] = null;
	    return target;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        if (!position.isInBounds()) return null;
        return board[position.getRow()-1][position.getColumn()-1];
    }

    public List<ChessPosition> search(ChessPiece piece) {
	List<ChessPosition> found = new ArrayList<>();
        if (null == piece) return null;
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                var pos = new ChessPosition(row, col);
                var otherPiece = getPiece(pos);
                if (piece.equals(otherPiece)) {
			found.add(pos);
		}
            }
        }
        return found;
    }

    public ChessPosition getKingPos(ChessGame.TeamColor color) {
	    var king = new ChessPiece(color, ChessPiece.PieceType.KING);
	    var maybePos = search(king);
	    if (maybePos.isEmpty()) return null;
	    return maybePos.get(0);
    }

    public Collection<ChessPosition> getTeam(ChessGame.TeamColor color) {
	    List<ChessPosition> team = new ArrayList<>();
	    for (int row = 1; row <= 8; row++) {
		    for (int col = 1; col <= 8; col++) {
			    var pos = new ChessPosition(row, col);
			    var piece = getPiece(pos);
			    if (null == piece || piece.getTeamColor() != color) continue;
			    team.add(pos);
		    }
	    }
	    return team;
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
	addPiece(new ChessPosition(1, 1), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
	addPiece(new ChessPosition(1, 2), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
	addPiece(new ChessPosition(1, 3), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
	addPiece(new ChessPosition(1, 4), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN));
	addPiece(new ChessPosition(1, 5), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING));
	addPiece(new ChessPosition(1, 6), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
	addPiece(new ChessPosition(1, 7), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
	addPiece(new ChessPosition(1, 8), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
        for (int i = 0; i <= 8; i++) {
		addPiece(new ChessPosition(2, i), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
		addPiece(new ChessPosition(7, i), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        }
	addPiece(new ChessPosition(8, 1), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
	addPiece(new ChessPosition(8, 2), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
	addPiece(new ChessPosition(8, 3), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
	addPiece(new ChessPosition(8, 4), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN));
	addPiece(new ChessPosition(8, 5), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING));
	addPiece(new ChessPosition(8, 6), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
	addPiece(new ChessPosition(8, 7), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
	addPiece(new ChessPosition(8, 8), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
        for (int i = 3; i <= 6; i++) {
            for (int j = 1; j <= 8; j++) {
		removePiece(new ChessPosition(i, j));
                // board[i][j] = null;
            }
        }
    }

    public String toString() {
        String retval = "";
        for (int row = 8; row >= 1; row--) {
            for (int col = 1; col <= 8; col++) {
                var pos = new ChessPosition(row, col);
                var piece = getPiece(pos);
                if (null == piece) {
                    retval += '.';
                    continue;
                }
                retval += piece.toString();
            }
            retval += '\n';
        }
        return retval;
    }

    public boolean equals(Object other) {
        if (this == other) return true;
        if (null == other || getClass() != other.getClass()) return false;
        return toString().equals(other.toString());
    }

    public int hashCode() {
        return toString().hashCode();
    }

    public boolean empty() {
        for (var row : board) {
            for (var val : board) {
                if (null != val) return false;
            }
        }
        return true;
    }

    public ChessBoard clone() {
        var newboard = new ChessBoard();
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                var pos = new ChessPosition(row, col);
                var piece = getPiece(pos);
                newboard.addPiece(pos, piece);
            }
        }
        return newboard;
    }
}
