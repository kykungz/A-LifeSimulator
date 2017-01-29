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
public class Organism implements Comparable<Organism> {

    @Override
    public int compareTo(Organism o) {
        if (y > o.y) {
            return 1;
        } else if (y < o.y) {
            return -1;
        } else {
            return 0;
        }
    }

    public static enum GENDER {

        MALE, FEMALE;
    }
    private Random rand;

    public int getEnergy() {
        return energy;
    }
    protected boolean breedNow = false;
    protected Score score;
    protected String specie;
    protected GENDER gender;
    protected int age;
    protected int x, y, speed;
    protected boolean movingX;
    protected int direction = 1;
    protected Color color;
    protected int energy;
    protected int radar;
    protected String prey;
    protected String predator;
    protected int size;
    protected Organism leader;
    protected int gap = 20;
    protected int largestDistant = 20;
    protected Point home;
    protected Point food;
    protected int MAX_ENERGY = 300;
    protected int MAX_AGE = 3000;//(int) Math.pow(10, 8);
    protected int remainingTime;
    protected int breeded = 0;
    //
    public boolean open = false;
    //

    public Organism getLeader() {
        return leader;
    }

    public String getSpecie() {
        return specie;
    }

    public int getCenterX() {
        return x + size / 2;
    }

    public int getCenterY() {
        return y + size / 2;
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
//        x = 700;
//        y = 400;
        this.speed = speed;
        this.gender = GENDER.values()[rand.nextInt(GENDER.values().length)];
        this.specie = specie;
        this.color = c;
        this.prey = food;
        this.predator = predator;
        this.size = 8;
        this.energy = MAX_ENERGY / 3;
        this.radar = rand.nextInt(size * 10) + 1;
        this.score = new Score();
        this.remainingTime = MAX_AGE;
    }

    public void updateEnergy() {
        energy--;
        if (energy >= MAX_ENERGY) {
            energy = MAX_ENERGY;
        } else if (energy < 0) {
            energy = 0;
        }
    }

    private void updateAge() {
        remainingTime--;
    }

    private int getAge() {
        return MAX_AGE - remainingTime;
    }

    public void update() {
        updateAge();
        updateEnergy();
        ArrayList<Organism> inradar = inRadar();
        ArrayList<Organism> localFriends = getLocalFriends();
        if (!localFriends.isEmpty()) {
            updateLeader(localFriends);
        }
        if (!inradar.isEmpty()) { // found preys or predators in radar
            //direction = direction * -1;
            //interact(inradar);
            interact2(inradar);
        } else if (isHungry() && food != null) { // hungry but no food around
            if (in(getFoodArea())) { // in marked food but no food
                food = null;
            } else {
                moveTo(food);
            }
        } else if (readyToReproduce()) {
            reproduce();
        } else if (leader != null) { // have leader
            follow(localFriends);
            //followLeader2();
        } else {
            moveRandomly();
        }
        markFood(inradar);
        offScreenFix();
    }

    public void followLeader2() {
        System.out.println("follow");
        boolean[] nsew = getCollision();
        if (x < leader.getCenterX() && !nsew[2]) {
            x += speed;
        } else if (x > leader.getCenterX() && !nsew[3]) {
            x -= speed;
        }
        if (y < leader.getCenterY() && !nsew[1]) {
            y += speed;
        } else if (y > leader.getCenterY() && !nsew[0]) {
            y -= speed;
        }
    }

    public void update2() {
        energy--;
        if (energy >= 500) {
            energy = 500;
        } else if (energy < 0) {
            energy = 0;
        }

        ArrayList<Organism> inradar = inRadar();
        ArrayList<Organism> localFriends = getLocalFriends();

        if (!localFriends.isEmpty()) {
            updateLeader(localFriends);
        }

        if (!inradar.isEmpty()) { // found prey or predator in radar
            //direction = direction * -1;
            //interact(inradar);
            interact2(inradar);
        } //        else if (leader != null) {
        //            followLeader();
        //        }
        else if (food != null && isHungry() && in(getFoodArea())) {
            System.out.println("in food");
            food = null;
            moveRandomly();
        } else if (isHungry() && food != null) {
            moveTo(food);
        } else {
            moveRandomly();
        }

    }

