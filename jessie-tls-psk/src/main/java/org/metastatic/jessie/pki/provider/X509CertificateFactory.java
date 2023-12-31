/* X509CertificateFactory.java -- generates X.509 certificates.
   Copyright (C) 2003 Free Software Foundation, Inc.

This file is part of GNU Classpath.

GNU Classpath is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2, or (at your option)
any later version.

GNU Classpath is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
General Public License for more details.

You should have received a copy of the GNU General Public License
along with GNU Classpath; see the file COPYING.  If not, write to the
Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
02111-1307 USA.

Linking this library statically or dynamically with other modules is
making a combined work based on this library.  Thus, the terms and
conditions of the GNU General Public License cover the whole
combination.

As a special exception, the copyright holders of this library give you
permission to link this library with independent modules to produce an
executable, regardless of the license terms of these independent
modules, and to copy and distribute the resulting executable under
terms of your choice, provided that you also meet, for each linked
independent module, the terms and conditions of the license of that
module.  An independent module is a module which is not derived from
or based on this library.  If you modify this library, you may extend
this exception to your version of the library, but you are not
obligated to do so.  If you do not wish to do so, delete this
exception statement from your version. */

package org.metastatic.jessie.pki.provider;

import org.metastatic.jessie.pki.X509CRLImpl;
import org.metastatic.jessie.pki.X509CertificateImpl;
import org.metastatic.jessie.pki.io.Base64InputStream;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.*;
import java.util.Collection;
import java.util.LinkedList;

public class X509CertificateFactory extends CertificateFactorySpi {

    // Constants.
    // ------------------------------------------------------------------------

    public static final String BEGIN_CERTIFICATE = "-----BEGIN CERTIFICATE-----";
    public static final String END_CERTIFICATE = "-----END CERTIFICATE-----";
    public static final String BEGIN_X509_CRL = "-----BEGIN X509 CRL-----";
    public static final String END_X509_CRL = "-----END X509 CRL-----";

    // Constructors.
    // ------------------------------------------------------------------------

    public X509CertificateFactory() {
        super();
    }

    // Instance methods.
    // ------------------------------------------------------------------------

    public Certificate engineGenerateCertificate(InputStream inStream)
            throws CertificateException {
        try {
            return generateCert(inStream);
        } catch (IOException ioe) {
            throw new CertificateException(ioe.toString());
        }
    }

    public Collection engineGenerateCertificates(InputStream inStream)
            throws CertificateException {
        LinkedList certs = new LinkedList();
        while (true) {
            try {
                certs.add(generateCert(inStream));
            } catch (EOFException eof) {
                break;
            } catch (IOException ioe) {
                throw new CertificateException(ioe.toString());
            }
        }
        return certs;
    }

    public CRL engineGenerateCRL(InputStream inStream) throws CRLException {
        try {
            return generateCRL(inStream);
        } catch (IOException ioe) {
            throw new CRLException(ioe.toString());
        }
    }

    public Collection engineGenerateCRLs(InputStream inStream)
            throws CRLException {
        LinkedList crls = new LinkedList();
        while (true) {
            try {
                crls.add(generateCRL(inStream));
            } catch (EOFException eof) {
                break;
            } catch (IOException ioe) {
                throw new CRLException(ioe.toString());
            }
        }
        return crls;
    }

    // public CertPath engineGenerateCertPath(List certs)
    // {
    // return new X509CertPath(certs);
    // }

    // public CertPath engineGenerateCertPath(InputStream in)
    // throws CertificateEncodingException
    // {
    // return new X509CertPath(in);
    // }

    // public CertPath engineGenerateCertPath(InputStream in, String encoding)
    // throws CertificateEncodingException
    // {
    // return new X509CertPath(in, encoding);
    // }

    // public Iterator engineGetCertPathEncodings()
    // {
    // return X509CertPath.ENCODINGS.iterator();
    // }

    // Own methods.
    // ------------------------------------------------------------------------

    private X509CertificateImpl generateCert(InputStream inStream)
            throws IOException, CertificateException {
        if (inStream == null)
            throw new CertificateException("missing input stream");
        if (!inStream.markSupported())
            inStream = new BufferedInputStream(inStream, 8192);
        inStream.mark(20);
        int i = inStream.read();
        if (i == -1)
            throw new EOFException();

        // If the input is in binary DER format, the first byte MUST be
        // 0x30, which stands for the ASN.1 [UNIVERSAL 16], which is the
        // UNIVERSAL SEQUENCE, with the CONSTRUCTED bit (0x20) set.
        //
        // So if we do not see 0x30 here we will assume it is in Base-64.
        if (i != 0x30) {
            inStream.reset();
            StringBuffer line = new StringBuffer(80);
            do {
                line.setLength(0);
                do {
                    i = inStream.read();
                    if (i == -1)
                        throw new EOFException();
                    if (i != '\n' && i != '\r')
                        line.append((char) i);
                } while (i != '\n' && i != '\r');
            } while (!line.toString().equals(BEGIN_CERTIFICATE));
            X509CertificateImpl ret = new X509CertificateImpl(
                    new BufferedInputStream(new Base64InputStream(inStream),
                            8192));
            line.setLength(0);
            line.append('-'); // Base64InputStream will eat this.
            do {
                i = inStream.read();
                if (i == -1)
                    throw new EOFException();
                if (i != '\n' && i != '\r')
                    line.append((char) i);
            } while (i != '\n' && i != '\r');
            // XXX ???
            if (!line.toString().equals(END_CERTIFICATE))
                throw new CertificateException("no end-of-certificate marker");
            return ret;
        } else {
            inStream.reset();
            return new X509CertificateImpl(inStream);
        }
    }

    private X509CRLImpl generateCRL(InputStream inStream) throws IOException,
            CRLException {
        if (inStream == null)
            throw new CRLException("missing input stream");
        if (!inStream.markSupported())
            inStream = new BufferedInputStream(inStream, 8192);
        inStream.mark(20);
        int i = inStream.read();
        if (i == -1)
            throw new EOFException();

        // If the input is in binary DER format, the first byte MUST be
        // 0x30, which stands for the ASN.1 [UNIVERSAL 16], which is the
        // UNIVERSAL SEQUENCE, with the CONSTRUCTED bit (0x20) set.
        //
        // So if we do not see 0x30 here we will assume it is in Base-64.
        if (i != 0x30) {
            inStream.reset();
            StringBuffer line = new StringBuffer(80);
            do {
                line.setLength(0);
                do {
                    i = inStream.read();
                    if (i == -1)
                        throw new EOFException();
                    if (i != '\n' && i != '\r')
                        line.append((char) i);
                } while (i != '\n' && i != '\r');
            } while (!line.toString().startsWith(BEGIN_X509_CRL));
            X509CRLImpl ret = new X509CRLImpl(new BufferedInputStream(
                    new Base64InputStream(inStream), 8192));
            line.setLength(0);
            line.append('-'); // Base64InputStream will eat this.
            do {
                i = inStream.read();
                if (i == -1)
                    throw new EOFException();
                if (i != '\n' && i != '\r')
                    line.append((char) i);
            } while (i != '\n' && i != '\r');
            // XXX ???
            if (!line.toString().startsWith(END_X509_CRL))
                throw new CRLException("no end-of-CRL marker");
            return ret;
        } else {
            inStream.reset();
            return new X509CRLImpl(inStream);
        }
    }
}
