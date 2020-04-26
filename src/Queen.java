import java.io.Serializable;
import java.util.ArrayList;

public class Queen extends PrimaryPiece implements Chessman, Serializable{
    public Queen(char color, Coordinates coordinates,boolean move) {
        super(color, coordinates, move);
        if(color=='w'){
            setImage("file/WhiteQueen.png");
        }else{
            setImage("file/BlackQueen.png");
        }
    }

    @Override
    public char getSymbol() {
        return (this.getColor() == 'w') ? 'H' : 'h';
    }

    public final ArrayList<Coordinates> possibleMoves(Board board) {
        char[][] table = board.getBoard();
        ArrayList<Coordinates> result = new ArrayList<>();
        result.addAll(possibleMovesRowAndColumnOrDiagonals(table,1,1));
        result.addAll(possibleMovesRowAndColumnOrDiagonals(table,1,-1));
        result.addAll(possibleMovesRowAndColumnOrDiagonals(table,-1,-1));
        result.addAll(possibleMovesRowAndColumnOrDiagonals(table,-1,1));
        result.addAll(possibleMovesRowAndColumnOrDiagonals(table,1,0));
        result.addAll(possibleMovesRowAndColumnOrDiagonals(table,-1,0));
        result.addAll(possibleMovesRowAndColumnOrDiagonals(table,0,1));
        result.addAll(possibleMovesRowAndColumnOrDiagonals(table,0,-1));
        return result;
    }
}