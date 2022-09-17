package fr.feavy.discordplayspokemon.vba.key;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum Key {
    UP('z', 'w'),
    LEFT('q'),
    RIGHT('d'),
    DOWN('s'),
    A('a'),
    B('b'),
    SELECT('e'),
    START('t');

    public final List<Character> labels;

    Key(Character ...labels) {
        this.labels = Arrays.asList(labels);
    }

    private static final Map<Character, Key> labelToKeyMap = new HashMap<>();

    static {
        for(Key key : Key.values()) {
            for(Character label : key.labels) {
                labelToKeyMap.put(label, key);
            }
        }
    }

    public static Key ofLabel(char label) {
        return labelToKeyMap.get(label);
    }
}
