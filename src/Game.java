import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
public class Game {
    private Player player1;
    private Player player2;
    private Player currentPlayer;
    private int round;
    private Player winner;
    private static HashMap<String, HexCube> hexMap = new HashMap<>();

    public Game(Player player1, Player player2) throws InterruptedException {
        this.player1 = player1;             //player 1 instance
        this.player2 = player2;             //player 2 instance
        gameInit();                         //initialise game
        currentPlayer = player1;            //initialise starting player
        while (!checkWin(player1, player2)) {           //keep looping until winner
            System.out.println("Current turn: " + currentPlayer.getName());
            while(!playTurn(currentPlayer)){               //playturn returns false if the move is invalid
                System.out.println("Invalid entry, play again " + currentPlayer.getName());
            }
            changeTurn();                   //change turns
            System.out.println(currentPlayer.getName() + " score is " + currentPlayer.getNumCells());
        }
    }

    private void gameInit() {               //game initialisation
        double size = 20;
        double originX = 600;
        double originY = 300;

        Layout layout = new Layout(Layout.flat, new Point(size, size), new Point(originX, originY));
        ArrayList<ArrayList<Point>> grid = new ArrayList<>();

        ArrayList<HexCube> hexes = new ArrayList<>();
        for (int q = -6; q <= 6; q++) {
            for (int r = -6; r <= 6; r++) {
                for (int s = -6; s <= 6; s++) {
                    if (q + r + s == 0) {
                        HexCube hex = new HexCube(q, r, s);
                        hexes.add(hex);
                        hexMap.put(q + "," + r + "," + s, hex);
                    }
                }
            }
        }



        JFrame frame = new JFrame("Hex Grid");
        HexGrid hexGrid = new HexGrid(hexes, layout);
        frame.add(hexGrid);
        frame.setSize(1200, 1200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private void changeTurn() {
        currentPlayer = (currentPlayer == player1) ? player2 : player1;         //change turns
    }

    private Boolean playTurn(Player player) throws InterruptedException {
        HexCube clickedCell = HexGrid.clickedHex();
        HexCube actualHex = getCellByCoordinates(clickedCell.q, clickedCell.r, clickedCell.s);

        if (actualHex == null) {
            System.out.println("Hex not found in grid!");
            return false;
        }

        if (actualHex.isOccupied()) {
            System.out.println("Cell is already occupied!");
            return false;
        }

        actualHex.setOccupant(player);
        player.addCell();
        /*
        MOVE PLACEMENT

         */
        for (HexCube direction : HexCube.directions) { //checking all neighbouring cells
            HexCube neighbor = getCellByCoordinates(
                    actualHex.q + direction.q,
                    actualHex.r + direction.r,
                    actualHex.s + direction.s
            );

            if (neighbor != null && neighbor.isOccupied() && neighbor.getOccupant() == player && !player.isInGroup(actualHex)) { //if neighbouring cell belongs to player add to the group
                player.addToGroup(actualHex, neighbor);
            }
            if (neighbor != null && neighbor.isOccupied() && neighbor.getOccupant() == player && player.isInGroup(actualHex)) {
                Player.mergePlayerGroups(player1,actualHex,player1,neighbor);
            }
        }
        if(actualHex.getOccupant().isInGroup(actualHex) == false){  //if the cell isnt in a group, form a new group with it being the only member
            actualHex.getOccupant().newGroup(actualHex);
        }

        /*
        CAPTURING PLACEMENT
         */
        capturingPlacement(actualHex);

        player.printGroups();

        return true;
    }
public int capturingPlacement(HexCube actualHex) {
    Player otherPlayer = currentPlayer == player1 ? player2 : player1;
    for (HexCube direction : HexCube.directions) {
        HexCube neighbor = getCellByCoordinates(
                actualHex.q + direction.q,
                actualHex.r + direction.r,
                actualHex.s + direction.s
        );

        if (neighbor != null && neighbor.isOccupied() && neighbor.getOccupant() == otherPlayer) {
            if (currentPlayer.getGroupSize(actualHex) > otherPlayer.getGroupSize(neighbor)) {
                int n = otherPlayer.getGroupSize(neighbor);
                Player.mergePlayerGroups(currentPlayer, actualHex, otherPlayer, neighbor);
                currentPlayer.addCell(n);
                otherPlayer.addCell(-n);
                System.out.println("capturing placement, play again!");
                return 1;
            }
            if (currentPlayer.getGroupSize(actualHex) < otherPlayer.getGroupSize(neighbor)) {
                int n = otherPlayer.getGroupSize(neighbor);
                Player.mergePlayerGroups(otherPlayer, neighbor, currentPlayer, actualHex);
                currentPlayer.addCell(-n);
                otherPlayer.addCell(n);
                System.out.println("capturing placement, play again!");
                return 1;
            }

        }


    }
    return 0;
}
    // Method to find a HexCube by its q, r, s coordinates
    public HexCube getCellByCoordinates(int q, int r, int s) {
        return hexMap.get(q + "," + r + "," + s); // O(1) lookup
    }

    //checks for win condition
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
