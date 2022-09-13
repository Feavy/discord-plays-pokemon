import com.madgag.gif.fmsware.AnimatedGifEncoder;
import org.junit.Test;

import java.awt.*;

public class GifRecorderTest {
    public static void main(String[] args) throws AWTException {
        long lastFrameTime = 0;
        long intervalBetweenFrames = 16 * 1000000;
        int frameCount = 0;

        Robot robot = new Robot();
        Rectangle size = new Rectangle(200, 200);

        AnimatedGifEncoder animatedGifEncoder = new AnimatedGifEncoder();
        animatedGifEncoder.start("test.gif");
        animatedGifEncoder.setDelay(16);


        while(frameCount < 300) {
            long currentTime = System.nanoTime();
            if(currentTime - lastFrameTime >= intervalBetweenFrames) {
                lastFrameTime = currentTime;
                animatedGifEncoder.addFrame(robot.createScreenCapture(size));
                frameCount++;
                System.out.println("took");
            }
        }

        animatedGifEncoder.finish();
    }
}
