package fr.xlim.ssd.opal.library;

/**
 * Define the interface for every key used in Secure Communication process.
 *
 * @author Damien Arcuset
 * @author Julien Iguchi-Cartigny
 */
public interface SCKey {

    byte[] getData();

    byte getSetVersion();

    byte getId();

    KeyType getType();
}
