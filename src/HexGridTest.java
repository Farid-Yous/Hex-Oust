import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;
import java.awt.event.MouseEvent;

class HexGridTest {

    // Ensure mouse clicking functions correctly
    @Test
    void testMouseClicked() throws InterruptedException {

        // Mock HexGrid Layout
        ArrayList<ArrayList<Point>> grid = new ArrayList<>();
        Layout layout = new Layout(Layout.flat, new Point(20, 20), new Point(600, 300));
        HexGrid hexGrid = new HexGrid(grid, layout);

        // Simulate mouse clicked at 620(x) 320(y)
        MouseEvent mouseClick = new MouseEvent(hexGrid, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), 0, 620, 320, 1, false);

        // Call mouse clicked method
        hexGrid.mouseClicked(mouseClick);

        // Pull clicked hex
        HexCube hexClicked = HexGrid.queue.poll();

        // Ensure clicked hex was in the queue
        assertNotNull(hexClicked, "Hex not in queue");
        // Ensure the clicked hex cube is converted and rounded correctly
        FractionalHexCube fractionalHex = layout.pixelToHex(new Point(620, 320));
        HexCube roundedHex = fractionalHex.hexRound();
        assertEquals(roundedHex.q, hexClicked.q, "Wrong q co-ordinate");
        assertEquals(roundedHex.r, hexClicked.r, "Wrong r co-ordinate.");
        assertEquals(roundedHex.s, hexClicked.s, "Wrong s co-ordinate.");
    }
}
