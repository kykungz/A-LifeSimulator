/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package naturalselection;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author x64
 */
public class Organism {

    public static enum GENDER {

        MALE, FEMALE;
    }
    private Random rand;

    public int getEnergy() {
        return energy;
    }
    protected String specie;
    protected GENDER gender;
    protected int age;
    protected int x, y, speed;
    protected boolean movingX;
    protected int direction = 1;
    protected Color color;
    protected int energy;
    protected int radar;
    protected String food;
    protected String predator;
    protected int size;
    protected Organism leader;
    protected int gap = 16;
    //
    public boolean open = false;
    //

    public Organism getLeader() {
        return leader;
    }

    public String getSpecie() {
        return specie;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Organism(String specie, String food, String predator, int size, Color c, int speed) {
        rand = new Random();
        x = rand.nextInt(1500 - 200);
        y = rand.nextInt(800 - 200);
        this.speed = speed;
        this.gender = GENDER.values()[rand.nextInt(GENDER.values().length)];
        this.specie = specie;
        this.color = c;
        this.food = food;
        this.predator = predator;
        this.size = 6;
        this.energy = 5000;
        this.radar = rand.nextInt(size * 10) + 1;
    }

    public void update() {
        if (getSpecie().equalsIgnoreCase("wolf")) {
            energy--;
            if (energy >= 500) {
                energy = 500;
            } else if (energy < 0) {
                energy = 0;
            }
        }
        ArrayList<Organism> inradar = inRadar();
        ArrayList<Organism> localFriends = getLocalFriends();
        if (!inradar.isEmpty()) { // found something in radar
            direction = direction * -1;
            interact(inradar);
        } else if (!localFriends.isEmpty()) {
            follow(localFriends);
        } else {
            moveRandomly();
        }

    }

    public void follow(ArrayList<Organism> localFriends) {
        updateLeader(localFriends);
        if (leader == null) {
            moveRandomly();
            return;
        }
        int UP = 0;
        int DOWN = 0;
        int LEFT = 0;
        int RIGHT = 0;
        if (x > leader.getX()) {
            LEFT++;
        } else if (x < leader.getX()) {
            RIGHT++;
        }
        if (y > leader.getY()) {
            UP++;
        } else if (y < leader.getY()) {
            DOWN++;
        }
        avoidCrowding(localFriends, UP, DOWN, LEFT, RIGHT);
    }

    public void avoidCrowding(ArrayList<Organism> localFriends, int UP, int DOWN, int LEFT, int RIGHT) {
        if (getSurroundingFriends().isEmpty()) {
            calculate(UP, DOWN, LEFT, RIGHT);
            return;
        }
        for (Organism friend : getSurroundingFriends()) {
            if (friend != leader && friend.getLeader() == leader) {
                if (x > friend.getX()) {
                    RIGHT++;
                } else {
                    LEFT++;
                }
                if (y > friend.getX()) {
                    DOWN++;
                } else {
                    UP++;
                }
            }
        }
        calculate(UP, DOWN, LEFT, RIGHT);

    }

    public void updateLeader(ArrayList<Organism> localFriends) {
        if (leader == null) {
            for (Organism friend : localFriends) {
                if (friend.getRadar().getWidth() > this.getRadar().getWidth()) {
                    leader = friend;
                    break;
                }
            }
            if (leader == null) {
                return;
            }
        } else if (leader.getLeader() != null) {
            leader = leader.getLeader();
        }

        for (Organism friend : localFriends) {
            if (friend != leader && friend.getRadar().getWidth() > leader.getRadar().getWidth()) {
                leader = friend;
            }
        }

    }

    public void offScreenFix() {
        if (x < 10) { // Left corner
            movingX = true;
            direction = 1;
        } else if (x + size > Toolkit.getDefaultToolkit().getScreenSize().getWidth()) { // RIGHT corner
            movingX = true;
            direction = -1;
        }
        if (y < 0) { // Top corner
            movingX = false;
            direction = 1;
        } else if (y + size > Toolkit.getDefaultToolkit().getScreenSize().getHeight() - 100) { // bottom corner
            movingX = false;
            direction = -1;
        }
    }

    public Organism eatenBy() {
        for (Organism o : NaturalSelection.organisms) {
            if (o.getSpecie().equalsIgnoreCase(predator) && o.getBound().intersects(this.getBound())) {
                o.energy += 300;
                return o;
            }
        }
        return null;
    }

    public void interact(ArrayList<Organism> list) {
        int UP = 0;
        int DOWN = 0;
        int LEFT = 0;
        int RIGHT = 0;
        for (Organism o : list) {
            if (isPredator(o)) { // run !
                //System.out.println(this.getSpecie() + " run !");
                if (x > o.getX()) {
                    RIGHT++;
                } else if (x < o.getX()) {
                    LEFT++;
                }
                if (y > o.getY()) {
                    DOWN++;
                } else if (y < o.getY()) {
                    UP++;
                }
            } else if (isPrey(o)) { // chase !
                //System.out.println(this.getSpecie() + " chase !");
                if (x > o.getX()) {
                    LEFT++;
                } else if (x < o.getX()) {
                    RIGHT++;
                }
                if (y > o.getY()) {
                    UP++;
                } else if (y < o.getY()) {
                    DOWN++;
                }
            }
        }

        calculate(UP, DOWN, LEFT, RIGHT);

    }

    public void calculate(int UP, int DOWN, int LEFT, int RIGHT) {
        if (DOWN > UP) {
            y += speed;
        } else if (DOWN < UP) {
            y -= speed;
        }
        if (LEFT > RIGHT) {
            x -= speed;
        } else if (LEFT < RIGHT) {
            x += speed;
        }
    }

    public Point foreProsition(int UP, int DOWN, int LEFT, int RIGHT) {
        int testX = x;
        int testY = y;
        if (DOWN > UP) {
            //System.out.println(specie + " is moving DOWN");
            testY += speed;
        } else if (DOWN < UP) {
            //.out.println(specie + " is moving UP");
            testY -= speed;
        }
        if (LEFT > RIGHT) {
            //System.out.println(specie + " is moving LEFT");
            testX -= speed;
        } else if (LEFT < RIGHT) {
            //System.out.println(specie + " is moving RIGHT");
            testX += speed;
        }
        return new Point(testX, testY);
    }

    public boolean isPredator(Organism o) {
        boolean x = o.getSpecie().equalsIgnoreCase(this.predator);
        //System.out.println("Checking isPredator = " + x + " --> [" + o.getSpecie() + "] [" + predator + "]");
        return x;
    }

    public boolean isPrey(Organism o) {
        return o.getSpecie().equalsIgnoreCase(this.food);
    }

    public boolean isFriend(Organism o) {
        return o.getSpecie().equalsIgnoreCase(this.specie);
    }

    public ArrayList<Organism> getLocalFriends() {
        ArrayList<Organism> localFriends = new ArrayList<>();
        for (Organism o : NaturalSelection.organisms) {
            if (o.getBound().intersects(getFriendRadar()) && o != this && o.getSpecie().equalsIgnoreCase(specie)) {
                localFriends.add(o);
            }
        }
        return localFriends;

    }

    public ArrayList<Organism> getSurroundingFriends() {
        ArrayList<Organism> friends = new ArrayList<>();
        Rectangle area = new Rectangle(x - (gap - size) / 2, y - (gap - size) / 2, gap, gap);
        for (Organism o : NaturalSelection.organisms) {
            if (o != this && o != leader && o.getSpecie().equalsIgnoreCase(specie) && o.getBound().intersects(area)) {

                friends.add(o);

            }
        }
        return friends;
    }

    public Rectangle getFriendRadar() {
        return new Rectangle(x - (9 * size) / 2, y - (9 * size) / 2, 10 * size, 10 * size);
    }

    public ArrayList<Organism> inRadar() {
        ArrayList<Organism> list = new ArrayList<>();
        Rectangle radarRadius = getRadar();
        for (Organism o : NaturalSelection.organisms) {
            if (o.getBound().intersects(radarRadius) && !o.getSpecie().equalsIgnoreCase(specie)) {
                list.add(o);
            }
        }

        return list;
    }

    public Rectangle getRadar() {
        return new Rectangle(x - (radar - size) / 2, y - (radar - size) / 2, radar, radar);
    }

    public void moveRandomly() {
        int chosen = rand.nextInt(100);
        if (chosen < 4) {
            movingX = !movingX;
            if (rand.nextBoolean()) {
                direction = direction * -1;
            }
        }
        if (movingX) {
            x += speed * direction;
        } else {
            y += speed * direction;
        }
    }

    public void render(Graphics2D g) {
        if (open) {
            g.setColor(new Color(255, 250, 10, 50));
            g.fillRect((int) getRadar().getX(), (int) getRadar().getY(), radar, radar);
            if (leader != null) {
                g.setColor(Color.black);
                g.drawLine(x, y, leader.getX(), leader.getY());
            }
        }
        if (getSpecie().equalsIgnoreCase("wolf")) {
            g.setFont(new Font("tahoma", 0, 16));
            g.setColor(Color.black);
            g.drawString(energy + "", x, y);
        }
        g.setColor(color);
        if (leader != null) {
            g.setColor(Color.orange);
        }
        g.fillOval(x, y, size, size);
        g.setColor(Color.black);
        g.drawOval(x, y, size, size);

    }

    public Rectangle getBound() {
        return new Rectangle(x, y, size, size);
    }
}
//        previousFacing = facing;
//        if (rand.nextBoolean()) {
//            do {
//                facing = rand.nextInt(8);
//            } while (facing == previousFacing);
//        }
//        switch (facing) {
//            case 0:
//                x += speed;
//                break;
//            case 1:
//                x += speed;
//                y += speed;
//                break;
//            case 2:
//                y += speed;
//                break;
//            case 3:
//                x -= speed;
//                y += speed;
//                break;
//            case 4:
//                y -= speed;
//                break;
//            case 5:
//                y -= speed;
//                x -= speed;
//                break;
//            case 6:
//                y -= speed;
//                break;
//            case 7:
//                y -= speed;
//                x += speed;
//                break;
//        }
//        Point p = MouseInfo.getPointerInfo().getLocation();
//        if (x < p.getX()) {
//            x += speed;
//        } else if (x > p.getX()) {
//            x -= speed;
//        } 
//        if (y < p.getY()) {
//            y += speed;
//
//        } else if (y > p.getY()) {
//            y -= speed;
//        }