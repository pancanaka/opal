package fr.xlim.ssd.opal.library.config;

/**
 * Define the interface for every key used in Secure Communication process.
 *
 * @author Damien Arcuset
 * @author Julien Iguchi-Cartigny
 */
public interface SCKey {

    /// key type
    KeyType getType();

    /// key version
    byte getVersion();

    /// key id
    byte getId();

    /// value
    byte[] getValue();

}