    public void interact2(ArrayList<Organism> inradar) {
        Organism nearestPredator = getNearestPredator(inradar);
        Organism nearestPrey = getNearestPrey(inradar);
        if (nearestPredator != null) {
            // System.out.println("escape");
            escape(nearestPredator);
        } else if (nearestPrey != null && isHungry()) {
            //  System.out.println("chase");
            chase(nearestPrey);
        } //        else if (nearestPrey == null && isHungry() && food != null) {
        //            // go to marked food source
        //            moveTo(food);
        //            // System.out.println("move");
        //
        //        } 
        else {
            // System.out.println("rand");

            moveRandomly();
        }
//        if (nearestPrey != null) {
//            // System.out.println("mark");
//            markFood(nearestPrey.getCenterX(), nearestPrey.getCenterY());
//        }

    }

    public void reproduce() {
        Organism mate;
        if ((mate = getNearestMate(getLocalFriends())) != null) {
            if (!in(mate.getBound())) {
                moveTo(new Point(mate.getCenterX(), mate.getCenterY()));
            } else {
                breedNow = true;
            }
        } else {
            moveRandomly();
        }
    }

    public void breed() {
        breeded ++;
        breedNow = false;
        energy = energy / 2;
    }

    public boolean readyToReproduce() {
        return energy > (0.6d * (double) MAX_ENERGY) && breeded < 5 && getAge() > 300;
    }

    public Rectangle getFoodArea() {
        return new Rectangle((int) food.getX(), (int) food.getY(), 20, 20);
    }

    public boolean in(Rectangle rect) {
        return this.getBound().intersects(rect);
    }

    public boolean at(Point target) {
        return (x == (int) target.getX()) && (y == (int) target.getY());
    }

    public void moveTo(Point target) {
        x += speed * Integer.compare((int) target.getX(), x);
        y += speed * Integer.compare((int) target.getY(), y);
    }

    public Organism getNearestMate(ArrayList<Organism> organisms) {
        Organism nearestMate = null;
        for (Organism o : organisms) {
            if (o.getSpecie().equalsIgnoreCase(specie) && o.readyToReproduce()) {
                if (nearestMate != null) {
                    if (getDistant(o) < getDistant(nearestMate)) {
                        nearestMate = o;
                    }
                } else {
                    nearestMate = o;
                }
            }
        }
        return nearestMate;
    }

    public Organism getNearestFriend(ArrayList<Organism> organisms) {
        Organism nearestFriend = null;
        for (Organism o : organisms) {
            if (o.getSpecie().equalsIgnoreCase(specie)) {

                if (nearestFriend != null) {
                    if (getDistant(o) < getDistant(nearestFriend)) {
                        nearestFriend = o;
                    }
                } else {
                    nearestFriend = o;
                }
            }
        }
        return nearestFriend;
    }

    public Organism getNearestPredator(ArrayList<Organism> organisms) {
        Organism nearestPredator = null;
        for (Organism o : organisms) {
            if (isPredator(o)) {
                if (nearestPredator != null) {
                    if (getDistant(o) < getDistant(nearestPredator)) {
                        nearestPredator = o;
                    }
                } else {
                    nearestPredator = o;
                }

            }
        }
        return nearestPredator;

    }

    public Organism getNearestPrey(ArrayList<Organism> organisms) {
        Organism nearestPrey = null;
        for (Organism o : organisms) {
            if (isPrey(o)) {
                if (nearestPrey != null) {
                    if (getDistant(o) < getDistant(nearestPrey)) {
                        nearestPrey = o;
                    }
                } else {
                    nearestPrey = o;
                }

            }
        }
        return nearestPrey;

    }

    public double getDistant(Organism o) {
        int a = (int) Math.pow(getDistantY(o), 2);
        int b = (int) Math.pow(getDistantX(o), 2);
        return Math.sqrt(a + b);
    }

    public int getDistantX(Organism o) {
        return Math.abs(o.getX() - x);
    }

