/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package naturalselection;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.Timer;

/**
 *
 * @author x64
 */
public class NaturalSelection implements ActionListener, KeyListener, MouseListener {

    private Renderer renderer;
    private Timer timer;
    public Organism wolf;
    public static ArrayList<Organism> organisms;
    public int x = 30;

    public static void main(String[] args) {
        NaturalSelection ns = new NaturalSelection();
        ns.start();
    }

    public NaturalSelection() {
        renderer = new Renderer(this);
        timer = new Timer(1 * x, this);
        organisms = new ArrayList<>();
    }

    public void create(String specie, String food, String predator, Color c, int speed, int amount) {
        for (int i = 0; i < amount; i++) {
            organisms.add(new Organism(specie, food, predator, 10, c, speed));
        }
    }

    public void start() {
        JFrame frame = new JFrame("Natural Selection Simulator by Kongpon");
        frame.addKeyListener(this);
        frame.addMouseListener(this);
        frame.setSize(500, 500);
        frame.add(renderer);
        renderer.setBackground(Color.white);
        frame.setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        create("WOLF", "sheep", null, Color.BLUE, 3, 1);
        create("SHEEP", null, "wolf", Color.red, 2, 80);
        timer.start();
        Thread n = new Thread() {
            @Override
            public void run() {
                while (true) {
                    long start  = System.currentTimeMillis();
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(NaturalSelection.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    renderer.repaint();
                    System.out.println(1000/(System.currentTimeMillis()-start)); 
                }
            }
        };
        n.start();
    }

    public void render(Graphics2D g) {
        for (Organism o : organisms) {
            o.render(g);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for (Iterator<Organism> it = organisms.iterator(); it.hasNext();) {
            Organism o = it.next();
            o.update();

            if (o.eatenBy() != null) {
                it.remove();
                //System.out.println("removed " + o.getSpecie());
            }
            if (o.getEnergy() <= 0) {
                it.remove();
                //System.out.println("removed " + o.getSpecie());
            }
            //o.render(g);
        }
        //renderer.repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
    boolean togglewolf = true;
    boolean togglesheep = true;

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_1) { // open WOLF
            for (Organism o : organisms) {
                if (o.getSpecie().equalsIgnoreCase("wolf")) {
                    o.open = togglewolf;
                }
            }
            togglewolf = !togglewolf;

        } else if (e.getKeyCode() == KeyEvent.VK_2) { // open SHEEP
            for (Organism o : organisms) {
                if (o.getSpecie().equalsIgnoreCase("sheep")) {
                    o.open = togglesheep;

                }
            }
            togglesheep = !togglesheep;
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            x += 5;
            timer.setDelay(x);

        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            if (x > 0) {
                x -= 5;
            }
            timer.setDelay(x);

        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void mouseClicked(java.awt.event.MouseEvent e) {
        create("WOLF", "sheep", null, Color.BLUE, 4, 1);
    }

    @Override
    public void mousePressed(java.awt.event.MouseEvent e) {
    }

    @Override
    public void mouseReleased(java.awt.event.MouseEvent e) {
    }

    @Override
    public void mouseEntered(java.awt.event.MouseEvent e) {
    }

    @Override
    public void mouseExited(java.awt.event.MouseEvent e) {
    }
}
