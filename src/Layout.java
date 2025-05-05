import java.util.ArrayList;

class Layout {
    public final Orientation orientation;
    public final Point size;
    public final Point origin;
    public static Orientation flat = new Orientation(1.5, 0.0, Math.sqrt(3.0) / 2.0, Math.sqrt(3.0), 0.6666666666666666, 0.0, -0.3333333333333333, Math.sqrt(3.0) / 3.0, 0.0);

    public Layout(Orientation var1, Point var2, Point var3) {
        this.orientation = var1;
        this.size = var2;
        this.origin = var3;
    }

    public Point hexToPixel(HexCube var1) {
        Orientation var2 = this.orientation;
        double var3 = (var2.f0 * (double) var1.q + var2.f1 * (double) var1.r) * this.size.x;
        double var5 = (var2.f2 * (double) var1.q + var2.f3 * (double) var1.r) * this.size.y;
        return new Point(var3 + this.origin.x, var5 + this.origin.y);
    }

    public FractionalHexCube pixelToHex(Point var1) {
        Orientation var2 = this.orientation;
        Point var3 = new Point((var1.x - this.origin.x) / this.size.x, (var1.y - this.origin.y) / this.size.y);
        double var4 = var2.b0 * var3.x + var2.b1 * var3.y;
        double var6 = var2.b2 * var3.x + var2.b3 * var3.y;
        return new FractionalHexCube(var4, var6, -var4 - var6);
    }

    public Point hexCornerOffset(int var1) {
        Orientation var2 = this.orientation;
        double var3 = 6.283185307179586 * (var2.start_angle - (double) var1) / 6.0;
        return new Point(this.size.x * Math.cos(var3), this.size.y * Math.sin(var3));
    }

    public ArrayList<Point> polygonCorners(HexCube var1) {
        ArrayList var2 = new ArrayList<Point>() {
        };
        Point var3 = this.hexToPixel(var1);

        for (int var4 = 0; var4 < 6; ++var4) {
            Point var5 = this.hexCornerOffset(var4);
            var2.add(new Point(var3.x + var5.x, var3.y + var5.y));
        }

        return var2;
    }
}