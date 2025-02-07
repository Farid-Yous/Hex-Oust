public class Cell {
    private Player occupant = null;
    private int x;
    private int y;
    private int z;


    private Boolean isOccupied(){
        if(occupant == null){
            return false;
        }
        return true;
    }
    void setOccupant(Player p){
        occupant = p;
    }
    Cell(int x, int y, int z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
    private int getxCoord(){
        return x;
    }
    private int getyCoord(){
        return y;
    }
    private int getzCoord(){
        return z;
    }

}
