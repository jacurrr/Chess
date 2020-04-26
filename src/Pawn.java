import java.io.Serializable;
import java.util.ArrayList;

public class Pawn extends PrimaryPiece implements Chessman, Serializable {
    public Pawn(char color, Coordinates coordinates,boolean move){
        super(color,coordinates,move);
        if(color=='w'){
            setImage("file/WhitePawn.png");
        }else{
            setImage("file/BlackPawn.png");
        }
    }

    @Override
    public final ArrayList<Coordinates> possibleMoves(Board board) {
        char[][] table = board.getBoard();
        ArrayList<Coordinates> result = new ArrayList<>();
        int x = getCoordinates().getX();
        int y = getCoordinates().getY();
        int row;
        int incrementation;
        boolean color=!Character.isLowerCase(getSymbol());
        if(getColor()=='w'){
            row = 1;
            incrementation=+1;
        }else{
            row = 6;
            incrementation=-1;
        }
        if(getCoordinates().getY()==row&&table[x][y+incrementation]=='-'&&table[x][y+2*incrementation]=='-'){
            result.addAll(possibleMovesKnightOrKing(table,0,incrementation*2));
        }
        if(table[x][y+incrementation]=='-'){
            result.addAll(possibleMovesKnightOrKing(table,0,incrementation));
        }
        if(x-1>-1&&table[x-1][y+incrementation]!='-'&&Character.isLowerCase(table[x-1][y+incrementation])==color){
            result.addAll(possibleMovesKnightOrKing(table,-1,incrementation));
        }
        if(x+1<8&&table[x+1][y+incrementation]!='-'&&Character.isLowerCase(table[x+1][y+incrementation])==color){
            result.addAll(possibleMovesKnightOrKing(table,1,incrementation));
        }
        return result;
    }

    @Override
    public char getSymbol() {
        return (this.getColor()=='w') ? 'P' : 'p';
    }
}
