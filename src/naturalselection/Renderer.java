/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package naturalselection;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author x64
 */
public class Renderer extends JPanel {

    NaturalSelection n;
    public int screenWidth, screenHight;
    BufferedImage bg;

    public Renderer(NaturalSelection n) {
        this.n = n;
        this.screenWidth = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        this.screenHight = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
        this.bg = prepareBackground();
    }

    private BufferedImage prepareBackground() {
        BufferedImage grass_tile = null;
        try {
            grass_tile = ImageIO.read(this.getClass().getResource("/images/grass_tile.png"));
        } catch (IOException ex) {
            Logger.getLogger(Renderer.class.getName()).log(Level.SEVERE, null, ex);
        }
        BufferedImage background = new BufferedImage((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth(),
                (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = background.createGraphics();
        
        for (int row = 0; row < screenWidth/grass_tile.getWidth(); row++) {
            for (int column = 0; column < screenHight/grass_tile.getHeight(); column++ ) {
                g.drawImage(grass_tile, null, row*grass_tile.getHeight(),column*grass_tile.getWidth());
            }
        }
        return background;

    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        super.paintComponent(g2);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.drawImage(bg, null, 0, 0);
        //g2.drawRect(10, 0, (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth(), (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() - 100);
        n.render(g2);
    }
}
