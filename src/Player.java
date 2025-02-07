import java.util.Scanner;

public class Player {
    private String name;
    private String colour;
    private int numCells;
    //constructor
    public Player(String name, String colour) {
        this.name = name;
        if(colour == null || !colour.equals("blue") || colour.equals("red")){
            throw new IllegalArgumentException();
        }
        this.colour = colour;
    }
    //getDetails takes user input for the two players
    public void getDetails() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Please enter player name: ");
        name = sc.nextLine();
        System.out.println("Please enter player colour: ");
        colour = sc.nextLine();
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
    public void setNumCells(int numCells) {
        this.numCells = numCells;
    }
}
