package fr.feavy.discordplayspokemon.storage;

public interface Storage {
    void save(String filename, byte[] data);
}