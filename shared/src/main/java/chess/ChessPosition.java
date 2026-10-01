package chess;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition extends DefaultEqualsHashcode {
    private int row, col;

    public ChessPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left column
     */
    public int getColumn() {
        return col;
    }

    public String toString() {
        return row + "," + col;
    }

    public ChessPosition plussed(int rowadj, int coladj) {
        return new ChessPosition(row + rowadj, col + coladj);
    }

    public boolean isInBounds() {
        return (row <= 8 && row >= 1 && col <= 8 && col >= 1);
    }
}
