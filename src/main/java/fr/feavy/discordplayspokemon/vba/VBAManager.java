package fr.feavy.discordplayspokemon.vba;

import io.quarkus.runtime.StartupEvent;
import org.eclipse.microprofile.context.ManagedExecutor;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;

@ApplicationScoped
public class VBAManager {
    private final ManagedExecutor executor;

    private final ConcurrentLinkedQueue<Integer> keyQueue = new ConcurrentLinkedQueue<>();

    private final BufferedImage layout;
    private final BufferedImage header;
    private final Robot robot = new Robot();
    private byte[] image;

    public VBAManager(ManagedExecutor executor) throws AWTException, IOException {
        this.executor = executor;
        this.layout = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/layout.png")));
        this.header = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/header.png")));
    }

    public void startup(@Observes StartupEvent e) throws IOException {
        startEmulator();
        startLoop();
    }

    public void startEmulator() throws IOException {
        System.out.println("[VBAService] Starting emulator... /usr/bin/vba pokemon_red.gb");
        ProcessBuilder pb = new ProcessBuilder("/usr/bin/vba", "-c", "vba.ini", "pokemon.gb");
        pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        pb.redirectError(ProcessBuilder.Redirect.INHERIT);
        Process process = pb.start();
        System.out.println("[VBAService] Emulator started! "+process);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        generateImage();
    }

    public void startLoop() {
        executor.runAsync(() -> {
            System.out.println("[VBAService] Key queue loop started!");
            while (true) {
                Integer key = keyQueue.poll();
                boolean keyPressed = false;
                if (key != null) {
                    robot.keyPress(key);
                    keyPressed = true;
                    try {
                        Thread.sleep(3);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    robot.keyRelease(key);
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if(keyPressed) {
                    try {
                        generateImage();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

    public void queueKey(int key) {
        keyQueue.add(key);
    }

    public BufferedImage takeScreenshot() {
        return robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
    }

    private void generateImage() throws IOException {
        System.out.println("generateImage");
        BufferedImage screen = takeScreenshot();
        BufferedImage image = new BufferedImage(470, 289, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = image.getGraphics();
        graphics.drawImage(screen, 0, 0, null);
        graphics.drawImage(layout, 320, 0, null);
        graphics.drawImage(header, 0, 0, null);
        var os = new ByteArrayOutputStream();
        ImageIO.write(image, "png", os);
        this.image = os.toByteArray();
    }

    public byte[] getImage() {
        return image;
    }
}
