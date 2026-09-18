package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
        ChessPosition leftPos = startpos.plussed(rowDir, -1);
        if (null != board.getPiece(leftPos) && focusColor != board.getPiece(leftPos).getTeamColor()) {
            positions.add(leftPos);
        }
        ChessPosition rightPos = startpos.plussed(rowDir, 1);
        if (null != board.getPiece(rightPos) && focusColor != board.getPiece(rightPos).getTeamColor()) {
            positions.add(rightPos);
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
}
