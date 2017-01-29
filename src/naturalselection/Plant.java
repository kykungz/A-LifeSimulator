/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package naturalselection;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author x64
 */
public class Plant extends Organism {

    private BufferedImage tree;

    public Plant(String specie, String food, String predator, int size, Color c, int speed) {

        super(specie, food, "sheep", size, c, speed);
        this.specie = "tree";
        try {
            tree = ImageIO.read(this.getClass().getResource("/images/tree_tile.png"));
        } catch (IOException ex) {
            Logger.getLogger(Plant.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void updateEnergy() {
        super.updateEnergy(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int compareTo(Organism o) {
        //if (o instanceof Plant) {
            if (y > o.y) {
                return 1;
            } else if (y < o.y) {
                return -1;
            } else {
                return 0;
            }
        //}
        //return 1;

    }

    @Override
    public void render(Graphics2D g) {
//        g.setColor(Color.green);
//       g.fillOval(x, y, 20, 20);
        g.drawImage(tree, null, x, y);

    }

    @Override
    public void update() {
        energy = 10;
        age = 0;
    }

    @Override
    public Rectangle getBound() {
        return new Rectangle(x, y, 20, 20);
    }
}
