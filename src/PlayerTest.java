import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    // Ensure player is initialised correctly, should pass
    @Test
    void testPlayerInitialise() {
        Player player = new Player("A", "blue");
        assertEquals("A", player.getName());
        assertEquals("blue", player.getColour());
        assertEquals(0, player.getNumCells());
    }

    // Ensure player with Invalid colour is not initialised, should pass
    @Test
    void testInvalidPlayerColour() {
        assertThrows(IllegalArgumentException.class, () -> new Player("B", "yellow"));
    }

    // Ensure number of cells player controls is incremented correctly, should pass
    @Test
    void testAddCell() {
        Player player = new Player("B", "red");
        player.addCell();
        assertEquals(1, player.getNumCells());
    }
}