class FractionalHexCube {
    public final double q;
    public final double r;
    public final double s;

    public FractionalHexCube(double var1, double var3, double var5) {
        this.q = var1;
        this.r = var3;
        this.s = var5;
        if (Math.round(var1 + var3 + var5) != 0L) {
            throw new IllegalArgumentException("q + r + s must be 0");
        }
    }

    public HexCube hexRound() {
        int var1 = (int) Math.round(this.q);
        int var2 = (int) Math.round(this.r);
        int var3 = (int) Math.round(this.s);
        double var4 = Math.abs((double) var1 - this.q);
        double var6 = Math.abs((double) var2 - this.r);
        double var8 = Math.abs((double) var3 - this.s);
        if (var4 > var6 && var4 > var8) {
            var1 = -var2 - var3;
        } else if (var6 > var8) {
            var2 = -var1 - var3;
        } else {
            var3 = -var1 - var2;
        }

        return new HexCube(var1, var2, var3);
    }
}