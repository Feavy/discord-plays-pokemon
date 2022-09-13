package fr.feavy.discordplayspokemon.vba.emulator;

import fr.feavy.discordplayspokemon.image.Image;
import fr.feavy.discordplayspokemon.vba.key.Key;

public interface Emulator {
    void start();
    void pressKey(Key key);
    void sleep(long ms);
    Image screenshot();
    void saveState();
    void loadState();
}
