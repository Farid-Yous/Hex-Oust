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
    public boolean capture;

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
            System.out.println("capture occured: "+ capture);
            if(capture){
                System.out.println("play turn again, captured enemy");
                while(capture){
                    playTurn(currentPlayer);
                }
            }
            changeTurn();                   //change turns
            System.out.println(currentPlayer.getName() + " score is " + currentPlayer.getNumCells());
            round++;
        }
        System.out.println("winner is " + winner.getName());
    }

    private void gameInit() {               //game initialisation
        double size = 20;
        double originX = 600;
        double originY = 300;

        Layout layout = new Layout(Layout.flat, new Point(size, size), new Point(originX, originY));

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
    public void capturingPlacement(HexCube actualHex) throws InterruptedException {
        Player enemy = (currentPlayer == player1) ? player2 : player1;
        capture = false;
        // First, check if the newly placed cell should be captured
        HashSet<Integer> surroundingEnemyGroupIds = new HashSet<>();
        for (HexCube direction : HexCube.directions) {
            HexCube neighbor = getCellByCoordinates(
                    actualHex.q + direction.q,
                    actualHex.r + direction.r,
                    actualHex.s + direction.s
            );

            if (neighbor != null && neighbor.isOccupied() && neighbor.getOccupant() == enemy) {
                Integer enemyGroupId = enemy.getGroupId(neighbor);
                if (enemyGroupId != null) {
                    surroundingEnemyGroupIds.add(enemyGroupId);
                }
            }
        }

        // check if enemy group can capture the placed cells group
        for (Integer enemyGroupId : surroundingEnemyGroupIds) {
            HashSet<HexCube> enemyGroup = enemy.groups.get(enemyGroupId);
            Integer myGroupId = currentPlayer.getGroupId(actualHex);
            HashSet<HexCube> myGroup = currentPlayer.groups.get(myGroupId);

            if (enemyGroup != null && myGroup != null && enemyGroup.size() > myGroup.size()) { // check for size comparison
                for (HexCube capturedCell : myGroup) {
                    capturedCell.setOccupant(enemy);
                }
                int capturedSize = myGroup.size();
                enemy.addCell(capturedSize);
                currentPlayer.addCell(-capturedSize);

                // mergee the groups
                Player.mergePlayerGroups(enemy, enemyGroup.iterator().next(), currentPlayer, actualHex);
                return;
            }
        }
        Integer myGroupId = currentPlayer.getGroupId(actualHex);
        if (myGroupId == null) {
            return;
        }
        HashSet<HexCube> myGroup = currentPlayer.groups.get(myGroupId);

        boolean captureOccurred;
        do {
            captureOccurred = false;
            HashSet<Integer> adjacentEnemyGroupIds = new HashSet<>();

            // find all neighbouring enemy groups
            for (HexCube hex : myGroup) {
                for (HexCube direction : HexCube.directions) {
                    HexCube neighbor = getCellByCoordinates(
                            hex.q + direction.q,
                            hex.r + direction.r,
                            hex.s + direction.s
                    );

                    if (neighbor != null && neighbor.isOccupied() && neighbor.getOccupant() == enemy) {
                        Integer enemyGroupId = enemy.getGroupId(neighbor);
                        if (enemyGroupId != null) {
                            adjacentEnemyGroupIds.add(enemyGroupId);
                        }

                    }
                }
            }
            // Process captures
            for (Integer enemyGroupId : adjacentEnemyGroupIds) {
                HashSet<HexCube> enemyGroup = enemy.groups.get(enemyGroupId);
                if (enemyGroup != null && myGroup.size() > enemyGroup.size()) {
                    capture = true;
                    // Capture the enemy group
                    for (HexCube capturedCell : enemyGroup) {
                        capturedCell.setOccupant(currentPlayer);
                    }
                    int capturedSize = enemyGroup.size();
                    currentPlayer.addCell(capturedSize);
                    enemy.addCell(-capturedSize);

                    // call merge function to sort out the group organisation
                    Player.mergePlayerGroups(currentPlayer, actualHex, enemy, enemyGroup.iterator().next());
                    captureOccurred = true;
                    capture = true;
                    myGroup = currentPlayer.groups.get(currentPlayer.getGroupId(actualHex));
                }
            }
        } while (captureOccurred);

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
