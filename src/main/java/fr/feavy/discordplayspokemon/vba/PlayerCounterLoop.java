package fr.feavy.discordplayspokemon.vba;

import java.util.concurrent.atomic.AtomicInteger;

public class PlayerCounterLoop implements Runnable {
    private final AtomicInteger playerCountEstimation = new AtomicInteger(0);
    private final AtomicInteger keyPressCounter = new AtomicInteger(0);

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            int count = keyPressCounter.getAndSet(0);
            playerCountEstimation.set(count);
        }
    }

    public void incrementActionCounter() {
        keyPressCounter.incrementAndGet();
    }

    public int getPlayerCountEstimation() {
        return playerCountEstimation.get();
    }
}
