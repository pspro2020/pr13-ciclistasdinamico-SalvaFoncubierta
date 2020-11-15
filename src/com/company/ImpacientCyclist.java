package com.company;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Phaser;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class ImpacientCyclist implements Runnable {

    private final String name;
    private final Phaser phaser;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    public ImpacientCyclist(String name, Phaser phaser) {
        this.name = name;
        this.phaser = phaser;
    }

    @Override
    public void run() {
        phaser.register();
        try {
            goToFuelStation();
        } catch (InterruptedException e) {
            System.out.printf("%s se ha interrumpido mientras iba a la gasolinera\n", name);
            return;
        }

        try {
            phaser.awaitAdvanceInterruptibly(phaser.arrive());
        } catch (InterruptedException e) {
            System.out.printf("%s se ha interrumpido mientras esperaba en la gasolinera\n", name);
            return;
        }

        try {
            startRunning();
        } catch (InterruptedException e) {
            System.out.printf("%s se ha interrumpido mientras hacía la etapa\n", name);
            return;
        }

        phaser.arriveAndDeregister();

        try {
            returnToFuelStation();
        } catch (InterruptedException e) {
            System.out.printf("%s se ha interrumpido mientras volvía a la gasolinera\n", name);
            return;
        }

        try {
            goHome();
        } catch (InterruptedException e) {
            System.out.printf("%s se ha interrumpido mientras volvía a la casa\n", name);
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
}