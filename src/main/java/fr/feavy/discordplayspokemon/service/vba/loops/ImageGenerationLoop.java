package fr.feavy.discordplayspokemon.service.vba.loops;

import fr.feavy.discordplayspokemon.storage.Storage;
import fr.feavy.discordplayspokemon.vba.emulator.Emulator;
import io.micrometer.core.instrument.MeterRegistry;
import org.eclipse.microprofile.config.inject.ConfigProperty;
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
    private final long sleepDelay;
    private final ManagedExecutor executor;
    private final Emulator emulator;
    private final Storage storage;

    private final BufferedImage aside;
    private final BufferedImage footer;
    private final Font font;

    private final AtomicReference<byte[]> image = new AtomicReference<>();
    private final AtomicBoolean isDirty = new AtomicBoolean(true);
    private final AtomicInteger playerCountEstimation;

    private final Dimension screenSize;
    private final Dimension embedSize;

    public ImageGenerationLoop(@ConfigProperty(name = "image-generation.interval") long interval,
                               MeterRegistry registry,
                               ManagedExecutor executor,
                               Emulator emulator,
                               Storage storage) throws IOException, FontFormatException {
        this.sleepDelay = interval;
        this.executor = executor;
        this.emulator = emulator;
        this.storage = storage;
        this.aside = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/layout/side.png")));
        this.footer = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/layout/footer.png")));
        this.font = Font.createFont(Font.TRUETYPE_FONT, Objects.requireNonNull(getClass().getResourceAsStream("/font/LuckiestGuy.ttf"))).deriveFont(13f);

        this.screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.embedSize = new Dimension(aside.getWidth() + screenSize.width, screenSize.height + footer.getHeight());

        this.playerCountEstimation = registry.gauge("player_count", new AtomicInteger(0));
    }

    @Override
    public void run() {
        while (true) {
            if (isDirty.compareAndSet(true, false)) {
                try {
                    Thread.sleep(sleepDelay);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                BufferedImage screen = emulator.screenshot();
                updateEmbed(screen);
                executor.runAsync(() -> saveImage(screen));
            } else {
                try {
                    Thread.sleep(sleepDelay);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void updateEmbed(BufferedImage screen) {
        BufferedImage image = new BufferedImage(embedSize.width, embedSize.height, BufferedImage.TYPE_INT_RGB);

        Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.setFont(font);

        RenderingHints rh = new RenderingHints(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.setRenderingHints(rh);

        graphics.drawImage(screen, 0, 0, null);
        graphics.drawImage(aside, screenSize.width, 0, null);
        graphics.drawImage(footer, 0, screenSize.height, null);

        int count = playerCountEstimation.get();
        if (count == 0) count = 1;

        String playerCountLbl = String.valueOf(count);
        int textWidth = graphics.getFontMetrics().stringWidth(playerCountLbl);

        int x = 65 - textWidth / 2;

        graphics.drawString(playerCountLbl, screenSize.width + x, 21);

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
            storage.save(System.currentTimeMillis() + ".png", imageData);
        } catch (IOException ignored) {
        }
    }

    public void setDirty() {
        isDirty.set(true);
    }

    public byte[] getImage() {
        return image.get();
    }

    public void setPlayerCountEstimation(int playerCountEstimation) {
        this.playerCountEstimation.set(playerCountEstimation);
    }
}
