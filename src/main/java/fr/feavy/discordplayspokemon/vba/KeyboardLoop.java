package fr.feavy.discordplayspokemon.vba;

import java.awt.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Send key input every 3ms
 */
public class KeyboardLoop implements Runnable {
    private final ConcurrentLinkedQueue<Integer> keyQueue = new ConcurrentLinkedQueue<>();
    private final Robot robot = new Robot();

    private final ImageGeneratorLoop screenshotLoop;

    public KeyboardLoop(ImageGeneratorLoop screenshotLoop) throws AWTException {
        this.screenshotLoop = screenshotLoop;
    }

    public void queueKey(int key) {
        keyQueue.add(key);
    }

    @Override
    public void run() {
        while (true) {
            Integer key = keyQueue.poll();
            if (key != null) {
                robot.keyPress(key);
                screenshotLoop.setDirty();
                try {
                    Thread.sleep(3);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                robot.keyRelease(key);
            }
        }
    }
}