    public int getDistantY(Organism o) {
        return Math.abs(o.getY() - y);
    }

    public void eat(Organism o) {
        o.energy = 0;
        energy = energy + 200;
    }

    public boolean isDead() {
        return (energy <= 0) || (remainingTime <= 0);
    }

    public boolean isEatable(Organism o) {
        if (o.getBound().intersects(this.getBound())) {
            return true;
        }
        return false;
    }

    public void chase(Organism o) {
        moveTo(new Point(o.getCenterX(), o.getCenterY()));
        if (isEatable(o)) {
            eat(o);
        }

    }

    public void escape(Organism o) {
        direction = direction * -1;

        if (x < o.getCenterX()) {
            x -= speed;
        } else if (x > o.getCenterX()) {
            x += speed;
        }
        if (y < o.getCenterY()) {
            y -= speed;
        } else if (y > o.getCenterY()) {
            y += speed;
        }
    }

    public void rest() {
    }

    public void markFood(ArrayList<Organism> inradar) {
        Organism nearestPrey;
        if ((nearestPrey = getNearestPrey(inradar)) != null) {
            markFood(nearestPrey.getCenterX(), nearestPrey.getCenterY());
        }
    }

    public void markFood(int foodx, int foody) {
        food = new Point(foodx, foody);
    }

    public void markHome(int homex, int homey) {
    }

    public ArrayList<Organism> getPreys(ArrayList<Organism> organisms) {
        ArrayList<Organism> preys = new ArrayList<>();
        for (Organism o : organisms) {
            if (isPrey(o)) {
                preys.add(o);
            }
        }
        return preys;

    }

    public ArrayList<Organism> getPredators(ArrayList<Organism> organisms) {
        ArrayList<Organism> predators = new ArrayList<>();
        for (Organism o : organisms) {
            if (isPredator(o)) {
                predators.add(o);
            }
        }
        return predators;
    }

    public void followLeader() {
        int LEFT = 0;
        int RIGHT = 0;
        int UP = 0;
        int DOWN = 0;
        if (Math.abs(x - leader.getX()) > largestDistant || Math.abs(y - leader.getY()) > largestDistant) {
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
            calculate(UP, DOWN, LEFT, RIGHT);

        } else {
            moveRandomly();
        }
        //offScreenFix();
    }

    public void moveRandomlyRespectToLeader() {
        int chosen = rand.nextInt(100);
        if (chosen < 4) {
            movingX = !movingX;
            if (rand.nextBoolean()) {
                direction = direction * -1;
            }
        }
        if (leader.movingX) {
            x += speed * leader.direction;
        } else {
            y += speed * leader.direction;
        }
    }

    public void moveRandomly2() {
        int choice = rand.nextInt(8);
        switch (choice) {
            case 0:
                x += speed;
                break;
            case 1:
                x += speed;
                y += speed;
                break;
            case 2:
                y += speed;
                break;
            case 3:
                x -= speed;
                y += speed;
                break;
            case 4:
                y -= speed;
                break;
            case 5:
                y -= speed;
                x -= speed;
                break;
            case 6:
                y -= speed;
                break;
            case 7:
                y -= speed;
                x += speed;
                break;
        }
    }

    public void follow(ArrayList<Organism> localFriends) {
        //updateLeader(localFriends);
//        if (leader == null) {
//            moveRandomly();
//            return;
//        }
        if (x > leader.getX()) {
            score.addLEFT(getDistantX(leader));
        } else if (x < leader.getX()) {
            score.addRIGHT(getDistantX(leader));
        }
        if (y > leader.getY()) {
            score.addUP(getDistantY(leader));
        } else if (y < leader.getY()) {
            score.addDOWN(getDistantY(leader));
        }
        //avoidCrowding(localFriends, UP, DOWN, LEFT, RIGHT);
        avoidCrowding();
        //calculate2();

    }

    public void calculate2() {
    }

