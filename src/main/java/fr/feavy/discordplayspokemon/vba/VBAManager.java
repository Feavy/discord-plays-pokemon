package fr.feavy.discordplayspokemon.vba;

import io.quarkus.runtime.StartupEvent;
import org.eclipse.microprofile.context.ManagedExecutor;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import java.awt.*;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

@ApplicationScoped
public class VBAManager {
    private final ManagedExecutor executor;
    private final PlayerCounterLoop playerCounterLoop = new PlayerCounterLoop();
    private final ImageGeneratorLoop imageGeneratorLoop = new ImageGeneratorLoop(playerCounterLoop);
    private final KeyboardLoop keyboardLoop = new KeyboardLoop(imageGeneratorLoop);


    public VBAManager(ManagedExecutor executor) throws AWTException, IOException, FontFormatException {
        this.executor = executor;
    }

    public void startup(@Observes StartupEvent e) throws IOException, InterruptedException {
        startEmulator();
        Thread.sleep(3000);
        startLoops();
    }

    public void startEmulator() throws IOException {
        System.out.println("[VBAService] Starting emulator... /usr/bin/vba pokemon_red.gb");
        ProcessBuilder pb = new ProcessBuilder("/usr/bin/vba", "-c", "vba.ini", "pokemon.gb");
        pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        pb.redirectError(ProcessBuilder.Redirect.INHERIT);
        Process process = pb.start();
        System.out.println("[VBAService] Emulator started! "+process);
    }

    public void startLoops() {
        executor.runAsync(this.keyboardLoop);
        executor.runAsync(this.imageGeneratorLoop);
        executor.runAsync(this.playerCounterLoop);
    }

    public void queueKey(int key) {
        keyboardLoop.queueKey(key);
        playerCounterLoop.incrementActionCounter();
    }

    public byte[] getImage() {
        return imageGeneratorLoop.getImage();
    }
}
