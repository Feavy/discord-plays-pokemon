package fr.feavy.discordplayspokemon.service.vba.loops;

import fr.feavy.discordplayspokemon.vba.emulator.Emulator;

public class SaveGameLoop implements Runnable {
    private static final long TEN_MINUTES = 1000 * 60 * 10; // 10 minutes
    private final Emulator emulator;

    public SaveGameLoop(Emulator emulator) {
        this.emulator = emulator;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(TEN_MINUTES);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            emulator.saveState();
        }
    }
}
