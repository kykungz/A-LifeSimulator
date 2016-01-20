/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package naturalselection;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

/**
 *
 * @author x64
 */
public class Plant extends Organism {

    public Plant(String specie, String food, String predator, int size, Color c, int speed) {
        super(specie, food, predator, size, c, speed);
        specie = "PLANT";
    }

    @Override
    public void render(Graphics g) {

        g.setColor(Color.green);
        g.fillOval(x, y, size, size);

    }
}
