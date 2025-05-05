import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HexCubeTest {
    // Test if Cube is initialised with correct value, should pass
    @Test
    void testInitialiseHexCube() {
        HexCube hex = new HexCube(1, -1, 0);
        assertEquals(1, hex.q);
        assertEquals(-1, hex.r);
        assertEquals(0, hex.s);
    }

    // Ensure a HexCube with invalid values(totals more than 0) is not created, should pass
    @Test
    void testInvalidHexCube() {
        assertThrows(IllegalArgumentException.class, () -> new HexCube(1, 1, 1));
    }

    // Ensure hex co-ordinates are added correctly, should pass
    @Test
    void testHexCubeAdd() {
        HexCube hexCube1 = new HexCube(1, -1, 0);
        HexCube hexCube2 = new HexCube(-1, 1, 0);
        HexCube sum = hexCube1.add(hexCube2);
        assertEquals(0, sum.q);
        assertEquals(0, sum.r);
        assertEquals(0, sum.s);
    }

    // Ensure subtract works correctly
    @Test
    void testHexCubeSubtract() {
        HexCube hex1 = new HexCube(1, -1, 0);
        HexCube hex2 = new HexCube(-1, 1, 0);
        HexCube result = hex1.subtract(hex2);

        assertEquals(2, result.q);
        assertEquals(-2, result.r);
        assertEquals(0, result.s);
    }

    // Ensure occupied flag is set correctly, should pass
    @Test
    void testOccupation() {
        HexCube hex = new HexCube(1, -1, 0);
        assertFalse(hex.isOccupied(), "Hex should be unoccupied in the beginning");

        Player player = new Player("A", "blue");
        hex.setOccupant(player);
        assertTrue(hex.isOccupied(), "Hex should be occupied");
    }

    // Ensure distance from centre is correctly calculated, should pass
    @Test
    void testHexCubeLength() {
        HexCube hex = new HexCube(4, -4, 0);
        assertEquals(4, hex.length(), "Incorrect distance from centre");
    }

    // Ensure distance between hex cubes is calculated correctly, should pass
    @Test
    void testHexCubeDistance() {
        HexCube hex1 = new HexCube(0, 0, 0);
        HexCube hex2 = new HexCube(2, -1, -1);

        assertEquals(2, hex1.distance(hex2), "Incorrect distance between hex");
    }

}