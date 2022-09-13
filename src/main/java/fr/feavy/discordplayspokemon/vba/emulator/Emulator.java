package fr.feavy.discordplayspokemon.vba.emulator;

import fr.feavy.discordplayspokemon.vba.key.Key;

public interface Emulator {
    void start();
    void pressKey(Key key);
    byte[] record(long duration, int fps);
    void saveState();
    void loadState();
}
