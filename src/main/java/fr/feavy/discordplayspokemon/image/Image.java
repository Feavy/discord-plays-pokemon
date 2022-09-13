package fr.feavy.discordplayspokemon.image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Image {
    public final BufferedImage internal;
    private byte[] bytes;

    public Image(BufferedImage internal) {
        this.internal = internal;
    }

    public Image(int width, int height) {
        this.internal = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }

    public byte[] toByteArray() {
        if(bytes == null) {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(internal, "png", baos);
                bytes = baos.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bytes;
    }

    public Graphics2D getGraphics() {
        return (Graphics2D) internal.getGraphics();
    }
}
