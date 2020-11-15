package com.company;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Phaser;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class TardyCyclist implements Runnable {

    private final String name;
    private final Phaser phaser;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    public TardyCyclist(String name, Phaser phaser) {
        this.name = name;
        this.phaser = phaser;
    }

    @Override
    public void run() {
        if (!phaser.isTerminated()) {
            int joinPhase = phaser.register();
            System.out.printf("%s se ha unido a sus amigos en la fase %d %s\n",
                    name, joinPhase, LocalTime.now().format(dateTimeFormatter));
            try {
                goToFuelStation();
            } catch (InterruptedException e) {
                System.out.printf("%s se ha interrumpido mientras iba a la gasolinera\n", name);
                return;
            }
            // Tardy friends shouln't do arrive on previous phases in order not to
            // interfere with the synchonization process or the current phase of the
            // phaser.
            if (joinPhase <= CyclistPhaser.ARRIVE_TO_FUEL_STATION_PHASE) {
                try {
                    phaser.awaitAdvanceInterruptibly(phaser.arrive());
                } catch (InterruptedException e) {
                    System.out.printf("%s se ha interrumpido mientras esperaba en la gasolinera\n", name);
                    return;
                }
            }
            try {
                startRunning();
            } catch (InterruptedException e) {
                System.out.printf("%s se ha interrumpido mientras hacía la etapa\n", name);
                return;
            }
            if (joinPhase <= CyclistPhaser.ARRIVE_TO_SALE_PHASE) {
                try {
                    phaser.awaitAdvanceInterruptibly(phaser.arrive());
                } catch (InterruptedException e) {
                    System.out.printf("%s se ha interrumpido mientras esperaba en la venta\n", name);
                    return;
                }
            }
            try {
                returnToFuelStation();
            } catch (InterruptedException e) {
                System.out.printf("%s se ha interrumpido mientras volvía a la gasolinera\n", name);
                return;
            }
            if (joinPhase <= CyclistPhaser.RETURN_TO_FUEL_STATION_PHASE) {
                try {
                    phaser.awaitAdvanceInterruptibly(phaser.arrive());
                } catch (InterruptedException e) {
                    System.out.printf("%s se ha interrumpido mientras esperaba en la gasolinera para volver\n", name);
                    return;
                }
            }
            try {
                goHome();
            } catch (InterruptedException e) {
                System.out.printf("%s se ha interrumpido mientras volvía a la casa\n", name);
            }
        } else {
            System.out.printf("%s ha llamado a sus amigos demasiado tarde %s\n",
                    name, LocalTime.now().format(dateTimeFormatter));
        }
    }

    private void goToFuelStation() throws InterruptedException{
        System.out.printf("%s ha salido de su casa %s\n", name, LocalTime.now().format(dateTimeFormatter));
        TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(3) + 1);
        System.out.printf("%s ha llegado a la gasolinera %s\n", name, LocalTime.now().format(dateTimeFormatter));
    }

    private void startRunning() throws InterruptedException{
        TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(5) + 5);
        System.out.printf("%s ha llegado a la venta %s\n", name, LocalTime.now().format(dateTimeFormatter));
    }

    private void returnToFuelStation() throws InterruptedException{
        TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(5) + 5);
        System.out.printf("%s ha llegado a la gasolinera para volver %s\n", name, LocalTime.now().format(dateTimeFormatter));
    }

    private void goHome() throws InterruptedException{
        TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(3) + 1);
        System.out.printf("%s está ya en casa %s\n", name, LocalTime.now().format(dateTimeFormatter));
    }

    @SuppressWarnings("unused")
    private void awaitPhase(Phaser phaser, int currentPhase, int expectedPhase) {
        while (currentPhase < expectedPhase && !phaser.isTerminated()) {
            System.out.printf("%s -> %s is waiting phase #%d to finish\n",
                    LocalTime.now().format(dateTimeFormatter), name, currentPhase);
            currentPhase = phaser.arriveAndAwaitAdvance();
        }
    }

}