    public void avoidCrowding() {
        if (leader == null) {
            return;
        }
        for (Organism friend : getSurroundingFriends()) {
            if (sameGroup(friend)) {
                if (x > friend.getX()) {
                    score.addRIGHT(getDistantX(friend));
                } else {
                    score.addLEFT(getDistantX(friend));
                }
                if (y > friend.getY()) {
                    score.addDOWN(getDistantY(friend));
                } else {
                    score.addUP(getDistantY(friend));
                }
            }
        }
        calculate();
    }

    public boolean sameGroup(Organism o) {
        return o.leader == leader;
    }

    public void avoidCrowding(ArrayList<Organism> localFriends, int UP, int DOWN, int LEFT, int RIGHT) {
        if (leader == null) {
            return;
        }
        if (getSurroundingFriends().isEmpty()) {
            calculate(UP, DOWN, LEFT, RIGHT);
            return;
        }
        for (Organism friend : getSurroundingFriends()) {
            if (friend != leader && friend.getLeader() == leader) {
                if (x > friend.getX()) {
                    RIGHT = (int) (RIGHT + (1d / Math.abs(x - leader.getX())) * 100000);
                } else {
                    LEFT = (int) (LEFT + (1d / Math.abs(x - leader.getX())) * 100000);
                }
                if (y > friend.getY()) {
                    DOWN = (int) (DOWN + (1d / Math.abs(y - leader.getY())) * 100000);
                } else {
                    UP = (int) (UP + (1d / Math.abs(y - leader.getY())) * 100000);
                }
            }
        }
        calculate(UP, DOWN, LEFT, RIGHT);

    }

    public void interact(ArrayList<Organism> list) {
        for (Organism o : list) {
            if (isPredator(o)) { // run !
                if (x < o.getCenterX()) {
                    score.addLEFT(getDistantX(o));
                } else {
                    score.addRIGHT(getDistantX(o));
                }
                if (y < o.getCenterY()) {
                    score.addUP(getDistantY(o));
                } else {
                    score.addDOWN(getDistantY(o));
                }
            } else if (isPrey(o) && isHungry()) { // chase !
                if (getBound().intersects(o.getBound())) {
                    eat(o);
                }
                if (x < o.getCenterX()) {
                    score.addRIGHT(getDistantX(o));
                } else {
                    score.addLEFT(getDistantX(o));
                }
                if (y < o.getCenterY()) {
                    score.addDOWN(getDistantY(o));
                } else {
                    score.addUP(getDistantY(o));
                }
            } else {
                moveRandomly();
                return;
            }
        }

        calculate();

    }

    public boolean isHungry() {
        return energy < 200;
    }

