package com.company;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

public class Main {

    private static final int NORMAL_CYCLISTS = 10;

    public static void main(String[] args) throws InterruptedException {
        int i;
        CyclistPhaser phaser = new CyclistPhaser();

        for (i = 1; i <= NORMAL_CYCLISTS; i++) {
            new Thread(new Cyclist("Ciclista " + i, phaser), "Ciclista " + i).start();
        }

        new Thread(new ImpacientCyclist("Ciclista " + i, phaser), "Ciclista " + i).start();
        i++;

        TimeUnit.SECONDS.sleep(9);
        new Thread(new TardyCyclist("Ciclista " + i, phaser), "Ciclista " + i).start();
        i++;

        TimeUnit.SECONDS.sleep(20);
        new Thread(new TardyCyclist("Ciclista " + i, phaser), "Ciclista " + i).start();
    }
}
