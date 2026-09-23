package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChessMoveCalculator {
    private ChessBoard board;
    private ChessPosition focusStartpos;
    private ChessPiece focusPiece;
    private ChessGame.TeamColor focusColor;

    public ChessMoveCalculator(ChessBoard board) {
        this.board = board;
    }

    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    public Collection<ChessMove> getMoves(ChessPosition startpos) {
        focusStartpos = startpos;
        focusPiece = board.getPiece(startpos);
        focusColor = focusPiece.getTeamColor();
        List<ChessPosition> positions = new ArrayList<>();
        List<ChessMove> moves = new ArrayList<>();
        switch (focusPiece.getPieceType()) {
            case PAWN -> {
                moves = getPawnMoves(startpos);
            }
            case KING -> {
                positions = getKingMovePositions(startpos);
            }
            case KNIGHT -> {
                positions = getKnightMovePositions(startpos);
            }
            case ROOK -> {
                positions = getRookMovePositions(startpos);
            }
            case BISHOP -> {
                positions = getBishopMovePositions(startpos);
            }
            case QUEEN -> {
                positions = getQueenMovePositions(startpos);
            }
        }
        for (ChessPosition pos : positions) {
            if (!pos.isInBounds()) continue;
            moves.add(new ChessMove(startpos, pos, null));
        }
        return moves;
    }

    private List<ChessMove> getPawnMoves(ChessPosition startpos) {
        List<ChessPosition> positions = new ArrayList<>();
        int rowDir = (focusColor == ChessGame.TeamColor.WHITE) ? 1 : -1;
        ChessPosition frontPos = startpos.plussed(rowDir, 0);
        if (null == board.getPiece(frontPos)) {
            positions.add(frontPos);
            ChessPosition front2Pos = startpos.plussed(rowDir*2, 0);
            if (null == board.getPiece(front2Pos)) {
                if (rowDir == 1 && startpos.getRow() == 2) {
                    positions.add(front2Pos);
                } else if (rowDir == -1 && startpos.getRow() == 7) {
                    positions.add(front2Pos);
                }
            }
        }
        ChessPosition leftFrontPos = startpos.plussed(rowDir, -1);
        if (null != board.getPiece(leftFrontPos) && focusColor != board.getPiece(leftFrontPos).getTeamColor()) {
            positions.add(leftFrontPos);
        }

        ChessPosition rightFrontPos = startpos.plussed(rowDir, 1);
        if (null != board.getPiece(rightFrontPos) && focusColor != board.getPiece(rightFrontPos).getTeamColor()) {
            positions.add(rightFrontPos);
        }
        List<ChessMove> moves = new ArrayList<>();
        for (var pos : positions) {
            if (pos.getRow() < startpos.getRow() && pos.getRow() == 1 || pos.getRow() > startpos.getRow() && pos.getRow() == 8) {
                moves.add(new ChessMove(startpos, pos, ChessPiece.PieceType.QUEEN));
                moves.add(new ChessMove(startpos, pos, ChessPiece.PieceType.BISHOP));
                moves.add(new ChessMove(startpos, pos, ChessPiece.PieceType.ROOK));
                moves.add(new ChessMove(startpos, pos, ChessPiece.PieceType.KNIGHT));
            } else {
                moves.add(new ChessMove(startpos, pos, null));
            }
        }
        return moves;
    }

    private List<ChessPosition> getKingMovePositions(ChessPosition startpos) {
        List<ChessPosition> positions = new ArrayList<>();
        int[][] adjustments = new int[][] {
                {0, 1},
                {0, -1},
                {1, 0},
                {-1, 0},
                {1, 1},
                {-1, 1},
                {1, -1},
                {-1, -1}
        };
        for (var adj : adjustments) {
            var pos = startpos.plussed(adj[0], adj[1]);
            if (board.getPiece(pos) == null || board.getPiece(pos).getTeamColor() != focusColor) {
                positions.add(pos);
            }
        }
        return positions;
    }

    private List<ChessPosition> getKnightMovePositions(ChessPosition startpos) {
        List<ChessPosition> positions = new ArrayList<>();
        int[][] adjs = new int[][]{
                {1, 2},
                {1, -2},
                {-1, 2},
                {-1, -2},
                {2, 1},
                {2, -1},
                {-2, 1},
                {-2, -1}
        };
        for (var adj : adjs) {
            var pos = startpos.plussed(adj[0], adj[1]);
            if (board.getPiece(pos) == null || board.getPiece(pos).getTeamColor() != focusColor) {
                positions.add(pos);
            }
        }
        return positions;
    }

    private List<ChessPosition> getRookMovePositions(ChessPosition startpos) {
        List<ChessPosition> positions = new ArrayList<>();
        for (int i = 1; startpos.getColumn()+i <= 8; i++) {
            var pos = startpos.plussed(0, i);
            if (null != board.getPiece(pos) && board.getPiece(pos).getTeamColor() == focusColor) break;
            positions.add(pos);
            if (null != board.getPiece(pos) && board.getPiece(pos).getTeamColor() != focusColor) break;
        }
        for (int i = -1; startpos.getColumn()+i >= 1; i--) {
            var pos = startpos.plussed(0, i);
            if (null != board.getPiece(pos) && board.getPiece(pos).getTeamColor() == focusColor) break;
            positions.add(pos);
            if (null != board.getPiece(pos) && board.getPiece(pos).getTeamColor() != focusColor) break;
        }
        for (int i = 1; startpos.getRow()+i <= 8; i++) {
            var pos = startpos.plussed(i, 0);
            if (null != board.getPiece(pos) && board.getPiece(pos).getTeamColor() == focusColor) break;
            positions.add(pos);
            if (null != board.getPiece(pos) && board.getPiece(pos).getTeamColor() != focusColor) break;
        }
        for (int i = -1; startpos.getRow()+i >= 1; i--) {
            var pos = startpos.plussed(i, 0);
            if (null != board.getPiece(pos) && board.getPiece(pos).getTeamColor() == focusColor) break;
            positions.add(pos);
            if (null != board.getPiece(pos) && board.getPiece(pos).getTeamColor() != focusColor) break;
        }
        return positions;
    }

    private List<ChessPosition> getBishopMovePositions(ChessPosition startpos) {
        List<ChessPosition> positions = new ArrayList<>();
        for (int i = 1; startpos.plussed(i, i).isInBounds(); i++) {
            var pos = startpos.plussed(i, i);
            if (null != board.getPiece(pos) && board.getPiece(pos).getTeamColor() == focusColor) break;
            positions.add(pos);
            if (null != board.getPiece(pos) && board.getPiece(pos).getTeamColor() != focusColor) break;
        }
        for (int i = 1; startpos.plussed(-i, i).isInBounds(); i++) {
            var pos = startpos.plussed(-i, i);
            if (null != board.getPiece(pos) && board.getPiece(pos).getTeamColor() == focusColor) break;
            positions.add(pos);
            if (null != board.getPiece(pos) && board.getPiece(pos).getTeamColor() != focusColor) break;
        }
        for (int i = 1; startpos.plussed(i, -i).isInBounds(); i++) {
            var pos = startpos.plussed(i, -i);
            if (null != board.getPiece(pos) && board.getPiece(pos).getTeamColor() == focusColor) break;
            positions.add(pos);
            if (null != board.getPiece(pos) && board.getPiece(pos).getTeamColor() != focusColor) break;
        }
        for (int i = 1; startpos.plussed(-i, -i).isInBounds(); i++) {
            var pos = startpos.plussed(-i, -i);
            if (null != board.getPiece(pos) && board.getPiece(pos).getTeamColor() == focusColor) break;
            positions.add(pos);
            if (null != board.getPiece(pos) && board.getPiece(pos).getTeamColor() != focusColor) break;
        }
        return positions;
    }

    private List<ChessPosition> getQueenMovePositions(ChessPosition startpos) {
        List<ChessPosition> positions = new ArrayList<>();
        List<ChessPosition> rookishPositions = getRookMovePositions(startpos);
        List<ChessPosition> bishopishPositions = getBishopMovePositions(startpos);
        positions.addAll(rookishPositions);
        positions.addAll(bishopishPositions);
        return positions;
    }

    public ChessMove getEnPassant(ChessPosition startpos, ChessMove lastmove) {
	    var piece = board.getPiece(startpos);
	    if (null == piece || ChessPiece.PieceType.PAWN != piece.getPieceType()) return null;
	    var color = piece.getTeamColor();
	    int rowDir = (focusColor == ChessGame.TeamColor.WHITE) ? 1 : -1;
	    if ((color == ChessGame.TeamColor.WHITE && startpos.getRow() != 5) || (color == ChessGame.TeamColor.BLACK && startpos.getRow() != 4)) return null;
	    var lastMovedPiece = board.getPiece(lastmove.getEndPosition());
	    if (lastMovedPiece.getPieceType() != ChessPiece.PieceType.PAWN) return null;
	    if (Math.abs(lastmove.getEndPosition().getRow() - lastmove.getStartPosition().getRow()) != 2) return null;
	    if (Math.abs(startpos.getColumn() - lastmove.getEndPosition().getColumn()) != 1) return null;
	    if ((lastMovedPiece.getTeamColor() == ChessGame.TeamColor.WHITE && lastmove.getStartPosition().getRow() != 2) ||
		(lastMovedPiece.getTeamColor() == ChessGame.TeamColor.BLACK && lastmove.getStartPosition().getRow() != 7)
	    ) {
		    return null;
	    }
	    ChessPosition enPassantEnd = new ChessPosition(startpos.getRow() + rowDir, lastmove.getEndPosition().getColumn());
	    ChessMove enPassantMove = new ChessMove(startpos, enPassantEnd, null);
	    return enPassantMove;
    }

    public Collection<ChessMove> getCastleMoves(ChessPosition startpos, ChessGame game) {
	    var piece = board.getPiece(startpos);
	    if (null == piece) return new ArrayList<>();
	    var color = piece.getTeamColor();
	    if (piece.getPieceType() != ChessPiece.PieceType.KING) return new ArrayList<>();
	    if (piece.hasPieceMoved()) return new ArrayList<>();
	    var rookPositions = board.search(new ChessPiece(color, ChessPiece.PieceType.ROOK));
	    if (rookPositions.isEmpty()) return new ArrayList<>();
	    if (game.isInCheck(color)) return new ArrayList<>();
	    Collection<ChessMove> castleMoves = new ArrayList<>();
	    for (int i = 0; i < rookPositions.size(); i++) {
		    var rookPos = rookPositions.get(i);
		    var rook = board.getPiece(rookPos);
		    if (rook.hasPieceMoved()) continue;
		    int dir = (startpos.getColumn() > rookPos.getColumn()) ? 1 : -1;
		    boolean castlingError = false;
		    for (int col = rookPos.getColumn(); col != startpos.getColumn(); col+=dir) {
			    var pos = new ChessPosition(rookPos.getRow(), col);
			    if (Math.abs(startpos.getColumn() - col) <= 2 && game.isPositionAttacked(color, pos)) castlingError = true;
			    if (col != rookPos.getColumn() && board.getPiece(pos) != null) castlingError = true;
			    if (castlingError) break;
		    }
		    if (!castlingError) {
		    	ChessMove move = new ChessMove(startpos, startpos.plussed(0, -2*dir), null);
		    	castleMoves.add(move);
		    }
	    }
	    return castleMoves;
    }
}
