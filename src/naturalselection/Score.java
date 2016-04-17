/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package naturalselection;

/**
 *
 * @author x64
 */
public class Score {

    private double UP = 0, DOWN = 0, LEFT = 0, RIGHT = 0;
    
    public double getUP() {
        return UP;
    }

    public double getDOWN() {
        return DOWN;
    }

    public double getLEFT() {
        return LEFT;
    }

    public double getRIGHT() {
        return RIGHT;
    }

    public void addUP(int amount) {
        if (amount != 0) {
            UP += 1d/amount;
        }
    }

    public void addDOWN(int amount) {
        if (amount != 0) {
            DOWN += 1d/amount;
        }
    }

    public void addLEFT(int amount) {
        if (amount != 0) {
            LEFT += 1d/amount;
        }
    }

    public void addRIGHT(int amount) {
        if (amount != 0) {
            RIGHT += 1d/amount;
        }
    }

    public void subtractUP(int amount) {
        if (amount != 0) {
            UP -= 1d/amount;
        }
    }

    public void subtractDOWN(int amount) {
        if (amount != 0) {
            DOWN -= 1d/amount;
        }
    }

    public void subtractLEFT(int amount) {
        if (amount != 0) {
            LEFT -= 1d/amount;
        }
    }

    public void subtractRIGHT(int amount) {
        if (amount != 0) {
            RIGHT -= 1d/amount;
        }
    }

    public void reset() {
        UP = 0;
        DOWN = 0;
        LEFT = 0;
        RIGHT = 0;
    }
}
