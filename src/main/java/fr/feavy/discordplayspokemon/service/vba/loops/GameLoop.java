package fr.feavy.discordplayspokemon.service.vba.loops;

import fr.feavy.discordplayspokemon.image.Image;
import fr.feavy.discordplayspokemon.service.discord.ImageMessageService;
import fr.feavy.discordplayspokemon.storage.Storage;
import fr.feavy.discordplayspokemon.vba.emulator.Emulator;
import fr.feavy.discordplayspokemon.vba.key.Key;
import io.micrometer.core.instrument.MeterRegistry;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.context.ManagedExecutor;

import javax.enterprise.context.ApplicationScoped;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Send key input every 600ms
 */
@ApplicationScoped
public class GameLoop implements Runnable {
    private final long startKeyCooldown;
    private final ImageMessageService imageService;
    private final ManagedExecutor executor;
    private final Storage storage;
    private final Emulator emulator;
    private volatile Key nextKey = null;
    private final AtomicInteger nextKeysLastSecond = new AtomicInteger();
    private final AtomicInteger playerCountEstimation;
    private long counterStart = System.currentTimeMillis();
    private volatile long lastStartKeyPressTime = 0;

    public GameLoop(@ConfigProperty(name = "key.start.cooldown") long startKeyCooldown,
                    ImageMessageService imageService,
                    MeterRegistry registry,
                    ManagedExecutor executor,
                    Storage storage,
                    Emulator emulator) {
        this.playerCountEstimation = registry.gauge("player_count", new AtomicInteger(0));
        this.startKeyCooldown = startKeyCooldown;
        this.imageService = imageService;
        this.executor = executor;
        this.storage = storage;
        this.emulator = emulator;
    }

    public void setNextKey(Key key) {
        this.nextKey = key;
        nextKeysLastSecond.incrementAndGet();
    }

    @Override
    public void run() {
        while (true) {
            final Key key = nextKey;

            if (key != null) {
                nextKey = null;
                if (key == Key.START) {
                    if (System.currentTimeMillis() - lastStartKeyPressTime < startKeyCooldown) continue;
                    lastStartKeyPressTime = System.currentTimeMillis();
                }
                emulator.pressKey(key);
                emulator.sleep(100);

                Image image = emulator.screenshot();
                imageService.updateImage(image, nextKeysLastSecond.get());
                executor.runAsync(() -> storage.save(System.currentTimeMillis() + ".png", image.toByteArray()));
                emulator.sleep(500);
            }
            if (System.currentTimeMillis() - counterStart >= 1000) {
                counterStart = System.currentTimeMillis();
                playerCountEstimation.set(nextKeysLastSecond.get());
                nextKeysLastSecond.set(0);
            }
        }
    }
}
