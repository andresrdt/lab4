package edu.eci.arsw.highlandersim;

import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Immortal extends Thread {

    private ImmortalUpdateReportCallback updateCallback = null;

    private Integer health;

    private int defaultDamageValue;

    private final List<Immortal> immortalsPopulation;

    private final String name;

    private final Integer life;

    private final Random r = new Random(System.currentTimeMillis());

    private boolean flag = true;
    public static final Object Deadlock = new Object();
    public volatile boolean espere;

    public Immortal(String name, List<Immortal> immortalsPopulation, int health, int defaultDamageValue, ImmortalUpdateReportCallback ucb, Integer life) {
        super(name);
        this.updateCallback = ucb;
        this.name = name;
        this.immortalsPopulation = immortalsPopulation;
        this.health = health;
        this.defaultDamageValue = defaultDamageValue;
        this.life = life;
        this.espere = false;
    }

    public void resumen() {
        flag = true;
    }

    public void pause() {
        flag = false;
    }

    public void run() {
        while (true) {
            if (flag ) {
                Immortal im;
                int myIndex = immortalsPopulation.indexOf(this);

                int nextFighterIndex = r.nextInt(immortalsPopulation.size());

                //avoid self-fight
                if (nextFighterIndex == myIndex) {
                    nextFighterIndex = ((nextFighterIndex + 1) % immortalsPopulation.size());
                }

                im = immortalsPopulation.get(nextFighterIndex);
                if (im.health > 0) {
                    if (this.espere || im.espere) {
                        im.espere=false;
                        try {
                            this.currentThread().sleep(100);
                            this.espere=false;
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    } else {
                        this.fight(im);

                    }
                }
            } else {
                synchronized (life) {
                    try {
                        life.wait();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Immortal.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }

    }

    public void fight(Immortal i2) {
        if (this.health > i2.getHealth()) {
            synchronized (health) {
                synchronized (i2.health) {
                    if (i2.getHealth() > 0) {

                        i2.changeHealth(i2.getHealth() - defaultDamageValue);
                        this.health += defaultDamageValue;
                        updateCallback.processReport("Fight: " + this + " vs " + i2 + "\n");

                    } else {
                        updateCallback.processReport(this + " says:" + i2 + " is already dead!\n");
                    }
                }
            }
        } else if (this.health < i2.getHealth()) {
            synchronized (i2.health) {
                synchronized (health) {
                    if (i2.getHealth() > 0) {
                        espere = true;
                        i2.changeHealth(i2.getHealth() - defaultDamageValue);
                        this.health += defaultDamageValue;
                        updateCallback.processReport("Fight: " + this + " vs " + i2 + "\n");

                    } else {
                        updateCallback.processReport(this + " says:" + i2 + " is already dead!\n");
                    }
                }
            }
        } else {
            synchronized (Deadlock) {
                synchronized (health) {
                    synchronized (i2.health) {
                        if (i2.getHealth() > 0) {

                            i2.changeHealth(i2.getHealth() - defaultDamageValue);
                            this.health += defaultDamageValue;
                            updateCallback.processReport("Fight: " + this + " vs " + i2 + "\n");

                        } else {
                            updateCallback.processReport(this + " says:" + i2 + " is already dead!\n");
                        }
                    }
                }
            }
        }

    }

    public void changeHealth(int v) {
        health = v;
    }

    public int getHealth() {
        return health;
    }

    @Override
    public String toString() {

        return name + "[" + health + "]";
    }

}
