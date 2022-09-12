package fr.feavy.discordplayspokemon.storage;

import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.StorageOptions;

import javax.inject.Singleton;
import java.nio.file.Path;
import java.time.LocalDateTime;

@Singleton
public class GoogleStorage implements Storage {
    private Bucket bucket = null;

    public GoogleStorage() {
        try {
            com.google.cloud.storage.Storage storage = StorageOptions.getDefaultInstance().getService();
            this.bucket = storage.get("discord-plays-pokemon");
        } catch (Exception e) {
            System.err.println("Error while connecting to Google Cloud Storage: "+e.getMessage());
        }
    }

    public void save(String filename, byte[] data) {
        if (this.bucket == null) {
            System.out.println("Could not save " + filename + " to Google Storage. Bucket is null.");
            return;
        }
        LocalDateTime now = LocalDateTime.now();

        Path targetBlob = Path.of("screenshots", String.valueOf(now.getYear()))
                .resolve(String.valueOf(now.getMonthValue()))
                .resolve(String.valueOf(now.getDayOfMonth()))
                .resolve(filename);

        bucket.create(targetBlob.toString(), data);
    }
}
