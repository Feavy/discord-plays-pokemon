package fr.feavy.discordplayspokemon.service.vba.loops;

import fr.feavy.discordplayspokemon.vba.emulator.Emulator;

public class GameSavingLoop implements Runnable {
    private static final long ONE_MINUTE = 1000 * 60; // delay
    private final Emulator emulator;

    public GameSavingLoop(Emulator emulator) {
        this.emulator = emulator;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(ONE_MINUTE);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            emulator.saveState();
        }
    }
}
