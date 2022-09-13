package fr.feavy.discordplayspokemon.service.vba;

import fr.feavy.discordplayspokemon.service.discord.ImageMessageService;
import fr.feavy.discordplayspokemon.service.vba.loops.GameLoop;
import fr.feavy.discordplayspokemon.service.vba.loops.StateSavingLoop;
import fr.feavy.discordplayspokemon.vba.emulator.Emulator;
import fr.feavy.discordplayspokemon.vba.key.Key;
import io.micrometer.core.instrument.MeterRegistry;
import io.quarkus.runtime.StartupEvent;
import org.eclipse.microprofile.context.ManagedExecutor;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

@ApplicationScoped
public class EmulatorService {
    private final ManagedExecutor executor;
    private final Emulator emulator;
    private final ImageMessageService imageService;
    private final GameLoop gameLoop;

    private final StateSavingLoop stateSavingLoop;

    private final MeterRegistry registry;

    public EmulatorService(ManagedExecutor executor, Emulator emulator,
                           MeterRegistry registry,
                           ImageMessageService imageService,
                           GameLoop gameLoop,
                           StateSavingLoop stateSavingLoop) {
        this.executor = executor;
        this.emulator = emulator;
        this.imageService = imageService;
        this.gameLoop = gameLoop;
        this.stateSavingLoop = stateSavingLoop;
        this.registry = registry;
    }

    public void startup(@Observes StartupEvent e) {
        emulator.start();
        emulator.sleep(3000);
        emulator.loadState();
        startLoops();
        imageService.updateImage(emulator.screenshot(), 0);
    }

    public void startLoops() {
        executor.runAsync(this.gameLoop);
        executor.runAsync(this.stateSavingLoop);
    }

    public void setNextKey(Key key) {
        gameLoop.setNextKey(key);
        registry.counter("key.pressed", "key", key.name()).increment();
    }

    public byte[] getImage() {
        return imageService.getImage();
    }
}
