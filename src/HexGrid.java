import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class HexGrid extends JPanel implements MouseListener {
    static BlockingQueue<HexCube> queue = new LinkedBlockingQueue<>() ;
    private ArrayList<ArrayList<Point>> grid;
    private Layout layout;
    private HexCube selectedHex = null;

    public HexGrid(ArrayList<ArrayList<Point>> grid, Layout layout) {
        this.grid = grid;
        this.layout = layout;
        addMouseListener(this);
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLUE);

        for (ArrayList<Point> hex : grid) {
            Point first = hex.get(0);
            int prevX = (int) Math.round(first.x);
            int prevY = (int) Math.round(first.y);

            for (Point p : hex) {
                int x = (int) Math.round(p.x);
                int y = (int) Math.round(p.y);
                g.drawLine(prevX, prevY, x, y);
                prevX = x;
                prevY = y;
            }
            Point last = hex.getLast();
            g.drawLine(prevX, prevY, (int) Math.round(first.x), (int) Math.round(first.y));
        }

        if (selectedHex != null) {
            g.setColor(Color.RED);
            ArrayList<Point> selectedHexPoints = layout.polygonCorners(selectedHex);
            int[] xPoints = new int[6];
            int[] yPoints = new int[6];
            for (int i = 0; i < 6; i++) {
                xPoints[i] = (int) Math.round(selectedHexPoints.get(i).x);
                yPoints[i] = (int) Math.round(selectedHexPoints.get(i).y);
            }
            g.fillPolygon(xPoints, yPoints, 6);
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
