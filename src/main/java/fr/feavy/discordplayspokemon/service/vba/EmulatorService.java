package fr.feavy.discordplayspokemon.service.vba;

import fr.feavy.discordplayspokemon.service.vba.loops.ImageGeneratorLoop;
import fr.feavy.discordplayspokemon.service.vba.loops.KeyboardLoop;
import fr.feavy.discordplayspokemon.service.vba.loops.SaveGameLoop;
import fr.feavy.discordplayspokemon.vba.emulator.Emulator;
import fr.feavy.discordplayspokemon.vba.key.Key;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import org.eclipse.microprofile.context.ManagedExecutor;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import java.awt.*;
import java.io.IOException;

@ApplicationScoped
public class EmulatorService {
    private final ManagedExecutor executor;
    private final ImageGeneratorLoop imageGeneratorLoop;
    private final KeyboardLoop keyboardLoop;

    private final Emulator emulator;
    private final SaveGameLoop saveGameLoop;

    public EmulatorService(ManagedExecutor executor, Emulator emulator) throws IOException, FontFormatException {
        this.executor = executor;
        this.emulator = emulator;

        this.imageGeneratorLoop = new ImageGeneratorLoop(emulator);
        this.keyboardLoop = new KeyboardLoop(emulator, imageGeneratorLoop);
        this.saveGameLoop = new SaveGameLoop(emulator);
    }

    public void startup(@Observes StartupEvent e) throws InterruptedException {
        System.out.println("[EmulatorService] Starting.");
        emulator.start();
        Thread.sleep(3000);
        emulator.loadState();
        startLoops();
    }

    public void stop(@Observes ShutdownEvent ev) {
        System.out.println("[EmulatorService] Stopping.");
        emulator.saveState();
    }

    public void startLoops() {
        executor.runAsync(this.keyboardLoop);
        executor.runAsync(this.imageGeneratorLoop);
        executor.runAsync(this.saveGameLoop);
    }

    public void queueKey(Key key) {
        keyboardLoop.queueKey(key);
    }

    public byte[] getImage() {
        return imageGeneratorLoop.getImage();
    }
}
