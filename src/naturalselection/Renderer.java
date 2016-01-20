/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package naturalselection;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import javax.swing.JPanel;

/**
 *
 * @author x64
 */
public class Renderer extends JPanel {

    NaturalSelection n;

    public Renderer(NaturalSelection n) {
        this.n = n;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawRect(10, 0,(int)Toolkit.getDefaultToolkit().getScreenSize().getWidth()
                , (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight()-100);
        Graphics2D g2 = (Graphics2D) g;
        n.render(g2);
    }
}
