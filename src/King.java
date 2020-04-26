import java.io.Serializable;
import java.util.ArrayList;

public class King extends PrimaryPiece implements Chessman, Serializable{
    public King(char color, Coordinates coordinates,boolean move){
        super(color,coordinates,move);
        if(color=='w'){
            setImage("file/WhiteKing.png");
        }else{
            setImage("file/BlackKing.png");
        }
    }

    @Override
    public char getSymbol(){
        return (this.getColor()=='w') ? 'K' : 'k';
    }

    @Override
    public final ArrayList<Coordinates> possibleMoves(Board board) {
        char[][] table = board.getBoard();
        ArrayList <Coordinates> result = new ArrayList<>();
        int row;
        if(getColor()=='w'){
            row = 0;
        }else{
            row = 7;
        }
        boolean rook1Move = false;
        boolean rook2Move = false;
        if(board.getChessman(new Coordinates(0, row))!=null){
            rook1Move = !board.getChessman(new Coordinates(0, row)).isMove();
        }
        if(board.getChessman(new Coordinates(7, row))!=null){
            rook2Move = !board.getChessman(new Coordinates(7, row)).isMove();
        }
        if(!isMove()&&!isKingCheck(table,getCoordinates())){
            if(rook1Move&&table[1][row]=='-'&&table[2][row]=='-'&&table[3][row]=='-'){
                result.addAll(possibleMovesKnightOrKing(table,-2,0));
            }
            if(rook2Move&&table[5][row]=='-'&&table[6][row]=='-'){
                result.addAll(possibleMovesKnightOrKing(table,2,0));
            }
        }
        result.addAll(possibleMovesKnightOrKing(table,1,1));
        result.addAll(possibleMovesKnightOrKing(table,0,1));
        result.addAll(possibleMovesKnightOrKing(table,-1,1));
        result.addAll(possibleMovesKnightOrKing(table,1,0));
        result.addAll(possibleMovesKnightOrKing(table,-1,0));
        result.addAll(possibleMovesKnightOrKing(table,1,-1));
        result.addAll(possibleMovesKnightOrKing(table,0,-1));
        result.addAll(possibleMovesKnightOrKing(table,-1,-1));
        return result;
    }
}
