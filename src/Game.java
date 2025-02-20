import javax.swing.*;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.Scanner;
public class Game {
    private Player player1;
    private Player player2;
    private Player currentPlayer;
    private int round;
    private Player winner;

    public Game(Player player1, Player player2) throws InterruptedException {
        this.player1 = player1;
        this.player2 = player2;
        gameInit();
        currentPlayer = player1;
        while (!checkWin(player1, player2)) {
            System.out.println("Current turn: " + currentPlayer.getName());
            while(!playTurn(currentPlayer)){
                System.out.println("Invalid entry, play again " + currentPlayer.getName());
            }
            changeTurn();
            System.out.println(currentPlayer.getName() + " score is " + currentPlayer.getNumCells());

        }
    }

    private void gameInit() {
        double size = 20;
        double originX = 600;
        double originY = 300;

        Layout layout = new Layout(Layout.flat, new Point(size, size), new Point(originX, originY));
        ArrayList<ArrayList<Point>> grid = new ArrayList<>();

        for (int q = -6; q <= 6; q++) {
            for (int r = -6; r <= 6; r++) {
                for (int s = -6; s <= 6; s++) {
                    if (q + r + s == 0) {
                        HexCube hex = new HexCube(q, r, s);
                        grid.add(layout.polygonCorners(hex));
                    }
                }
            }
        }

        JFrame frame = new JFrame("Hex Grid");
        HexGrid hexGrid = new HexGrid(grid, layout);
        frame.add(hexGrid);
        frame.setSize(1200, 1200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private void changeTurn() {
        currentPlayer = (currentPlayer == player1) ? player2 : player1;
    }

    private Boolean playTurn(Player player) throws InterruptedException {
        HexCube cell = HexGrid.clickedHex();
        System.out.println("Clicked hex: " + cell.q + "," + cell.r + "," + cell.s);
        System.out.println("Is occupied: " + cell.isOccupied());
        cell = getCellByCoordinates(cell.q, cell.r, cell.s);
        if(cell.isOccupied()){
            return false;  // Return false if the cell is already occupied
        }

        cell.setOccupant(player);  // Set the current player as the occupant
        player.addCell();  // Add the cell to the player's score or collection of cells
        return true;
    }

    // Assuming your grid is a list of lists of HexCube
    ArrayList<ArrayList<HexCube>> grid = new ArrayList<>();

    // Method to find a HexCube by its q, r, s coordinates
    public HexCube getCellByCoordinates(int q, int r, int s) {
        for (ArrayList<HexCube> row : grid) {
            for (HexCube hex : row) {
                if (hex.q == q && hex.r == r && hex.s == s) {
                    return hex;  // Return the found hex cell
                }
            }
        }
        return null;  // Return null if no matching cell is found
    }

    private boolean checkWin(Player player1, Player player2) {
        if (round < 2) {
            return false;
        }
        if (player1.getNumCells() == 0) {
            winner = player2;
            return true;
        } else if (player2.getNumCells() == 0) {
            winner = player1;
            return true;
        }
        return false;
    }
    public static void main(String[] args) throws InterruptedException {
        // Set up players
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter Player 1's name: ");
        String player1Name = scanner.nextLine();
        System.out.println("Enter Player 1's color (blue or red): ");
        String player1Color = scanner.nextLine();

        Player player1 = new Player(player1Name, player1Color);

        System.out.println("Enter Player 2's name: ");
        String player2Name = scanner.nextLine();
        System.out.println("Enter Player 2's color (blue or red): ");
        String player2Color = scanner.nextLine();

        Player player2 = new Player(player2Name, player2Color);

        // Start the game with the players
        new Game(player1, player2);
    }
}
