package fr.feavy.discordplayspokemon.service.vba;

import fr.feavy.discordplayspokemon.service.vba.loops.GameRecordingLoop;
import fr.feavy.discordplayspokemon.service.vba.loops.GameInteractionLoop;
import fr.feavy.discordplayspokemon.service.vba.loops.GameSavingLoop;
import fr.feavy.discordplayspokemon.vba.emulator.Emulator;
import fr.feavy.discordplayspokemon.vba.key.Key;
import io.quarkus.runtime.StartupEvent;
import org.eclipse.microprofile.context.ManagedExecutor;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import java.awt.*;
import java.io.IOException;

@ApplicationScoped
public class EmulatorService {
    private final ManagedExecutor executor;
    private final GameRecordingLoop gameRecordingLoop;
    private final GameInteractionLoop gameInteractionLoop;

    private final Emulator emulator;
    private final GameSavingLoop gameSavingLoop;

    public EmulatorService(ManagedExecutor executor, Emulator emulator) throws IOException, FontFormatException {
        this.executor = executor;
        this.emulator = emulator;

        this.gameRecordingLoop = new GameRecordingLoop(emulator);
        this.gameInteractionLoop = new GameInteractionLoop(emulator, gameRecordingLoop);
        this.gameSavingLoop = new GameSavingLoop(emulator);
    }

    public void startup(@Observes StartupEvent e) throws InterruptedException {
        emulator.start();
        Thread.sleep(3000);
        emulator.loadState();
        startLoops();
    }

    public void startLoops() {
        executor.runAsync(this.gameInteractionLoop);
        executor.runAsync(this.gameRecordingLoop);
        executor.runAsync(this.gameSavingLoop);
    }

    public void queueKey(Key key) {
        gameInteractionLoop.queueKey(key);
    }

    public byte[] getImage() {
        return gameRecordingLoop.getImage();
    }
}