    public void calculate(int UP, int DOWN, int LEFT, int RIGHT) {
        if (specie.equalsIgnoreCase("wolf")) {
            //System.out.println("UP = " + UP + " DOWN = " + DOWN + " LEFT = " + LEFT + " RIGHT = " + RIGHT);
        }
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

    public void calculate() {

        int resultY = -Double.compare(score.getUP(), score.getDOWN());
        int resultX = -Double.compare(score.getLEFT(), score.getRIGHT());
        y += speed * (resultY);
        x += speed * (resultX);

//        if (score.getUP() > score.getDOWN()) {
//            y -= speed;
//        } else if (score.getUP() < score.getDOWN()) {
//            y += speed;
//        }
//        if (score.getLEFT() > score.getRIGHT()) {
//            x -= speed;
//        } else if (score.getLEFT() < score.getRIGHT()) {
//            x += speed;
//        }
        score.reset();
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
        if (leader.isDead()) {
            leader = null;
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
        //System.out.println("Checking isPredator = " + updateRate + " --> [" + o.getSpecie() + "] [" + predator + "]");
        return x;
    }

    public boolean isPrey(Organism o) {
        return o.getSpecie().equalsIgnoreCase(this.prey);
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
        for (Organism o : NaturalSelection.organisms) {
            if (o != this && o != leader && o.getSpecie().equalsIgnoreCase(specie) && o.getBound().intersects(getAvoidingRadar())) {
                friends.add(o);
            }
        }
        return friends;
    }

    public Rectangle getAvoidingRadar() {
        return new Rectangle(x - (gap - size) / 2, y - (gap - size) / 2, gap, gap);

    }

    public Rectangle getFriendRadar() {
        return new Rectangle(x - (9 * size) / 2, y - (9 * size) / 2, 10 * size, 10 * size);
    }

    public ArrayList<Organism> inRadar() {
        ArrayList<Organism> list = new ArrayList<>();
        for (Organism o : NaturalSelection.organisms) {
            if (o.getBound().intersects(getRadar()) && !o.getSpecie().equalsIgnoreCase(specie) && (isPredator(o) || isPrey(o))) {
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
public Color getColor() {
    return this.color;
}
    
    public void render(Graphics2D g) {

        if (open) {
            g.setColor(new Color(255, 250, 10, 50));
            g.fillRect((int) getRadar().getX(), (int) getRadar().getY(), radar, radar);
            //g.fillRect((int) getAvoidingRadar().getX(), (int) getAvoidingRadar().getY(), (int) getAvoidingRadar().getWidth(), (int) getAvoidingRadar().getWidth());
            if (leader != null) {
                g.setColor(Color.black);
                g.drawLine(x, y, leader.getX(), leader.getY());
            }
            if (food != null) {
                g.setColor(Color.blue);
                g.fill(getFoodArea());
            }
            g.drawString(String.valueOf(getAge()), x, y);
            g.drawRect(getFriendRadar().x, getFriendRadar().y, getFriendRadar().width, getFriendRadar().height);

        }
//        if (getSpecie().equalsIgnoreCase("wolf")) {
//            g.setFont(new Font("tahoma", 0, 16));
//            g.setColor(Color.black);
//            g.drawString(energy + "", x, y);
//        }

        g.setColor(color);
        if (leader != null) {
            //g.setColor(Color.orange);
        }
        g.fillRect(x, y, size, size);
        g.setColor(Color.black);
        g.drawRect(x, y, size, size);
//        if (isHungry() && 1 != 1) {
//            g.setColor(Color.green);
//            g.fillRect(x, y, size, size);
//        }
//        if (readyToReproduce()) {
//            g.setColor(Color.pink);
//            g.fillRect(x, y, size, size);
//        }

    }

    public boolean[] getCollision() {
        boolean n = false, s = false, e = false, w = false;
        for (Organism o : getSurroundingFriends()) {
            if (getNBound().intersects(o.getBound()) && !n) {
                n = true;
            }
            if (getSBound().intersects(o.getBound()) && !s) {
                s = true;
            }
            if (getEBound().intersects(o.getBound()) && !e) {
                e = true;
            }
            if (getWBound().intersects(o.getBound()) && !w) {
                w = true;
            }
        }
        System.out.println(n + ", " + s + ", " + e + ", " + w);
        return new boolean[]{n, s, e, w};
    }

    public Rectangle getNBound() {
        return new Rectangle(x, y, size, 1);
    }

    public Rectangle getSBound() {
        return new Rectangle(x, y + size - 1, size, 1);
    }

    public Rectangle getEBound() {
        return new Rectangle(x + size - 1, y, 1, size);
    }

    public Rectangle getWBound() {
        return new Rectangle(x, y, 1, size);
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
//                updateRate += speed;
//                break;
//            case 1:
//                updateRate += speed;
//                y += speed;
//                break;
//            case 2:
//                y += speed;
//                break;
//            case 3:
//                updateRate -= speed;
//                y += speed;
//                break;
//            case 4:
//                y -= speed;
//                break;
//            case 5:
//                y -= speed;
//                updateRate -= speed;
//                break;
//            case 6:
//                y -= speed;
//                break;
//            case 7:
//                y -= speed;
//                updateRate += speed;
//                break;
//        }
//        Point p = MouseInfo.getPointerInfo().getLocation();
//        if (updateRate < p.getX()) {
//            updateRate += speed;
//        } else if (updateRate > p.getX()) {
//            updateRate -= speed;
//        } 
//        if (y < p.getY()) {
//            y += speed;
//
//        } else if (y > p.getY()) {
//            y -= speed;
//        }
