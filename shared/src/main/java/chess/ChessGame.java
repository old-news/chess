package chess;

import org.junit.platform.commons.util.BlacklistedExceptions;

import javax.swing.plaf.basic.BasicLookAndFeel;
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
    private List<ChessMove> movesMade;

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        turn = TeamColor.WHITE;
	movesMade = new ArrayList<>();
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
        Collection<ChessMove> potentialMoves = calculator.getMoves(startPosition, lastMove());
        Collection<ChessMove> castleMoves = calculator.getCastleMoves(startPosition, this);
        potentialMoves.addAll(castleMoves);
        Collection<ChessMove> movesWhichEscapeCheck = new ArrayList<>();
        for (var move : potentialMoves) {
            if (!moveEndsWithCheck(move)) {
                movesWhichEscapeCheck.add(move);
            }
        }
        System.out.println("valid moves: " + movesWhichEscapeCheck);
        return movesWhichEscapeCheck;
    }

    public Collection<ChessMove> allTeamMoves(TeamColor color) {
	    Collection<ChessPosition> teamPositions = board.getTeam(color);
	    Collection<ChessMove> moves = new ArrayList<>();
	    for (var pos : teamPositions) {
		    var piece = board.getPiece(pos);
		    if (null == piece) continue;
		    moves.addAll(piece.pieceMoves(board, pos));
	    }
	    return moves;
    }

    private Collection<ChessMove> allValidTeamMoves(TeamColor color) {
	    Collection<ChessMove> moves = allTeamMoves(color);
	    Collection<ChessMove> validMoves = new ArrayList<>();
	    for (var move : moves) {
		    if (!moveEndsWithCheck(move)) {
			    validMoves.add(move);
		    }
	    }
	    return validMoves;
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */

    public void makeMove(ChessMove move) throws InvalidMoveException {
	    boolean isValid = isMoveValid(move);
	    if (!isValid) throw new InvalidMoveException();
	    var startpos = move.getStartPosition();
	    var endpos = move.getEndPosition();
	    ChessPiece movingPiece = (null == move.getPromotionPiece()) ? board.getPiece(startpos) : new ChessPiece(turn, move.getPromotionPiece());

	    movingPiece.registerMove();
	    var killedPiece = board.getPiece(endpos);
	    board.removePiece(startpos);
	    board.addPiece(endpos, movingPiece);

	    checkDoEnPassantSpecial(move, killedPiece);
	    checkDoCastleSpecial(move);

	    turn = (turn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
	    movesMade.add(move);

    }

    private void checkDoEnPassantSpecial(ChessMove move, ChessPiece killedPiece) {
	    var endpos = move.getEndPosition();
	    var startpos = move.getStartPosition();
	    var movingPiece = board.getPiece(endpos);

	    var rowDir = endpos.getRow() - startpos.getRow();
	    var enPassantAttackedPos = endpos.plussed(-rowDir, 0);
	    var potentialEnPassantVictim = board.getPiece(enPassantAttackedPos);
	    if (movingPiece.getPieceType() == ChessPiece.PieceType.PAWN && potentialEnPassantVictim != null && potentialEnPassantVictim.getPieceType() == ChessPiece.PieceType.PAWN && null == killedPiece) {
		    System.out.println("Removing piece due to enpassant");
		    board.removePiece(enPassantAttackedPos);
	    }
    }

    private void checkDoCastleSpecial(ChessMove move) {
	    var endpos = move.getEndPosition();
	    var startpos = move.getStartPosition();
	    var movingPiece = board.getPiece(endpos);

	    if (movingPiece.getPieceType() == ChessPiece.PieceType.KING && Math.abs(startpos.getColumn() - endpos.getColumn()) == 2) {
		    System.out.println("Castling...");
		    ChessPosition rookOldPos, rookNewPos;
		    if (startpos.getColumn() > endpos.getColumn()) {
			    rookOldPos = new ChessPosition(startpos.getRow(), 1);
			    rookNewPos = new ChessPosition(startpos.getRow(), startpos.getColumn() - 1);
		    } else {
			    rookOldPos = new ChessPosition(startpos.getRow(), 8);
			    rookNewPos = new ChessPosition(startpos.getRow(), startpos.getColumn() + 1);
		    }
		    var rook = board.removePiece(rookOldPos);
		    rook.registerMove();
		    board.addPiece(rookNewPos, rook);
	    }
    }

    private boolean isMoveValid(ChessMove move) {
	    if (null == move) return false;
	    var piece = board.getPiece(move.getStartPosition());
	    if (null == piece || piece.getTeamColor() != turn) return false;
	    var validPieceMoves = validMoves(move.getStartPosition());
	    if (!validPieceMoves.contains(move)) return false;
	    return true;
    }

    public boolean moveEndsWithCheck(ChessMove move) {
        var movingPiece = board.getPiece(move.getStartPosition());
        var endpiece = board.getPiece(move.getEndPosition());
	makeMoveNoInvalidChecks(move);
        var endedInCheck = isInCheck(movingPiece.getTeamColor());
	reverseMove(move, endpiece);
        return endedInCheck;
    }

    private void reverseMove(ChessMove move, ChessPiece killedPiece) {
        var piece = board.getPiece(move.getEndPosition());
        board.addPiece(move.getEndPosition(), killedPiece);
        board.addPiece(move.getStartPosition(), piece);
    }

    public void makeMoveNoInvalidChecks(ChessMove move) {
        var piece = board.getPiece(move.getStartPosition());
        board.removePiece(move.getStartPosition());
        board.addPiece(move.getEndPosition(), piece);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        var teamKing = new ChessPiece(teamColor, ChessPiece.PieceType.KING);
        ChessPosition kingpos = board.search(teamKing).get(0);
	return isPositionAttacked(teamColor, kingpos);
    }

    public boolean isPositionAttacked(TeamColor teamColor, ChessPosition pos) {
        var enemyColor = (teamColor == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
        var enemyMoves = allTeamMoves(enemyColor);
        for (var move : enemyMoves) {
            var endpos = move.getEndPosition();
            if (endpos.equals(pos)) return true;
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
	    return isInCheck(teamColor) && allValidTeamMoves(teamColor).isEmpty();
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
	    return !isInCheck(teamColor) && allValidTeamMoves(teamColor).isEmpty();
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

    private ChessMove lastMove() {
	    return (movesMade.isEmpty()) ? null : movesMade.getLast();
    }
}
