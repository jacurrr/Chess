import javafx.scene.image.ImageView;

import java.util.ArrayList;

public interface Chessman {
    ArrayList <Coordinates> possibleMoves(Board board);
    char getSymbol();
    Coordinates getCoordinates();
    boolean isMove();
    void setMove();
    char getColor();
    void setCoordinates(Coordinates coordinates);
    String getImage();
}
