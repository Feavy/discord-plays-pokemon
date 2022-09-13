package fr.feavy.discordplayspokemon.service.discord;

import fr.feavy.discordplayspokemon.image.Image;

import javax.enterprise.context.ApplicationScoped;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Generates and holds image to display on discord channels
 */
@ApplicationScoped
public class ImageMessageService {
    private final BufferedImage aside;
    private final BufferedImage footer;
    private final Font font;

    private final AtomicReference<byte[]> image = new AtomicReference<>();

    private final Dimension screenSize;
    private final Dimension embedSize;

    public ImageMessageService() throws IOException, FontFormatException {
        this.aside = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/layout/side.png")));
        this.footer = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/layout/footer.png")));
        this.font = Font.createFont(Font.TRUETYPE_FONT, Objects.requireNonNull(getClass().getResourceAsStream("/font/LuckiestGuy.ttf"))).deriveFont(13f);

        this.screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.embedSize = new Dimension(aside.getWidth() + screenSize.width, screenSize.height + footer.getHeight());
    }

    public void updateImage(Image screen, int playerCountEstimation) {
        Image image = new Image(embedSize.width, embedSize.height);

        Graphics2D graphics = image.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.setFont(font);

        RenderingHints rh = new RenderingHints(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.setRenderingHints(rh);

        graphics.drawImage(screen.internal, 0, 0, null);
        graphics.drawImage(aside, screenSize.width, 0, null);
        graphics.drawImage(footer, 0, screenSize.height, null);

        if (playerCountEstimation == 0) playerCountEstimation = 1;

        String playerCountLbl = String.valueOf(playerCountEstimation);
        int textWidth = graphics.getFontMetrics().stringWidth(playerCountLbl);

        int x = 65 - textWidth / 2;

        graphics.drawString(playerCountLbl, screenSize.width + x, 21);

        this.image.set(image.toByteArray());
    }

    public byte[] getImage() {
        return image.get();
    }

}
