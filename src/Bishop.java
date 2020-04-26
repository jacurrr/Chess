import java.io.Serializable;
import java.util.ArrayList;

public class Bishop extends PrimaryPiece implements Chessman, Serializable {
    public Bishop(char color, Coordinates coordinates, boolean move){
        super(color,coordinates,move);
        if(color=='w'){
            setImage("file/WhiteBishop.png");
        }else{
            setImage("file/BlackBishop.png");
        }
    }

    @Override
    public final ArrayList<Coordinates> possibleMoves(Board board) {
        char[][] table = board.getBoard();
        ArrayList<Coordinates> result = new ArrayList<>();
        result.addAll(possibleMovesRowAndColumnOrDiagonals(table,1,1));
        result.addAll(possibleMovesRowAndColumnOrDiagonals(table,1,-1));
        result.addAll(possibleMovesRowAndColumnOrDiagonals(table,-1,1));
        result.addAll(possibleMovesRowAndColumnOrDiagonals(table,-1,-1));
        return result;
    }

    @Override
    public char getSymbol(){
        return (this.getColor()=='w') ? 'G' : 'g';
    }
}
