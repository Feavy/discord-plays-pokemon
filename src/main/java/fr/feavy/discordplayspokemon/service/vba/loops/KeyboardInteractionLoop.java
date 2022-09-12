package fr.feavy.discordplayspokemon.service.vba.loops;

import fr.feavy.discordplayspokemon.vba.emulator.Emulator;
import fr.feavy.discordplayspokemon.vba.key.Key;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Send key input every 3ms
 */
public class KeyboardInteractionLoop implements Runnable {
    private final ConcurrentLinkedQueue<Key> keyQueue = new ConcurrentLinkedQueue<>();
    private final Emulator emulator;
    private final ImageGenerationLoop screenshotLoop;
    private final AtomicInteger keyQueueCount = new AtomicInteger();

    private long counterStart = System.currentTimeMillis();

    private static final long START_KEY_COOLDOWN = 30000;
    private volatile long lastStartKeyPressTime = 0;

    public KeyboardInteractionLoop(Emulator emulator, ImageGenerationLoop screenshotLoop) {
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
            if(key == Key.START) {
                if(System.currentTimeMillis() - lastStartKeyPressTime < START_KEY_COOLDOWN) continue;
                lastStartKeyPressTime = System.currentTimeMillis();
            }
            if (key != null) {
                emulator.pressKey(key);
                screenshotLoop.setDirty();
            }
            if(System.currentTimeMillis() - counterStart >= 1000){
                counterStart = System.currentTimeMillis();
                screenshotLoop.setPlayerCountEstimation(keyQueueCount.getAndSet(0));
            }
        }
    }
}
