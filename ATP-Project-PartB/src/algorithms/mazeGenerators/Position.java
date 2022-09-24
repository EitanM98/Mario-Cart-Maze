package algorithms.mazeGenerators;

import java.io.Serializable;
import java.util.Objects;

public class Position implements Serializable {
    private int row;
    private int col;

    public Position(int row, int col){
        this.row=row;
        this.col=col;
    }

    public int getRowIndex() {
        return row;
    }

    public int getColumnIndex() {
        return col;
    }

    public void setRow(int row) {
        if(row<0)
            return;
        this.row = row;
    }

    public void setCol(int col) {
        if(col<0)
            return;
        this.col = col;
    }

    /**
     * Overriding print method to be able to print Position objects
     * @return {row,col} as string
     */
    @Override
    public String toString() {
        return "{"+row +"," + col + '}';
    }

    /**
     * Checks positions by row and col indexes
     * @param other
     * @return true if the positions are equal, false otherwise
     */
    @Override
    public boolean equals(Object other){
        if(!(other instanceof Position)){
            return false;
        }
        return (this.row==((Position) other).row && this.col==((Position) other).col);
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }
}
