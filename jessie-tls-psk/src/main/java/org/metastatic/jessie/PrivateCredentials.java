/* PrivateCredentials.java -- private key/certificate pairs.
   Copyright (C) 2003  Casey Marshall <rsdio@metastatic.org>

This file is a part of Jessie.

Jessie is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by the
Free Software Foundation; either version 2 of the License, or (at your
option) any later version.

Jessie is distributed in the hope that it will be useful, but WITHOUT
ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License along
with Jessie; if not, write to the

   Free Software Foundation, Inc.,
   59 Temple Place, Suite 330,
   Boston, MA  02111-1307
   USA  */

package org.metastatic.jessie;

import gnu.crypto.hash.HashFactory;
import gnu.crypto.hash.IMessageDigest;
import gnu.crypto.mode.IMode;
import gnu.crypto.mode.ModeFactory;
import gnu.crypto.pad.WrongPaddingException;
import org.metastatic.callbacks.ConsoleCallbackHandler;
import org.metastatic.jessie.pki.der.DER;
import org.metastatic.jessie.pki.der.DERReader;

import javax.net.ssl.ManagerFactoryParameters;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.DSAPrivateKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * An instance of a manager factory parameters for holding a single
 * certificate/private key pair, encoded in PEM format.
 */
public class PrivateCredentials implements ManagerFactoryParameters {

    // Fields.
    // -------------------------------------------------------------------------

    public static final String BEGIN_DSA = "-----BEGIN DSA PRIVATE KEY";
    public static final String END_DSA = "-----END DSA PRIVATE KEY";
    public static final String BEGIN_RSA = "-----BEGIN RSA PRIVATE KEY";
    public static final String END_RSA = "-----END RSA PRIVATE KEY";

    private List privateKeys;
    private List certChains;

    // Constructor.
    // -------------------------------------------------------------------------

    public PrivateCredentials() {
        privateKeys = new LinkedList();
        certChains = new LinkedList();
    }

    // Instance methods.
    // -------------------------------------------------------------------------

    public void add(InputStream certChain, InputStream privateKey)
            throws CertificateException, InvalidKeyException,
            InvalidKeySpecException, IOException, NoSuchAlgorithmException,
            WrongPaddingException {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        Collection certs = cf.generateCertificates(certChain);
        X509Certificate[] chain = (X509Certificate[]) certs
                .toArray(new X509Certificate[0]);

        String alg = null;
        String line = readLine(privateKey);
        String finalLine = null;
        if (line.startsWith(BEGIN_DSA)) {
            alg = "DSA";
            finalLine = END_DSA;
        } else if (line.startsWith(BEGIN_RSA)) {
            alg = "RSA";
            finalLine = END_RSA;
        } else
            throw new IOException("Unknown private key type.");

        boolean encrypted = false;
        String cipher = null;
        String salt = null;
        StringBuffer base64 = new StringBuffer();
        while (true) {
            line = readLine(privateKey);
            if (line == null)
                throw new EOFException("premature end-of-file");
            else if (line.startsWith("Proc-Type: 4,ENCRYPTED"))
                encrypted = true;
            else if (line.startsWith("DEK-Info: ")) {
                int i = line.indexOf(',');
                if (i < 0)
                    cipher = line.substring(10).trim();
                else {
                    cipher = line.substring(10, i).trim();
                    salt = line.substring(i + 1).trim();
                }
            } else if (line.startsWith(finalLine))
                break;
            else if (line.length() > 0) {
                base64.append(line);
                base64.append(System.getProperty("line.separator"));
            }
        }

        byte[] enckey = Base64.decode(base64.toString());
        if (encrypted) {
            enckey = decryptKey(enckey, cipher, toByteArray(salt));
        }

        DERReader der = new DERReader(enckey);
        if (der.read().getTag() != DER.SEQUENCE)
            throw new IOException("malformed DER sequence");
        der.read(); // version

        KeyFactory kf = KeyFactory.getInstance(alg);
        KeySpec spec = null;
        if (alg.equals("DSA")) {
            BigInteger p = (BigInteger) der.read().getValue();
            BigInteger q = (BigInteger) der.read().getValue();
            BigInteger g = (BigInteger) der.read().getValue();
            der.read(); // y
            BigInteger x = (BigInteger) der.read().getValue();
            spec = new DSAPrivateKeySpec(x, p, q, g);
        } else {
            spec = new RSAPrivateCrtKeySpec((BigInteger) der.read().getValue(), // modulus
                    (BigInteger) der.read().getValue(), // pub exponent
                    (BigInteger) der.read().getValue(), // priv expenent
                    (BigInteger) der.read().getValue(), // prime p
                    (BigInteger) der.read().getValue(), // prime q
                    (BigInteger) der.read().getValue(), // d mod (p-1)
                    (BigInteger) der.read().getValue(), // d mod (q-1)
                    (BigInteger) der.read().getValue()); // coefficient
        }
        privateKeys.add(kf.generatePrivate(spec));
        certChains.add(chain);
    }

