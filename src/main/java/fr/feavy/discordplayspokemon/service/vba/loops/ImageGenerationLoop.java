package fr.feavy.discordplayspokemon.service.vba.loops;

import fr.feavy.discordplayspokemon.storage.Storage;
import fr.feavy.discordplayspokemon.vba.emulator.Emulator;
import org.eclipse.microprofile.context.ManagedExecutor;

import javax.enterprise.context.ApplicationScoped;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Generates new image of the game every 100ms
 */
@ApplicationScoped
public class ImageGenerationLoop implements Runnable {
    private final ManagedExecutor executor;
    private final Emulator emulator;
    private final Storage storage;
    private final AtomicBoolean dirty = new AtomicBoolean(true);

    private final BufferedImage side;
    private final BufferedImage footer;
    private final AtomicReference<byte[]> image = new AtomicReference<>();
    private final Font font;
    private final AtomicInteger playerCountEstimation = new AtomicInteger(0);

    public ImageGenerationLoop(ManagedExecutor executor, Emulator emulator, Storage storage) throws IOException, FontFormatException {
        this.executor = executor;
        this.emulator = emulator;
        this.storage = storage;
        this.side = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/side.png")));
        this.footer = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/footer.png")));
        this.font = Font.createFont(Font.TRUETYPE_FONT, Objects.requireNonNull(getClass().getResourceAsStream("/LuckiestGuy-Regular.ttf"))).deriveFont(13f);
    }

    @Override
    public void run() {
        while (true) {
            if (dirty.compareAndSet(true, false)) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                BufferedImage screen = emulator.screenshot();
                updateEmbed(screen);
                executor.runAsync(() -> saveImage(screen));
            }else{
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void updateEmbed(BufferedImage screen) {
        BufferedImage image = new BufferedImage(470, 288+49, BufferedImage.TYPE_INT_RGB);

        Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.setFont(font);

        RenderingHints rh = new RenderingHints(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.setRenderingHints(rh);

        graphics.drawImage(screen, 0, 0, null);
        graphics.drawImage(side, 320, 0, null);
        graphics.drawImage(footer, 0, 288, null);

        int count = playerCountEstimation.get();
        if(count == 0) count = 1;

        String playerCountLbl = String.valueOf(count);
        int width = graphics.getFontMetrics().stringWidth(playerCountLbl);

        int x = 39+52/2-width/2;

        graphics.drawString(playerCountLbl, 320+x, 21);

        var os = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", os);
            byte[] imageData = os.toByteArray();
            this.image.set(imageData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveImage(BufferedImage image) {
        var os = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", os);
            byte[] imageData = os.toByteArray();
            storage.save(System.currentTimeMillis()+".png", imageData);
        } catch (IOException ignored) { }
    }

    public void setDirty() {
        dirty.set(true);
    }

    public byte[] getImage() {
        return image.get();
    }

    public void setPlayerCountEstimation(int playerCountEstimation) {
        this.playerCountEstimation.set(playerCountEstimation);
    }
}
