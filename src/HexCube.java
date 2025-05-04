import java.util.ArrayList;

class HexCube {
    private Player occupant;
    public final int q;
    public final int r;
    public final int s;

    public static ArrayList<HexCube> directions = new ArrayList<HexCube>() {{
        add(new HexCube(1, 0, -1));
        add(new HexCube(1, -1, 0));
        add(new HexCube(0, -1, 1));
        add(new HexCube(-1, 0, 1));
        add(new HexCube(-1, 1, 0));
        add(new HexCube(0, 1, -1));
    }};

    public HexCube(int q, int r, int s) {
        if (q + r + s != 0) {
            throw new IllegalArgumentException("q + r + s must be 0");
        }
        this.q = q;
        this.r = r;
        this.s = s;
    }

    public Boolean isOccupied() {
        return occupant != null;
    }

    public Player getOccupant(){
        return occupant;
    }

    public void setOccupant(Player player) {
        this.occupant = player;
    }

    public static HexCube direction(int var0) {
        return directions.get(var0);
    }

    public HexCube add(HexCube other) {
        return new HexCube(this.q + other.q, this.r + other.r, this.s + other.s);
    }

    public HexCube subtract(HexCube other) {
        return new HexCube(this.q - other.q, this.r - other.r, this.s - other.s);
    }

    public int length() {
        return (Math.abs(this.q) + Math.abs(this.r) + Math.abs(this.s)) / 2;
    }
    @Override
    public String toString() {
        return q + " " + r + " " + s;
    }
    public int distance(HexCube other) {
        return this.subtract(other).length();
    }
}
