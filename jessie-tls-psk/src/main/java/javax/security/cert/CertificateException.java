/* CertificateException.java -- certificate exception.
   Copyright (C) 2003  Casey Marshall <rsdio@metastatic.org>

This program is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by the
Free Software Foundation; either version 2 of the License, or (at your
option) any later version.

This program is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
General Public License for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the

   Free Software Foundation, Inc.,
   59 Temple Place, Suite 330,
   Boston, MA  02111-1307
   USA

Linking this library statically or dynamically with other modules is
making a combined work based on this library.  Thus, the terms and
conditions of the GNU General Public License cover the whole
combination.

As a special exception, the copyright holders of this library give you
permission to link this library with independent modules to produce an
executable, regardless of the license terms of these independent
modules, and to copy and distribute the resulting executable under terms
of your choice, provided that you also meet, for each linked independent
module, the terms and conditions of the license of that module.  An
independent module is a module which is not derived from or based on
this library.  If you modify this library, you may extend this exception
to your version of the library, but you are not obligated to do so.  If
you do not wish to do so, delete this exception statement from your
version.  */

package javax.security.cert;

/**
 * <p>
 * Signals a generic problem with certificates.
 * </p>
 * <p>
 * <b>This class is deprecated in favor of the
 * {@link java.security.cert.CertificateException} class. It should not be used
 * in new applications.</b>
 * </p>
 * <p>
 * <strong>NOTE:</strong> This class is deprecated. Use
 * java.security.cert.CertificateException instead.
 * </p>
 */
public class CertificateException extends Exception {

    public CertificateException() {
        super();
    }

    public CertificateException(String msg) {
        super(msg);
    }
}