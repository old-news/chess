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
                moves = getPawnMoves(board, myPosition);
            }
            case KING -> {
                positions = getKingMovePositions(board, myPosition);
            }
            case KNIGHT -> {
                positions = getKnightMovePositions(board, myPosition);
            }
            case ROOK -> {
                positions = getRookMovePositions(board, myPosition);
            }
            case BISHOP -> {
                positions = getBishopMovePositions(board, myPosition);
            }
        }
        for (ChessPosition pos : positions) {
            if (!pos.isInBounds()) continue;
            moves.add(new ChessMove(myPosition, pos, null));
        }
        return moves;
    }

    private List<ChessMove> getPawnMoves(ChessBoard board, ChessPosition myPosition) {
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

    private List<ChessPosition> getKingMovePositions(ChessBoard board, ChessPosition myPosition) {
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
            var pos = myPosition.plussed(adj[0], adj[1]);
            if (board.getPiece(pos) == null || board.getPiece(pos).color != color) {
                positions.add(pos);
            }
        }
        return positions;
    }

    private List<ChessPosition> getKnightMovePositions(ChessBoard board, ChessPosition myPosition) {
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
            var pos = myPosition.plussed(adj[0], adj[1]);
            if (board.getPiece(pos) == null || board.getPiece(pos).color != color) {
                positions.add(pos);
            }
        }
        return positions;
    }

    private List<ChessPosition> getRookMovePositions(ChessBoard board, ChessPosition myPosition) {
        List<ChessPosition> positions = new ArrayList<>();
        for (int i = 1; myPosition.getColumn()+i <= 8; i++) {
            var pos = myPosition.plussed(0, i);
            if (null != board.getPiece(pos) && board.getPiece(pos).getTeamColor() == color) break;
            positions.add(pos);
            if (null != board.getPiece(pos) && board.getPiece(pos).getTeamColor() != color) break;
        }
        for (int i = -1; myPosition.getColumn()+i >= 1; i--) {
            var pos = myPosition.plussed(0, i);
            if (null != board.getPiece(pos) && board.getPiece(pos).getTeamColor() == color) break;
            positions.add(pos);
            if (null != board.getPiece(pos) && board.getPiece(pos).getTeamColor() != color) break;
        }
        for (int i = 1; myPosition.getRow()+i <= 8; i++) {
            var pos = myPosition.plussed(i, 0);
            if (null != board.getPiece(pos) && board.getPiece(pos).getTeamColor() == color) break;
            positions.add(pos);
            if (null != board.getPiece(pos) && board.getPiece(pos).getTeamColor() != color) break;
        }
        for (int i = -1; myPosition.getRow()+i >= 1; i--) {
            var pos = myPosition.plussed(i, 0);
            if (null != board.getPiece(pos) && board.getPiece(pos).getTeamColor() == color) break;
            positions.add(pos);
            if (null != board.getPiece(pos) && board.getPiece(pos).getTeamColor() != color) break;
        }
        return positions;
    }

    private List<ChessPosition> getBishopMovePositions(ChessBoard board, ChessPosition myPosition) {
        List<ChessPosition> positions = new ArrayList<>();
        for (int i = 1; i+myPosition.getRow() <= 8 && i+myPosition.getColumn() <= 8; i++) {
            var pos = myPosition.plussed(i, i);
            if (null != board.getPiece(pos) && board.getPiece(pos).getTeamColor() == color) break;
            positions.add(pos);
            if (null != board.getPiece(pos) && board.getPiece(pos).getTeamColor() != color) break;
        }
        for (int i = 1; myPosition.getRow()-i >= 1 && i+myPosition.getColumn() <= 8; i++) {
            var pos = myPosition.plussed(-i, i);
            if (null != board.getPiece(pos) && board.getPiece(pos).getTeamColor() == color) break;
            positions.add(pos);
            if (null != board.getPiece(pos) && board.getPiece(pos).getTeamColor() != color) break;
        }
        for (int i = 1; i+myPosition.getRow() <= 8 && myPosition.getColumn()-i >= 1; i++) {
            var pos = myPosition.plussed(i, -i);
            if (null != board.getPiece(pos) && board.getPiece(pos).getTeamColor() == color) break;
            positions.add(pos);
            if (null != board.getPiece(pos) && board.getPiece(pos).getTeamColor() != color) break;
        }
        for (int i = 1; myPosition.getRow()-i >= 1 && myPosition.getColumn()-i >= 1; i++) {
            var pos = myPosition.plussed(-i, -i);
            if (null != board.getPiece(pos) && board.getPiece(pos).getTeamColor() == color) break;
            positions.add(pos);
            if (null != board.getPiece(pos) && board.getPiece(pos).getTeamColor() != color) break;
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