    public List getPrivateKeys() {
        if (isDestroyed()) {
            throw new IllegalStateException("this object is destroyed");
        }
        return privateKeys;
    }

    public List getCertChains() {
        return certChains;
    }

    public void destroy() {
        privateKeys.clear();
        privateKeys = null;
    }

    public boolean isDestroyed() {
        return (privateKeys == null);
    }

    // Own methods.
    // -------------------------------------------------------------------------

    private String readLine(InputStream in) throws IOException {
        boolean eol_is_cr = System.getProperty("line.separator").equals("\r");
        StringBuffer str = new StringBuffer();
        while (true) {
            int i = in.read();
            if (i == -1) {
                if (str.length() > 0)
                    break;
                else
                    return null;
            } else if (i == '\r') {
                if (eol_is_cr)
                    break;
            } else if (i == '\n')
                break;
            else
                str.append((char) i);
        }
        return str.toString();
    }

    private byte[] decryptKey(byte[] ct, String cipher, byte[] salt)
            throws IOException, InvalidKeyException, WrongPaddingException {
        byte[] pt = new byte[ct.length];
        IMode mode = null;
        if (cipher.equals("DES-EDE3-CBC")) {
            mode = ModeFactory.getInstance("CBC", "TripleDES", 8);
            HashMap attr = new HashMap();
            attr.put(IMode.KEY_MATERIAL, deriveKey(salt, 24));
            attr.put(IMode.IV, salt);
            attr.put(IMode.STATE, new Integer(IMode.DECRYPTION));
            mode.init(attr);
        } else if (cipher.equals("DES-CBC")) {
            mode = ModeFactory.getInstance("CBC", "DES", 8);
            HashMap attr = new HashMap();
            attr.put(IMode.KEY_MATERIAL, deriveKey(salt, 8));
            attr.put(IMode.IV, salt);
            attr.put(IMode.STATE, new Integer(IMode.DECRYPTION));
            mode.init(attr);
        } else
            throw new IllegalArgumentException("unknown cipher: " + cipher);

        for (int i = 0; i < ct.length; i += 8)
            mode.update(ct, i, pt, i);

        int pad = pt[pt.length - 1];
        if (pad < 1 || pad > 8)
            throw new WrongPaddingException();
        for (int i = pt.length - pad; i < pt.length; i++) {
            if (pt[i] != pad)
                throw new WrongPaddingException();
        }

        byte[] result = new byte[pt.length - pad];
        System.arraycopy(pt, 0, result, 0, result.length);
        return result;
    }

    private byte[] deriveKey(byte[] salt, int keylen) throws IOException {
        CallbackHandler passwordHandler = new ConsoleCallbackHandler();
        try {
            Class c = Class.forName(Security
                    .getProperty("jessie.password.handler"));
            passwordHandler = (CallbackHandler) c.newInstance();
        } catch (Exception x) {
        }

        PasswordCallback passwdCallback = new PasswordCallback(
                "Enter PEM passphrase: ", false);
        try {
            passwordHandler.handle(new Callback[]{passwdCallback});
        } catch (UnsupportedCallbackException uce) {
            throw new IOException("specified handler cannot handle passwords");
        }
        char[] passwd = passwdCallback.getPassword();

        IMessageDigest md5 = HashFactory.getInstance("MD5");
        byte[] key = new byte[keylen];
        int count = 0;
        while (count < keylen) {
            for (int i = 0; i < passwd.length; i++)
                md5.update((byte) passwd[i]);
            md5.update(salt, 0, salt.length);
            byte[] digest = md5.digest();
            int len = Math.min(digest.length, keylen - count);
            System.arraycopy(digest, 0, key, count, len);
            count += len;
            if (count >= keylen)
                break;
            md5.reset();
            md5.update(digest, 0, digest.length);
        }
        passwdCallback.clearPassword();
        return key;
    }

    private byte[] toByteArray(String hex) {
        hex = hex.toLowerCase();
        byte[] buf = new byte[hex.length() / 2];
        int j = 0;
        for (int i = 0; i < buf.length; i++) {
            buf[i] = (byte) ((Character.digit(hex.charAt(j++), 16) << 4) | Character
                    .digit(hex.charAt(j++), 16));
        }
        return buf;
    }
}
