package fr.feavy.discordplayspokemon.service.vba.loops;

import fr.feavy.discordplayspokemon.vba.emulator.Emulator;
import fr.feavy.discordplayspokemon.vba.key.Key;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Send key input every 3ms
 */
@ApplicationScoped
public class KeyboardInteractionLoop implements Runnable {
    private final long startKeyCooldown;
    private final Emulator emulator;
    private final ImageGenerationLoop screenshotLoop;
    private final ConcurrentLinkedQueue<Key> keyQueue = new ConcurrentLinkedQueue<>();
    private final AtomicInteger keyQueueCount = new AtomicInteger();
    private long counterStart = System.currentTimeMillis();
    private volatile long lastStartKeyPressTime = 0;

    public KeyboardInteractionLoop(@ConfigProperty(name = "key.start.cooldown") long startKeyCooldown,
                                   Emulator emulator,
                                   ImageGenerationLoop screenshotLoop) {
        this.startKeyCooldown = startKeyCooldown;
        this.emulator = emulator;
        this.screenshotLoop = screenshotLoop;
    }

    public void queueKey(Key key) {
        keyQueue.add(key);
        keyQueueCount.incrementAndGet();
    }

    @Override
    public void run() {
        while (true) {
            Key key = keyQueue.poll();
            if (key == Key.START) {
                if (System.currentTimeMillis() - lastStartKeyPressTime < startKeyCooldown) continue;
                lastStartKeyPressTime = System.currentTimeMillis();
            }
            if (key != null) {
                emulator.pressKey(key);
                screenshotLoop.setDirty();
            }
            if (System.currentTimeMillis() - counterStart >= 1000) {
                counterStart = System.currentTimeMillis();
                screenshotLoop.setPlayerCountEstimation(keyQueueCount.getAndSet(0));
            }
        }
    }
}
