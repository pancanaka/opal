/**
 * Copyright or © or Copr. SSD Research Team 2011
 * (XLIM Labs, University of Limoges, France).
 *
 * Contact: ssd@xlim.fr
 *
 * This software is a Java 6 library that implements Global Platform 2.x card
 * specification. OPAL is able to upload and manage Java Card applet lifecycle
 * on Java Card while dealing of the authentication of the user and encryption
 * of the communication between the user and the card. OPAL is also able
 * to manage different implementations of the specification via a pluggable
 * interface.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL-C
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
package fr.xlim.ssd.opal.library.config;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Abstract class to define common methods between the two version of Visa Global Platform implementations.
 *
 * @author Damien Arcuset
 * @author Eric Linke
 * @author Julien Iguchi-Cartigny
 */
public abstract class SCAbstractGemVisa implements SCKey, SCDerivableKey {

    private byte setVersion;

    private byte[] data;

    public SCAbstractGemVisa(byte setVersion, byte[] data) {
        this.setVersion = setVersion;

        if (data == null) {
            throw new IllegalArgumentException("data must not be null");
        }

        if (data.length != 24) {
            throw new IllegalArgumentException("data must be 24 bytes long");
        }

        this.data = data;
    }

    @Override
    public SCGPKey[] deriveKey(byte[] keydata) {

        if (keydata == null) {
            throw new IllegalArgumentException("keydata must not be null");
        }

        if (keydata.length != 10) {
            throw new IllegalArgumentException("keydata must be 10 bytes long");
        }

        byte[] divDataStaticEnc = getDivDataStaticEnc(keydata);
        byte[] divDataStaticMac = getDivDataStaticMac(keydata);
        byte[] divDataStaticKek = getDivDataStaticKek(keydata);

        byte[] staticEncKey = new byte[24];
        byte[] staticMacKey = new byte[24];
        byte[] staticKekKey = new byte[24];

        try {
            Cipher myCipher;
            myCipher = Cipher.getInstance("DESede/ECB/NoPadding");
            myCipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(this.data, "DESede"));
            byte[] sEncKey = myCipher.doFinal(divDataStaticEnc);
            System.arraycopy(sEncKey, 0, staticEncKey, 0, 16);
            System.arraycopy(sEncKey, 0, staticEncKey, 16, 8);

            myCipher = Cipher.getInstance("DESede/ECB/NoPadding");
            myCipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(this.data, "DESede"));
            byte[] sMacKey = myCipher.doFinal(divDataStaticMac);
            System.arraycopy(sMacKey, 0, staticMacKey, 0, 16);
            System.arraycopy(sMacKey, 0, staticMacKey, 16, 8);

            myCipher = Cipher.getInstance("DESede/ECB/NoPadding");
            myCipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(this.data, "DESede"));
            byte[] sKekKey = myCipher.doFinal(divDataStaticKek);
            System.arraycopy(sKekKey, 0, staticKekKey, 0, 16);
            System.arraycopy(sKekKey, 0, staticKekKey, 16, 8);

        } catch (NoSuchAlgorithmException e) {
            throw new UnsupportedOperationException("Cannot find algorithm", e);
        } catch (NoSuchPaddingException e) {
            throw new UnsupportedOperationException("No such PADDING problem", e);
        } catch (InvalidKeyException e) {
            throw new UnsupportedOperationException("Key problem", e);
        } catch (IllegalBlockSizeException e) {
            throw new UnsupportedOperationException("Block size problem", e);
        } catch (BadPaddingException e) {
            throw new UnsupportedOperationException("Bad PADDING problem", e);
        }

        SCGPKey[] res = new SCGPKey[3];
        res[0] = new SCGPKey(setVersion, (byte) 1, KeyType.DES_ECB, staticEncKey);
        res[1] = new SCGPKey(setVersion, (byte) 2, KeyType.DES_ECB, staticMacKey);
        res[2] = new SCGPKey(setVersion, (byte) 3, KeyType.DES_ECB, staticKekKey);
        return res;
    }

    protected abstract byte[] getDivDataStaticEnc(byte[] keydata);

    protected abstract byte[] getDivDataStaticMac(byte[] keydata);

    protected abstract byte[] getDivDataStaticKek(byte[] keydata);

    @Override
    public byte[] getValue() {
        return this.data.clone();
    }

    @Override
    public byte getVersion() {
        return this.setVersion;
    }

    @Override
    public byte getId() {
        return 1;
    }

    @Override
    public KeyType getType() {
        return KeyType.MOTHER_KEY;
    }
}
