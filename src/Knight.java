import java.io.Serializable;
import java.util.ArrayList;

public class Knight extends PrimaryPiece implements Chessman, Serializable {
    public Knight(char color, Coordinates coordinates,boolean move){
        super(color,coordinates,move);
        String imageView;
        if(color=='w'){
            setImage("file/WhiteKnight.png");
        }else{
            setImage("file/BlackKnight.png");
        }
    }

    @Override
    public char getSymbol(){
        return (this.getColor()=='w') ? 'S' : 's';
    }

    @Override
    public final ArrayList<Coordinates> possibleMoves(Board board) {
        char[][] table = board.getBoard();
        ArrayList<Coordinates> result = new ArrayList<>();
        result.addAll(possibleMovesKnightOrKing(table,2,1));
        result.addAll(possibleMovesKnightOrKing(table,2,-1));
        result.addAll(possibleMovesKnightOrKing(table,1,2));
        result.addAll(possibleMovesKnightOrKing(table,1,-2));
        result.addAll(possibleMovesKnightOrKing(table,-2,1));
        result.addAll(possibleMovesKnightOrKing(table,-2,-1));
        result.addAll(possibleMovesKnightOrKing(table,-1,2));
        result.addAll(possibleMovesKnightOrKing(table,-1,-2));
        return result;
    }
}
