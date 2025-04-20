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
    private Player player1;
    private Player player2;
    private Player currentPlayer;
    private int round;
    private Player winner;
    private static HashMap<String, HexCube> hexMap = new HashMap<>();
    private JFrame frame;  // Reference to the game window for disposal

    public Game(Player p1, Player p2) throws InterruptedException {
        this.player1 = p1; //player 1 instance
        this.player2 = p2;
        round = 0;
        gameInit();
        currentPlayer = player1;

        while (true) {

            while (!playTurn(currentPlayer)) {
            }
            changeTurn();
            round++;

            //checks if we have a winner
            if (checkWin()) {
                final String message = (winner != null)
                        ? winner.getName() + " wins!"
                        : "It's a tie!";
                //logic for postgame menu buttons
                Platform.runLater(() -> {
                    Alert alert = new Alert(AlertType.CONFIRMATION);
                    alert.setTitle("Game Over");
                    alert.setHeaderText(message);

                    ButtonType playAgainBtn = new ButtonType("Play Again");
                    ButtonType exitBtn = new ButtonType("Exit");
                    alert.getButtonTypes().setAll(playAgainBtn, exitBtn);

                    alert.showAndWait().ifPresent(response -> {
                        if (response == playAgainBtn) {
                            SwingUtilities.invokeLater(() -> {
                                if (frame != null) {
                                    frame.dispose();
                                }
                            });
                            // restart the game if that buttons pressed
                            try {
                                new HexoustLauncherFX().start(new Stage());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            // otherwise exit
                            Platform.exit();
                            System.exit(0);
                        }
                    });
                });
                break;
            }
        }
    }

    private void gameInit() {
        // Setup hex grid data
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
        // making a swing window
        SwingUtilities.invokeLater(() -> {
            frame = new JFrame("Hex Grid");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            HexGrid hexGrid = new HexGrid(hexes, layout);
            hexGrid.setPreferredSize(new Dimension(1200, 1200));

            frame.add(hexGrid);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    private void changeTurn() {
        currentPlayer = (currentPlayer == player1) ? player2 : player1;
    }

    private Boolean playTurn(Player player) throws InterruptedException {
        HexCube clickedCell = HexGrid.clickedHex();
        HexCube actualHex = getCellByCoordinates(clickedCell.q, clickedCell.r, clickedCell.s);
        if (actualHex == null || actualHex.isOccupied()) {
            System.out.println(actualHex == null ? "Hex not found in grid!" : "Cell is already occupied!");
            return false;
        }
        actualHex.setOccupant(player);
        player.addCell();
        // MOVE PLACEMENT
        for (HexCube direction : HexCube.directions) {
            HexCube neighbor = getCellByCoordinates(
                    actualHex.q + direction.q,
                    actualHex.r + direction.r,
                    actualHex.s + direction.s
            );
            if (neighbor != null && neighbor.isOccupied() && neighbor.getOccupant() == player) {
                if (!player.isInGroup(actualHex)) {
                    player.addToGroup(actualHex, neighbor);
                } else {
                    Player.mergePlayerGroups(player1, actualHex, player1, neighbor);
                }
            }
        }
        if (!actualHex.getOccupant().isInGroup(actualHex)) {
            actualHex.getOccupant().newGroup(actualHex);
        }
        // CAPTURING PLACEMENT
        capturingPlacement(actualHex);
        return true;
    }

    public void capturingPlacement(HexCube actualHex) {
        Player enemy = (currentPlayer == player1) ? player2 : player1;
        Integer myGroupId = currentPlayer.getGroupId(actualHex);
        if (myGroupId == null) return;
        HashSet<HexCube> myGroup = currentPlayer.groups.get(myGroupId);
        boolean captureOccurred;
        do {
            captureOccurred = false;
            HashSet<Integer> adjacentEnemyGroupIds = new HashSet<>();
            for (HexCube hex : myGroup) {
                for (HexCube direction : HexCube.directions) {
                    HexCube neighbor = getCellByCoordinates(
                            hex.q + direction.q,
                            hex.r + direction.r,
                            hex.s + direction.s
                    );
                    if (neighbor != null && neighbor.isOccupied() && neighbor.getOccupant() == enemy) {
                        Integer enemyGroupId = enemy.getGroupId(neighbor);
                        if (enemyGroupId != null) adjacentEnemyGroupIds.add(enemyGroupId);
                    }
                }
            }
            for (Integer enemyGroupId : adjacentEnemyGroupIds) {
                HashSet<HexCube> enemyGroup = enemy.groups.get(enemyGroupId);
                if (enemyGroup != null && myGroup.size() > enemyGroup.size()) {
                    int n = enemyGroup.size();
                    HexCube representative = enemyGroup.iterator().next();
                    Player.mergePlayerGroups(currentPlayer, actualHex, enemy, representative);
                    currentPlayer.addCell(n);
                    enemy.addCell(-n);
                    captureOccurred = true;
                    myGroup = currentPlayer.groups.get(currentPlayer.getGroupId(actualHex));
                }
            }
        } while (captureOccurred);
    }

    public HexCube getCellByCoordinates(int q, int r, int s) {
        return hexMap.get(q + "," + r + "," + s);
    }

    private boolean checkWin() {
        if (round >= 2) {
            if (player1.getNumCells() == 0) { winner = player2; return true; }
            if (player2.getNumCells() == 0) { winner = player1; return true; }
        }
        boolean allOccupied = hexMap.values().stream().allMatch(HexCube::isOccupied);
        if (allOccupied) {
            int c1 = player1.getNumCells(), c2 = player2.getNumCells();
            if (c1 > c2) winner = player1;
            else if (c2 > c1) winner = player2;
            else winner = null;
            return true;
        }
        return false;
    }
}