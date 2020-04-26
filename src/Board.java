import java.io.Serializable;
import java.util.ArrayList;

public class Board implements Serializable {
    public ArrayList<Chessman> chessmen = new ArrayList<>();

    public Board(ArrayList<Chessman> list){
        for (Chessman temp: list) {
            if(temp.getSymbol()=='K'||temp.getSymbol()=='k'){
                chessmen.add(new King(temp.getColor(),temp.getCoordinates(),temp.isMove()));
            }else if(temp.getSymbol()=='H'||temp.getSymbol()=='h'){
                chessmen.add(new Queen(temp.getColor(),temp.getCoordinates(),temp.isMove()));
            }else if(temp.getSymbol()=='G'||temp.getSymbol()=='g'){
                chessmen.add(new Bishop(temp.getColor(),temp.getCoordinates(),temp.isMove()));
            }else if(temp.getSymbol()=='W'||temp.getSymbol()=='w'){
                chessmen.add(new Rook(temp.getColor(),temp.getCoordinates(),temp.isMove()));
            }else if(temp.getSymbol()=='S'||temp.getSymbol()=='s'){
                chessmen.add(new Knight(temp.getColor(),temp.getCoordinates(),temp.isMove()));
            }else if(temp.getSymbol()=='P'||temp.getSymbol()=='p'){
                chessmen.add(new Pawn(temp.getColor(),temp.getCoordinates(),temp.isMove()));
            }
        }
    }

    public Board() {
        chessmen.add(new Rook('w',new Coordinates(0,0),false));
        chessmen.add(new Knight('w',new Coordinates(1,0),false));
        chessmen.add(new Bishop('w',new Coordinates(2,0),false));
        chessmen.add(new Queen('w',new Coordinates(3,0),false));
        chessmen.add(new King('w',new Coordinates(4,0),false));
        chessmen.add(new Bishop('w',new Coordinates(5,0),false));
        chessmen.add(new Knight('w',new Coordinates(6,0),false));
        chessmen.add(new Rook('w',new Coordinates(7,0),false));
        for(int i=0;i < 8; i++){
            chessmen.add(new Pawn('w',new Coordinates(i,1),false));
        }
        chessmen.add(new Rook('b',new Coordinates(0,7),false));
        chessmen.add(new Knight('b',new Coordinates(1,7),false));
        chessmen.add(new Bishop('b',new Coordinates(2,7),false));
        chessmen.add(new Queen('b',new Coordinates(3,7),false));
        chessmen.add(new King('b',new Coordinates(4,7),false));
        chessmen.add(new Bishop('b',new Coordinates(5,7),false));
        chessmen.add(new Knight('b',new Coordinates(6,7),false));
        chessmen.add(new Rook('b',new Coordinates(7,7),false));
        for(int i=0;i < 8; i++){
            chessmen.add(new Pawn('b',new Coordinates(i,6),false));
        }
    }

    char[][] getBoard(){
        char[][] table= new char[8][8];
        for(int i =0; i <8; i++){
            for(int j = 0; j < 8; j++){
                table[i][j]='-';
            }
        }
        for(int i = 0; i < chessmen.size(); i++){
            table[chessmen.get(i).getCoordinates().getX()][chessmen.get(i).getCoordinates().getY()] = chessmen.get(i).getSymbol();
        }
        return table;
    }

    public int getIndex(Coordinates coordinates){
        for(int i = 0; i < chessmen.size(); i++){
            if(chessmen.get(i).getCoordinates().equals(coordinates)) {
                return i;
            }
        }
        return -1;
    }

    public void addChessman(char color, char symbol, Coordinates coordinates){
        if(symbol == 'g'){
            chessmen.add(new Bishop(color, coordinates,false));
        }else if(symbol == 's'){
            chessmen.add(new Knight(color, coordinates,false));
        }else if(symbol == 'h'){
            chessmen.add(new Queen(color, coordinates,false));
        }else{
            chessmen.add(new Rook(color, coordinates,false));
        }
        chessmen.get(chessmen.size()-1).setMove();
    }

    public void deleteChessman(Coordinates coordinates){
        if(getIndex(coordinates)!=-1) chessmen.remove(getChessman(coordinates));
    }

    public Chessman getChessman(Coordinates coordinates){
        if(getIndex(coordinates)==-1) return null;
        return chessmen.get(getIndex(coordinates));
    }
    public Chessman getChessman(int index){
        return chessmen.get(index);
    }

    public boolean isCheck(char color){
        Coordinates kingCoordinates = new Coordinates();
        char symbol;
        symbol = (color=='w') ? 'K' : 'k';
        for (Chessman piece: chessmen) {
            if(piece.getSymbol()==symbol){
                kingCoordinates=piece.getCoordinates();
                break;
            }
        }
        for (Chessman piece: chessmen) {
            if(piece.getColor()==color) continue;
            if(piece.possibleMoves(this).contains(kingCoordinates)) return true;
        }
        return false;
    }

    public boolean areMovesLeft(char color){
        ArrayList<Chessman> availableChessman = new ArrayList<>();
        for (Chessman temp: chessmen) {
            if(temp.getColor()==color&&temp.possibleMoves(this).size()!=0){
                availableChessman.add(temp);
            }
        }
        if(availableChessman.size()!=0) return true;
        return false;
    }
}
