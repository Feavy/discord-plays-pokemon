package fr.feavy.discordplayspokemon.service.vba.loops;

import fr.feavy.discordplayspokemon.vba.emulator.Emulator;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class StateSavingLoop implements Runnable {
    private final long interval;
    private final Emulator emulator;

    public StateSavingLoop(@ConfigProperty(name = "saving.interval") long interval,
                           Emulator emulator) {
        this.interval = interval;
        this.emulator = emulator;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            emulator.saveState();
        }
    }
}
