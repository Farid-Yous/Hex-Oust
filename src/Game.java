import java.util.Scanner;

public class Game {
    private Board board;
    private Player player1;
    private Player player2;
    private int round;
    private Player winner;
    Player current = player2;
    private Cell[][][] cells;
    public void gameInit() {
        board = new Board();
        round = 0;
        for(int i = -3; i <= 3; i++) {
            for(int j = -3; j <= 3; j++) {
                for(int k = -3; k <= 3; k++) {
                    Cell cell = new Cell(i, j, k);
                    cells[i][j][k] = cell;
                }
            }
        }
    }
    //Game is the centre of the control and will alternate turns and call functions related to managing the game
    public Game(){
        while(!CheckWin( player1, player2)) {
            playTurn(current);
            changeTurn();
        }
    }
    //changeTurn will change the current turn
    private void changeTurn(){
        if(current == player1) {
            current = player2;
        }
        else current = player1;

    }
    //play turn will take as input a player and will ask the player to click on the screen to play their turn, the information taken from the click will set the occupant of that cell to the player who clicked
    private void playTurn(Player player) {
        int x = 0, y = 0, z = 0;
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        //AWAB CODE FOR HANDLING MOUSE INPUT


        //Once input is taken in and we have co ordinates for the cell, we use it to set occupant
        cells[x][y][z].setOccupant(player);
    }
    //checkwin will check if, after each player has played at least once already,that they own no cells on the board, which means they have lost. If that is the case the win will be awarded to the other player
    private boolean CheckWin(Player player1, Player player2) {
        if(round < 2){
            return false;
        }
        if(player1.getNumCells() == 0){
            winner = player2;
            return true;
        }
        else if(player2.getNumCells() == 0){
            winner = player1;
            return true;
        }
        return false;
    }
}
