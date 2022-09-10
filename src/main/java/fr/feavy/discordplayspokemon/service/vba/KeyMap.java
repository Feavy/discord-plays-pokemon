package fr.feavy.discordplayspokemon.service.vba;

import java.util.HashMap;
import java.util.Map;

public class KeyMap {
    private final Map<Character, Integer> keyMap = new HashMap<>();
    public KeyMap() {
        for (Key key : Key.values()) {
            for (Character label : key.labels) {
                keyMap.put(label, key.keyCode);
            }
        }
    }

    public int getKeyCode(char label) {
        Integer keyCode = keyMap.get(label);
        if (keyCode == null) {
            throw new IllegalArgumentException("Unknown key: "+label);
        }
        return keyCode;
    }
}
