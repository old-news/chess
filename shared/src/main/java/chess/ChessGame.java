package chess;

import org.junit.platform.commons.util.BlacklistedExceptions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board;
    private ChessGame.TeamColor turn;

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        turn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turn;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        turn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        var calculator = new ChessMoveCalculator(board);
        Collection<ChessMove> potentialMoves = calculator.getMoves(startPosition);
        return potentialMoves;
    }

    public Collection<ChessMove> allValidTeamMoves(TeamColor color) {
        Collection<ChessMove> moves = new ArrayList<>();
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                var pos = new ChessPosition(row, col);
                var piece = board.getPiece(pos);
                if (null == piece || piece.getTeamColor() != color) continue;
                moves.addAll(piece.pieceMoves(board, pos));
            }
        }
        return moves;
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        System.out.println("makemove: " + move);
        if (null == move) throw new InvalidMoveException("`null` move passed to ChessGame.makeMove");
        if (!move.getStartPosition().isInBounds()) throw new InvalidMoveException("Invalid start pos");
        if (!move.getEndPosition().isInBounds()) throw new InvalidMoveException("Invalid end pos");
        var startpos = move.getStartPosition();
        var piece = board.getPiece(startpos);
        if (null == piece) {
            throw new InvalidMoveException("Tried to move from empty square");
        }
        if (piece.getTeamColor() != turn) {
            throw new InvalidMoveException("Tried to move out of turn");
        }
        var validMoves = validMoves(startpos);
        if (!validMoves.contains(move)) {
            throw new InvalidMoveException("Invalid move for piece");
        }
        var wasInCheck = isInCheck(piece.getTeamColor());
        board.addPiece(move.getStartPosition(), null);
        if (null != move.getPromotionPiece()) {
            var promotionPiece = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
            board.addPiece(move.getEndPosition(), promotionPiece);
        } else {
            board.addPiece(move.getEndPosition(), piece);
        }
        turn = (turn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
        if (wasInCheck) {
            if (isInCheck(piece.getTeamColor())) {
                reverseMove(move);
                throw new InvalidMoveException("Illegal move in check");
            }
        }
    }

    private void reverseMove(ChessMove move) {
        var piece = board.getPiece(move.getEndPosition());
        board.addPiece(move.getEndPosition(), null);
        board.addPiece(move.getStartPosition(), piece);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        var enemyColor = (teamColor == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
        var enemyMoves = allValidTeamMoves(enemyColor);
        var teamKing = new ChessPiece(teamColor, ChessPiece.PieceType.KING);
        var kingpos = board.search(teamKing);
        for (var move : enemyMoves) {
            var endpos = move.getEndPosition();
            if (endpos.equals(kingpos)) return true;
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    public String toString() {
        return board.toString() + "\n" + ((turn == TeamColor.WHITE) ? "W" : "B");
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
