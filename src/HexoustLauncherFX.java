import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.Optional;

public class HexoustLauncherFX extends Application {
    @Override
    public void start(Stage primaryStage) {
        Platform.setImplicitExit(false);

        //main menu buttons
        Button startBtn = new Button("Start Game");
        Button exitBtn  = new Button("Exit");
        startBtn.setMaxWidth(Double.MAX_VALUE);
        exitBtn.setMaxWidth(Double.MAX_VALUE);

        startBtn.setOnAction(e -> showPlayerSetup(primaryStage));
        exitBtn .setOnAction(e -> primaryStage.hide());

        VBox menu = new VBox(10, startBtn, exitBtn);
        menu.setStyle(
                "-fx-padding: 40;" +
                        "-fx-alignment: center;" +
                        "-fx-pref-width: 300;" +
                        "-fx-pref-height: 200;"
        );

        Scene scene = new Scene(menu);
        primaryStage.setTitle("HEXOUST");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showPlayerSetup(Stage stage) {
        // player 1 name
        TextInputDialog dialog1 = new TextInputDialog();
        dialog1.setTitle("Player 1 Setup");
        dialog1.setHeaderText("Enter Player 1's name:");
        Optional<String> p1NameOpt = dialog1.showAndWait();
        if (!p1NameOpt.isPresent() || p1NameOpt.get().trim().isEmpty()) return;
        String p1Name = p1NameOpt.get().trim();

        // player 1 color
        ChoiceDialog<String> colorDialog = new ChoiceDialog<>("Red", Arrays.asList("Red", "Blue"));
        colorDialog.setTitle("Player 1 Setup");
        colorDialog.setHeaderText("Choose Player 1's color:");
        Optional<String> p1ColorOpt = colorDialog.showAndWait();
        if (!p1ColorOpt.isPresent()) return;
        String p1Color = p1ColorOpt.get();
        String p2Color = p1Color.equals("Red") ? "Blue" : "Red";

        // player 2 name
        TextInputDialog dialog2 = new TextInputDialog();
        dialog2.setTitle("Player 2 Setup");
        dialog2.setHeaderText("Enter Player 2's name (" + p2Color + "):");
        Optional<String> p2NameOpt = dialog2.showAndWait();
        if (!p2NameOpt.isPresent() || p2NameOpt.get().trim().isEmpty()) return;
        String p2Name = p2NameOpt.get().trim();


        stage.hide();

        // start a swing game on a background thread
        new Thread(() -> {
            try {
                Player player1 = new Player(p1Name, p1Color.toLowerCase());
                Player player2 = new Player(p2Name, p2Color.toLowerCase());
                new Game(player1, player2);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
