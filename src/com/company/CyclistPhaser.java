package com.company;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Phaser;

public class CyclistPhaser extends Phaser {

    public static final int ARRIVE_TO_FUEL_STATION_PHASE = 0;
    public static final int ARRIVE_TO_SALE_PHASE = 1;
    public static final int RETURN_TO_FUEL_STATION_PHASE = 2;

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Override
    protected boolean onAdvance(int phase, int registeredParties) {
        switch (phase) {
            case ARRIVE_TO_FUEL_STATION_PHASE:
                System.out.printf("%d han llegado a la gasolinera, el último ha sido %s %s\n",
                        registeredParties, Thread.currentThread().getName(),
                        LocalTime.now().format(dateTimeFormatter));
                break;
            case ARRIVE_TO_SALE_PHASE:
                System.out.printf("%d han llegado a la venta, el último ha sido %s %s\n",
                        registeredParties, Thread.currentThread().getName(),
                        LocalTime.now().format(dateTimeFormatter));
                break;
            case RETURN_TO_FUEL_STATION_PHASE:
                System.out.printf("%d han llegado a la gasolinera de vuelta, el último ha sido %s %s\n",
                        registeredParties, Thread.currentThread().getName(),
                        LocalTime.now().format(dateTimeFormatter));
                return true;
        }
        return super.onAdvance(phase, registeredParties);
    }

}