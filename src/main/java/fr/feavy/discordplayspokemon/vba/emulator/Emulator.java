package fr.feavy.discordplayspokemon.vba.emulator;

import fr.feavy.discordplayspokemon.vba.key.Key;

import java.awt.image.BufferedImage;

public interface Emulator {
    void start();
    void pressKey(Key key);
    BufferedImage screenshot();
    void saveState();
    void loadState();
}
