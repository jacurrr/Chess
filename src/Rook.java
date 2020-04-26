import java.io.Serializable;
import java.util.ArrayList;

public class Rook extends PrimaryPiece implements Chessman, Serializable{
    public Rook(char color, Coordinates coordinates, boolean move){
        super(color,coordinates,move);
        if(color=='w'){
            setImage("file/WhiteRook.png");
        }else{
            setImage("file/BlackRook.png");
        }
    }

    @Override
    public char getSymbol() {
        return (this.getColor()=='w') ? 'W' : 'w';
    }

    @Override
    public final ArrayList<Coordinates> possibleMoves(Board board) {
        char[][] table = board.getBoard();
        ArrayList<Coordinates> result = new ArrayList<>();
        result.addAll(possibleMovesRowAndColumnOrDiagonals(table,1,0));
        result.addAll(possibleMovesRowAndColumnOrDiagonals(table,-1,0));
        result.addAll(possibleMovesRowAndColumnOrDiagonals(table,0,1));
        result.addAll(possibleMovesRowAndColumnOrDiagonals(table,0,-1));
        return result;
    }
}
