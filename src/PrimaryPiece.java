import javafx.scene.image.ImageView;

import java.io.Serializable;
import java.util.ArrayList;

public abstract class PrimaryPiece implements Serializable {
    private char color;
    private Coordinates coordinates;
    private boolean move;
    private String imageView;
    public PrimaryPiece (char color, Coordinates coordinates, boolean move){
        this.color = color;
        this.coordinates = coordinates;
        this.move = move;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public char getColor() {
        return color;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
        setMove();
    }

    public boolean isMove() {
        return move;
    }

    public void setMove() {
        move = true;
    }

    public String getImage(){
        return imageView;
    }

    public void setImage(String imageView){
        this.imageView = imageView;
    }

    //metoda sprawdzająca czy po ruchu figury w kolorze x, występuje szach dla koloru x
    public boolean isKingCheck(char[][] boardOriginal, Coordinates destiny) {
        char[][] board = new char[8][8];
        for (int i = 0; i < 8; i++) {
            System.arraycopy(boardOriginal[i], 0, board[i], 0, 8);
        }
        //nadpisywanie w tablicy char nowej pozycji figury
        if(getCoordinates()!=destiny){
            board[destiny.getX()][destiny.getY()] = board[getCoordinates().getX()][getCoordinates().getY()];
            board[getCoordinates().getX()][getCoordinates().getY()] = '-';
        }

        int xKing = -1;
        int yKing = -1;
        char color = getColor();
        //wyciąganie współrzenych króla w danym kolorze
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (color == 'w' && board[i][j] != 'K') {
                    continue;
                } else if (color == 'b' && board[i][j] != 'k') {
                    continue;
                }
                xKing = i;
                yKing = j;
                break;
            }
            if (xKing != -1) break;
        }
        //sprawdzenie dla białych
        int xTemp = xKing;
        int yTemp = yKing;
        if (color == 'w') {
            //Szachowanie króla po wierszu i kolumnie
            if (isCheckRowAndColumnOrDiagonals(board, xKing, yKing, 1, 0, 'w')) return true;
            if (isCheckRowAndColumnOrDiagonals(board, xKing, yKing, -1, 0, 'w')) return true;
            if (isCheckRowAndColumnOrDiagonals(board, xKing, yKing, 0, 1, 'w')) return true;
            if (isCheckRowAndColumnOrDiagonals(board, xKing, yKing, 0, -1, 'w')) return true;
            if (isCheckRowAndColumnOrDiagonals(board, xKing, yKing, 1, 1, 'g')) return true;
            if (isCheckRowAndColumnOrDiagonals(board, xKing, yKing, -1, 1, 'g')) return true;
            if (isCheckRowAndColumnOrDiagonals(board, xKing, yKing, 1, -1, 'g')) return true;
            if (isCheckRowAndColumnOrDiagonals(board, xKing, yKing, -1, -1, 'g')) return true;
            if (isCheckPawn(board, xKing, yKing, 'p')) return true;
            if (isCheckKnight(board, xKing, yKing, 's')) return true;
            //Sprawdzenie dla czarnych
        } else {
            if (isCheckRowAndColumnOrDiagonals(board, xKing, yKing, 1, 0, 'W')) return true;
            if (isCheckRowAndColumnOrDiagonals(board, xKing, yKing, -1, 0, 'W')) return true;
            if (isCheckRowAndColumnOrDiagonals(board, xKing, yKing, 0, 1, 'W')) return true;
            if (isCheckRowAndColumnOrDiagonals(board, xKing, yKing, 0, -1, 'W')) return true;
            if (isCheckRowAndColumnOrDiagonals(board, xKing, yKing, 1, 1, 'G')) return true;
            if (isCheckRowAndColumnOrDiagonals(board, xKing, yKing, -1, 1, 'G')) return true;
            if (isCheckRowAndColumnOrDiagonals(board, xKing, yKing, 1, -1, 'G')) return true;
            if (isCheckRowAndColumnOrDiagonals(board, xKing, yKing, -1, -1, 'G')) return true;
            if (isCheckPawn(board, xKing, yKing, 'P')) return true;
            if (isCheckKnight(board, xKing, yKing, 'S')) return true;
        }
        return false;
    }

    boolean isCheckRowAndColumnOrDiagonals(char[][] board, int x, int y, int incrementationX, int incrementationY, char figureSymbol1) {
        char figureSymbol2;
        char figureSymbol3;
        if (figureSymbol1 == 'g' || figureSymbol1 == 'w') {
            figureSymbol2 = 'h';
            figureSymbol3 = 'k';
        } else {
            figureSymbol2 = 'H';
            figureSymbol3 = 'K';
        }
        for (int i = 0; i < 7; i++) {
            x = x + incrementationX;
            y = y + incrementationY;
            if (x > 7 || x < 0 || y > 7 || y < 0) break;
            if (board[x][y] == '-') continue;
            if (board[x][y] == figureSymbol1 || board[x][y] == figureSymbol2) return true;
            if (i == 0 && board[x][y] == figureSymbol3) return true;
            if (board[x][y] != '-') break;
        }
        return false;
    }

    boolean isCheckPawn(char[][] board, int x, int y, char figureSymbol) {
        if (figureSymbol == 'p') {
            y += 1;
        } else {
            y -= 1;
        }
        if (y < 8 && y > -1 && x + 1 < 8) {
            if (board[x + 1][y] == figureSymbol) return true;
        }
        if (y < 8 && y > -1 && x - 1 > -1) {
            if (board[x - 1][y] == figureSymbol) return true;
        }
        return false;
    }

    boolean isCheckKnight(char[][] board, int x, int y, char figureSymbol) {
        if (x + 1 < 8 && y + 2 < 8) {
            if (board[x + 1][y + 2] == figureSymbol) return true;
        }
        if (x + 2 < 8 && y + 1 < 8) {
            if (board[x + 2][y + 1] == figureSymbol) return true;
        }
        if (x + 2 < 8 && y - 1 > -1) {
            if (board[x + 2][y - 1] == figureSymbol) return true;
        }
        if (x + 1 < 8 && y - 2 > -1) {
            if (board[x + 1][y - 2] == figureSymbol) return true;
        }
        if (x - 1 > -1 && y - 2 > -1) {
            if (board[x - 1][y - 2] == figureSymbol) return true;
        }
        if (x - 2 > -1 && y - 1 > -1) {
            if (board[x - 2][y - 1] == figureSymbol) return true;
        }
        if (x - 2 > -1 && y + 1 < 8) {
            if (board[x - 2][y + 1] == figureSymbol) return true;
        }
        if (x - 1 > -1 && y + 2 < 8) {
            if (board[x - 1][y + 2] == figureSymbol) return true;
        }
        return false;
    }

    public final ArrayList<Coordinates> possibleMovesRowAndColumnOrDiagonals(char[][] board, int incrementationX, int incrementationY) {
        ArrayList<Coordinates> result = new ArrayList<>();
        int x = getCoordinates().getX();
        int y = getCoordinates().getY();
        for (int i = 0; i < 7; i++) {
            x = x + incrementationX;
            y = y + incrementationY;
            if (x > 7 || y > 7 || x < 0 || y < 0) break;
            if (board[x][y] == '-') {
                if (!isKingCheck(board, new Coordinates(x, y))) result.add(new Coordinates(x, y));
            } else if (getColor() == 'w') {
                if (Character.isLowerCase(board[x][y])) {
                    if (!isKingCheck(board, new Coordinates(x, y))) result.add(new Coordinates(x, y));
                }
                break;
            } else {
                if (!Character.isLowerCase(board[x][y])) {
                    if (!isKingCheck(board, new Coordinates(x, y))) result.add(new Coordinates(x, y));
                }
                break;
            }
        }
        return result;
    }

    public final ArrayList<Coordinates> possibleMovesKnightOrKing(char[][] board, int incrementationX, int incrementationY){
        ArrayList<Coordinates> result = new ArrayList<>();
        int x = getCoordinates().getX();
        int y = getCoordinates().getY();
        if(x + incrementationX > -1 && x + incrementationX <8 && y + incrementationY > -1 && y + incrementationY <8 ){
            boolean temp = Character.isLowerCase(board[getCoordinates().getX()+incrementationX][getCoordinates().getY()+incrementationY]);
            if(getColor()=='b'){
                temp = !temp;
            }
            if((board[x+incrementationX][y+incrementationY]=='-'||temp)&&!isKingCheck(board,new Coordinates(x+incrementationX, y+incrementationY))){
                result.add(new Coordinates(x+incrementationX,y+incrementationY));
            }
        }
        return result;
    }
}