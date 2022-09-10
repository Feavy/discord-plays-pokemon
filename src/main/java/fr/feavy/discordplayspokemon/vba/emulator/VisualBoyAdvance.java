package fr.feavy.discordplayspokemon.vba.emulator;

import fr.feavy.discordplayspokemon.vba.key.Key;
import fr.feavy.discordplayspokemon.vba.key.KeyMap;

import javax.inject.Singleton;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Singleton
public class VisualBoyAdvance implements Emulator {

    public final KeyMap KEY_MAP = new KeyMap()
            .withBinding(Key.UP, KeyEvent.VK_UP)
            .withBinding(Key.LEFT, KeyEvent.VK_LEFT)
            .withBinding(Key.RIGHT, KeyEvent.VK_RIGHT)
            .withBinding(Key.DOWN, KeyEvent.VK_DOWN)
            .withBinding(Key.A, KeyEvent.VK_Z)
            .withBinding(Key.B, KeyEvent.VK_X)
            .withBinding(Key.SELECT, KeyEvent.VK_BACK_SPACE)
            .withBinding(Key.START, KeyEvent.VK_ENTER);

    //    L, // A
//    R // S ;

    private final Robot robot = new Robot();

    public VisualBoyAdvance() throws AWTException {
    }

    @Override
    public void start() {
        System.out.println("[VisualBoyAdvance] Starting...");
        ProcessBuilder pb = new ProcessBuilder("/usr/bin/vba", "-c", "vba.ini", "pokemon.gb");
        pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        pb.redirectError(ProcessBuilder.Redirect.INHERIT);
        try {
            pb.start();
            System.out.println("[VBAService] Emulator started successfully!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public BufferedImage screenshot() {
        return robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
    }

    @Override
    public void pressKey(Key key) {
        int keyCode = KEY_MAP.getKeyCode(key);
        robot.keyPress(keyCode);
        robot.delay(3);
        robot.keyRelease(keyCode);
    }

    @Override
    public void saveState() {
        robot.keyPress(KeyEvent.VK_SHIFT);
        robot.delay(100);
        robot.keyPress(KeyEvent.VK_F1);
        robot.delay(200);
        robot.keyRelease(KeyEvent.VK_F1);
        robot.delay(100);
        robot.keyRelease(KeyEvent.VK_SHIFT);
        System.out.println("[VisualBoyAdvance] Saved state.");
    }

    @Override
    public void loadState() {
        robot.keyPress(KeyEvent.VK_F1);
        robot.delay(100);
        robot.keyRelease(KeyEvent.VK_F1);
        System.out.println("[VisualBoyAdvance] Loaded state.");
    }
}
