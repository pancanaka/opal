package fr.xlim.ssd.opal.library.config;

/**
 * Define the interface for every key used in Secure Communication process.
 *
 * @author Damien Arcuset
 * @author Julien Iguchi-Cartigny
 */
public interface SCKey {

    /// data
    byte[] getData();

    /// key version
    byte getSetVersion();

    /// key id
    byte getId();

    /// key type
    KeyType getType();
}
