import java.util.Scanner;

public class Player {
    private String name;
    private String colour;
    private int numCells;

    public Player(String name, String colour) {
        if (colour == null || (!colour.equals("blue") && !colour.equals("red"))) {
            throw new IllegalArgumentException("Invalid color! Choose 'blue' or 'red'.");
        }
        this.name = name;
        this.colour = colour;
        this.numCells = 0;
    }

    public String getName() {
        return name;
    }

    public String getColour() {
        return colour;
    }

    public int getNumCells() {
        return numCells;
    }

    public void addCell() {
        this.numCells = this.numCells + 1;
    }

    public void getDetails() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Please enter player name: ");
        this.name = sc.nextLine();
        System.out.print("Please enter player colour (blue or red): ");
        this.colour = sc.nextLine();
    }
}
