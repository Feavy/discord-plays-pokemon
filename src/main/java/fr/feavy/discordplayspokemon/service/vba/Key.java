package fr.feavy.discordplayspokemon.service.vba;

import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.List;

public enum Key {
    UP(KeyEvent.VK_UP, 'z', 'w'), // Up Arrow
    LEFT(KeyEvent.VK_LEFT, 'q'), // Left arrow
    RIGHT(KeyEvent.VK_RIGHT, 'd'), // Right arrow
    DOWN(KeyEvent.VK_DOWN, 's'), // Down arrow
    A(KeyEvent.VK_Z, 'a'), // Z
    B(KeyEvent.VK_X, 'b'), // X
    SELECT(KeyEvent.VK_BACK_SPACE, 'e'), // Backspace
    START(KeyEvent.VK_ENTER, 't'); // Enter
//    L, // A
//    R // S ;

    public final int keyCode;
    public final List<Character> labels;

    Key(int keyCode, Character ...labels) {
        this.keyCode = keyCode;
        this.labels = Arrays.asList(labels);
    }
}
