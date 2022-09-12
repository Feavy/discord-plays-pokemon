package fr.feavy.discordplayspokemon.service.vba;

import fr.feavy.discordplayspokemon.service.vba.loops.ImageGenerationLoop;
import fr.feavy.discordplayspokemon.service.vba.loops.KeyboardInteractionLoop;
import fr.feavy.discordplayspokemon.service.vba.loops.StateSavingLoop;
import fr.feavy.discordplayspokemon.vba.emulator.Emulator;
import fr.feavy.discordplayspokemon.vba.key.Key;
import io.quarkus.runtime.StartupEvent;
import org.eclipse.microprofile.context.ManagedExecutor;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

@ApplicationScoped
public class EmulatorService {
    private final ManagedExecutor executor;
    private final Emulator emulator;
    private final ImageGenerationLoop imageGenerationLoop;
    private final KeyboardInteractionLoop keyboardInteractionLoop;

    private final StateSavingLoop stateSavingLoop;

    public EmulatorService(ManagedExecutor executor, Emulator emulator,
                           ImageGenerationLoop imageGenerationLoop,
                           KeyboardInteractionLoop keyboardInteractionLoop,
                           StateSavingLoop stateSavingLoop) {
        this.executor = executor;
        this.emulator = emulator;
        this.imageGenerationLoop = imageGenerationLoop;
        this.keyboardInteractionLoop = keyboardInteractionLoop;
        this.stateSavingLoop = stateSavingLoop;
    }

    public void startup(@Observes StartupEvent e) throws InterruptedException {
        emulator.start();
        Thread.sleep(3000);
        emulator.loadState();
        startLoops();
    }

    public void startLoops() {
        executor.runAsync(this.keyboardInteractionLoop);
        executor.runAsync(this.imageGenerationLoop);
        executor.runAsync(this.stateSavingLoop);
    }

    public void queueKey(Key key) {
        keyboardInteractionLoop.queueKey(key);
    }

    public byte[] getImage() {
        return imageGenerationLoop.getImage();
    }
}
