package fr.feavy.discordplayspokemon.service.vba.loops;

import fr.feavy.discordplayspokemon.vba.emulator.Emulator;

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
public class ImageGeneratorLoop implements Runnable {
    private final AtomicBoolean dirty = new AtomicBoolean(true);

    private final BufferedImage side;
    private final BufferedImage footer;
    private final AtomicReference<byte[]> image = new AtomicReference<>();
    private final Emulator emulator;
    private final Font font;

    private final AtomicInteger playerCountEstimation = new AtomicInteger(0);

    public ImageGeneratorLoop(Emulator emulator) throws IOException, FontFormatException {
        this.emulator = emulator;
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
                generateImage();
            }else{
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void generateImage() {
        long start = System.currentTimeMillis();

        BufferedImage screen = emulator.screenshot();

        BufferedImage image = new BufferedImage(470, 289+49, BufferedImage.TYPE_INT_RGB);

        Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.setFont(font);

        RenderingHints rh = new RenderingHints(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.setRenderingHints(rh);

        graphics.drawImage(screen, 0, 0, null);
        graphics.drawImage(side, 320, 0, null);
        graphics.drawImage(footer, 0, 289, null);

        int count = playerCountEstimation.get();
        if(count == 0) count = 1;

        String playerCountLbl = String.valueOf(count);
        int width = graphics.getFontMetrics().stringWidth(playerCountLbl);

        int x = 39+52/2-width/2;

        graphics.drawString(playerCountLbl, 320+x, 21);

        var os = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", os);
            this.image.set(os.toByteArray());
            System.out.println("generated image in "+(System.currentTimeMillis()-start)+"ms");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
