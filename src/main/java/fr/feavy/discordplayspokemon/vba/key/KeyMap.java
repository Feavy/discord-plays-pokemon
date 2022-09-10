package fr.feavy.discordplayspokemon.vba.key;

import java.util.HashMap;
import java.util.Map;

public class KeyMap {
    private final Map<Key, Integer> keyMap = new HashMap<>();
    public KeyMap withBinding(Key key, Integer keyCode) {
        keyMap.put(key, keyCode);
        return this;
    }

    public int getKeyCode(Key key) {
        Integer keyCode = keyMap.get(key);
        if (keyCode == null) {
            throw new IllegalArgumentException("Unknown key: "+ key);
        }
        return keyCode;
    }
}
