import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class HexGrid extends JPanel implements MouseListener {
    private static BlockingQueue<HexCube> queue = new LinkedBlockingQueue<>() ;
    private ArrayList<ArrayList<Point>> grid;
    private Layout layout;
    private HexCube selectedHex = null;
    private ArrayList<HexCube> hexes;

    public HexGrid(ArrayList<HexCube> hexes, Layout layout) {
        this.hexes = hexes;
        this.layout = layout;
        addMouseListener(this);
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Loop over each HexCube in your grid.
        for (HexCube hex : hexes) {
            ArrayList<Point> corners = layout.polygonCorners(hex);
            int[] xPoints = new int[6];
            int[] yPoints = new int[6];
            for (int i = 0; i < 6; i++) {
                xPoints[i] = (int) Math.round(corners.get(i).x);
                yPoints[i] = (int) Math.round(corners.get(i).y);
            }

            // If the hex is occupied, fill it with the player's color.
            if (hex.isOccupied()) {
                String occupantColor = hex.getOccupant().getColour();
                if (occupantColor.equalsIgnoreCase("red")) {
                    g.setColor(Color.RED);
                } else if (occupantColor.equalsIgnoreCase("blue")) {
                    g.setColor(Color.BLUE);
                } else {
                    g.setColor(Color.GRAY); // Fallback for unknown colors.
                }
                g.fillPolygon(xPoints, yPoints, 6);
            }

            // Draw the hexagon outline.
            g.setColor(Color.BLACK);
            g.drawPolygon(xPoints, yPoints, 6);
        }
    }


    @Override
    public void mouseClicked(MouseEvent e) {
        int mouseX = e.getX();
        int mouseY = e.getY();
        FractionalHexCube fractionalHex = layout.pixelToHex(new Point(mouseX, mouseY));
        selectedHex = fractionalHex.hexRound();
        System.out.println("Clicked Hex: " + selectedHex.q + ", " + selectedHex.r + ", " + selectedHex.s);
        HexGrid.queue.offer(selectedHex);
        repaint();
    }

    public static HexCube clickedHex() throws InterruptedException {
        return queue.take();
    }

    @Override
    public void mousePressed(MouseEvent e) {}
    @Override
    public void mouseReleased(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}

}
