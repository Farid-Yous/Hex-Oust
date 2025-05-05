import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class Game {
    boolean captureOccurred;
    private Player player1;
    private Player player2;
    private Player currentPlayer;
    private int round;
    private Player winner;
    // map of all hexes by “q,r,s” coordinate
    private static HashMap<String, HexCube> hexMap = new HashMap<>();

    // Swing UI components
    private JFrame frame;
    private JLabel turnIndicator;

    public Game(Player p1, Player p2) throws InterruptedException {
        this.player1 = p1;
        this.player2 = p2;
        this.round = 0;

        // Set up the Swing window and data structures
        gameInit();

        // Player 1 always starts
        currentPlayer = player1;

        // Main game loop
        while (true) {
            // wait until a valid click/placement
            while (!playTurn(currentPlayer)) {
                Thread.sleep(10);
            }

            // check for win after every move
            if (checkWin()) {
                final String message = (winner != null)
                        ? winner.getName() + " wins!"
                        : "It's a tie!";

                // end of game dialog to choose whether to start again or exit
                Platform.runLater(() -> {
                    Alert alert = new Alert(AlertType.CONFIRMATION);
                    alert.setTitle("Game Over");
                    alert.setHeaderText(message);

                    ButtonType playAgainBtn = new ButtonType("Play Again");
                    ButtonType exitBtn = new ButtonType("Exit");
                    alert.getButtonTypes().setAll(playAgainBtn, exitBtn);

                    alert.showAndWait().ifPresent(response -> {
                        if (response == playAgainBtn) {
                            // close the Swing frame
                            SwingUtilities.invokeLater(() -> {
                                if (frame != null) frame.dispose();
                            });
                            // restart JavaFX launcher
                            try {
                                new HexoustLauncherFX().start(new Stage());
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        } else {
                            Platform.exit();
                            System.exit(0);
                        }
                    });
                });
                break;
            }

            // if no capture occurred, change turns
            if (!captureOccurred) {
                changeTurn();
            } else {
                // Reset capture flag for next turn
                captureOccurred = false;
                // Update the turn indicator to show it's still the same player's turn
                updateTurnIndicator();
            }
            round++;
        }
    }

    /**
     * builds the hex data and creates the Swing window with a turn indicator.
     */
    private void gameInit() {
        // build hex co ords
        double size = 20;
        double originX = 600;
        double originY = 300;
        Layout layout = new Layout(Layout.flat,
                new Point(size, size),
                new Point(originX, originY));
        ArrayList<HexCube> hexes = new ArrayList<>();
        for (int q = -6; q <= 6; q++) {
            for (int r = -6; r <= 6; r++) {
                int s = -q - r;
                if (Math.abs(s) <= 6) {
                    HexCube hex = new HexCube(q, r, s);
                    hexes.add(hex);
                    hexMap.put(q + "," + r + "," + s, hex);
                }
            }
        }

        // swing ui
        SwingUtilities.invokeLater(() -> {
            frame = new JFrame("Hex Grid");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());

            // turn indicator
            turnIndicator = new JLabel("", SwingConstants.CENTER);
            turnIndicator.setOpaque(true);
            turnIndicator.setPreferredSize(new Dimension(1200, 30));
            turnIndicator.setFont(turnIndicator.getFont().deriveFont(Font.BOLD, 16f));
            frame.add(turnIndicator, BorderLayout.NORTH);

            // hex grid
            HexGrid hexGrid = new HexGrid(hexes, layout);
            hexGrid.setPreferredSize(new Dimension(1200, 1200));
            frame.add(hexGrid, BorderLayout.CENTER);

            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            // indicator initialised for player 1
            updateTurnIndicator();
        });
    }

    /**
     * change turns
     */
    private void changeTurn() {
        currentPlayer = (currentPlayer == player1) ? player2 : player1;
        updateTurnIndicator();
    }

    /**
     * Try to place a stone for this player. Returns true on success.
     */
    private Boolean playTurn(Player player) throws InterruptedException {
        // get the hex user clicked on
        HexCube clickedCell = HexGrid.clickedHex();
        if (clickedCell == null) return false;
        //replace the clicked hex with the actual cell in our hashmap
        HexCube actualHex = getCellByCoordinates(
                clickedCell.q,
                clickedCell.r,
                clickedCell.s
        );
        if (actualHex == null || actualHex.isOccupied()) {
            return false;
        }

        // occupy it
        actualHex.setOccupant(player);
        player.addCell();

        // build / merge groups
        for (HexCube dir : HexCube.directions) {
            HexCube neighbor = getCellByCoordinates(
                    actualHex.q + dir.q,
                    actualHex.r + dir.r,
                    actualHex.s + dir.s
            );
            if (neighbor != null
                    && neighbor.isOccupied()
                    && neighbor.getOccupant() == player) {
                if (!player.isInGroup(actualHex)) {
                    player.addToGroup(actualHex, neighbor);
                } else {
                    // merging existing groups
                    Player.mergePlayerGroups(
                            player, actualHex,
                            player, neighbor
                    );
                }
            }
        }
        if (!player.isInGroup(actualHex)) {
            player.newGroup(actualHex);
        }

        // handle captures
        capturingPlacement(actualHex);
        return true;
    }
    /**
     * Handles the complex capture mechanics after a piece is placed.
     * This method implements two types of captures:
     * 1. Whether it will be captured upon placement by an adjacent opponent group larger than itself
     * 2. Whether it will capture an adjacent opponent group smaller than itself
     *
     *
     * The capture rules are:
     * - Larger groups can capture smaller adjacent groups
     * - Turns aren't swapped when a player captures an opponent group
     * - If the placed piece's group is captured, no further captures are checked
     *
     * @param actualHex The hex that was just placed
     */
    private void capturingPlacement(HexCube actualHex) {
        Player enemy = (currentPlayer == player1) ? player2 : player1;
        captureOccurred = false;  // Reset at start of capture check

        // first check if the newly placed group is captured
        HashSet<Integer> surroundingEnemy = new HashSet<>();
        for (HexCube dir : HexCube.directions) {
            HexCube n = getCellByCoordinates(
                    actualHex.q + dir.q,
                    actualHex.r + dir.r,
                    actualHex.s + dir.s);
            if (n != null && n.isOccupied() && n.getOccupant() == enemy) {
                Integer egid = enemy.getGroupId(n);
                if (egid != null) surroundingEnemy.add(egid);
            }
        }
        for (Integer egid : surroundingEnemy) {
            HashSet<HexCube> eg = enemy.groups.get(egid);
            Integer myGid = currentPlayer.getGroupId(actualHex);
            HashSet<HexCube> mg = currentPlayer.groups.get(myGid);
            if (eg != null && mg != null && eg.size() > mg.size()) {
                for (HexCube c : mg) c.setOccupant(enemy);
                int cap = mg.size();
                enemy.addCell(cap);
                currentPlayer.addCell(-cap);
                Player.mergePlayerGroups(
                        enemy,
                        eg.iterator().next(),
                        currentPlayer,
                        actualHex
                );
                // Don't set captureOccurred to true here since the current player got captured
                return;
            }
        }

        // then chain‐capture any smaller neighbors
        Integer myGid2 = currentPlayer.getGroupId(actualHex);
        if (myGid2 == null) return;
        HashSet<HexCube> myGroup = currentPlayer.groups.get(myGid2);
        boolean repeat;
        do {
            repeat = false;
            HashSet<Integer> adjEnemy = new HashSet<>();
            for (HexCube h : myGroup) {
                for (HexCube dir : HexCube.directions) {
                    HexCube n = getCellByCoordinates(
                            h.q + dir.q, h.r + dir.r, h.s + dir.s);
                    if (n != null
                            && n.isOccupied()
                            && n.getOccupant() == enemy) {
                        Integer id = enemy.getGroupId(n);
                        if (id != null) adjEnemy.add(id);
                    }
                }
            }
            for (Integer id : adjEnemy) {
                HashSet<HexCube> eg = enemy.groups.get(id);
                if (eg != null && myGroup.size() > eg.size()) {
                    for (HexCube c : eg) c.setOccupant(currentPlayer);
                    int cap = eg.size();
                    currentPlayer.addCell(cap);
                    enemy.addCell(-cap);
                    Player.mergePlayerGroups(
                            currentPlayer,
                            actualHex,
                            enemy,
                            eg.iterator().next()
                    );
                    repeat = true;
                    captureOccurred = true;  // Set to true only when current player captures enemy stones
                    myGroup = currentPlayer.groups
                            .get(currentPlayer.getGroupId(actualHex));
                }
            }
        } while (repeat);
    }

    //returns the hexcell from hashmap from its co ordinates
    private HexCube getCellByCoordinates(int q, int r, int s) {
        return hexMap.get(q + "," + r + "," + s);
    }

    //checks for a win
    private boolean checkWin() {
        if (round >= 2) {  //dont check for a win when both players havent played yet
            if (player1.getNumCells() == 0) { //if either player has 0 cells after already playing, theyve lost
                winner = player2;
                return true;
            }
            if (player2.getNumCells() == 0) {
                winner = player1;
                return true;
            }
        }
        return false;
    }
    //helper function for updateTurnIndicator
    private Color awtColorFromPlayer(Player p) {
        switch (p.getColour().toLowerCase()) {
            case "red":
                return Color.RED;
            case "blue":
                return Color.BLUE;
            default:
                return Color.GRAY;
        }
    }
    //updates the turn indicator at the top of the screen
    private void updateTurnIndicator() {
        SwingUtilities.invokeLater(() -> {
            if (turnIndicator == null || currentPlayer == null) return;
            String name = currentPlayer.getName();
            Color bg = awtColorFromPlayer(currentPlayer);
            Color fg = (bg.equals(Color.BLACK) ? Color.WHITE : Color.BLACK);

            turnIndicator.setText(name + "’s turn");
            turnIndicator.setBackground(bg);
            turnIndicator.setForeground(fg);
        });
    }
}