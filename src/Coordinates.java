import java.io.Serializable;
public class Coordinates implements Serializable{
    private int x;
    private int y;
    public Coordinates(){}
    public Coordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Coordinates(Coordinates coordinates){
        this.x= coordinates.getX();
        this.y= coordinates.getY();
    }
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        Coordinates that = (Coordinates) o;
        if (x != that.x) return false;
        return y == that.y;
    }

    @Override
    public String toString(){
        return "X: "+(x+1)+" Y: "+(y+1)+"\n";
    }
}
