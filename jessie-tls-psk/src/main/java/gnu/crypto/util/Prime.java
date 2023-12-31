package gnu.crypto.util;

// ----------------------------------------------------------------------------
// $Id: Prime.java,v 1.9 2004/01/14 23:13:25 rsdio Exp $
//
// Copyright (C) 2001, 2002, 2003, 2004 Free Software Foundation, Inc.
//
// This file is part of GNU Crypto.
//
// GNU Crypto is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2, or (at your option)
// any later version.
//
// GNU Crypto is distributed in the hope that it will be useful, but
// WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; see the file COPYING.  If not, write to the
//
//    Free Software Foundation Inc.,
//    59 Temple Place - Suite 330,
//    Boston, MA 02111-1307
//    USA
//
// Linking this library statically or dynamically with other modules is
// making a combined work based on this library.  Thus, the terms and
// conditions of the GNU General Public License cover the whole
// combination.
//
// As a special exception, the copyright holders of this library give
// you permission to link this library with independent modules to
// produce an executable, regardless of the license terms of these
// independent modules, and to copy and distribute the resulting
// executable under terms of your choice, provided that you also meet,
// for each linked independent module, the terms and conditions of the
// license of that module.  An independent module is a module which is
// not derived from or based on this library.  If you modify this
// library, you may extend this exception to your version of the
// library, but you are not obligated to do so.  If you do not wish to
// do so, delete this exception statement from your version.
// ----------------------------------------------------------------------------

import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.math.BigInteger;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * <p>A collection of prime number related utilities used in this library.</p>
 *
 * @version $Revision: 1.9 $
 */
public class Prime {

    // Debugging methods and variables
    // -------------------------------------------------------------------------

    private static final String NAME = "prime";
    private static final boolean DEBUG = false;
    private static final int debuglevel = 5;
    private static final PrintWriter err = new PrintWriter(System.out, true);

    private static void debug(String s) {
        err.println(">>> " + NAME + ": " + s);
    }

    // Constants and variables
    // -------------------------------------------------------------------------

    private static final int DEFAULT_CERTAINTY = 20; // XXX is this a good value?
    private static final BigInteger ZERO = BigInteger.ZERO;
    private static final BigInteger ONE = BigInteger.ONE;
    private static final BigInteger TWO = BigInteger.valueOf(2L);

    /**
     * The first SMALL_PRIME primes: Algorithm P, section 1.3.2, The Art of
     * Computer Programming, Donald E. Knuth.
     */
    private static final int SMALL_PRIME_COUNT = 1000;
    private static final BigInteger[] SMALL_PRIME =
            new BigInteger[SMALL_PRIME_COUNT];

    static {
        long time = -System.currentTimeMillis();
        SMALL_PRIME[0] = TWO;
        int N = 3;
        int J = 0;
        int prime;
        P2:
        while (true) {
            SMALL_PRIME[++J] = BigInteger.valueOf(N);
            if (J >= 999) {
                break P2;
            }
            P4:
            while (true) {
                N += 2;
                P6:
                for (int K = 1; true; K++) {
                    prime = SMALL_PRIME[K].intValue();
                    if ((N % prime) == 0) {
                        continue P4;
                    } else if ((N / prime) <= prime) {
                        continue P2;
                    }
                }
            }
        }
        time += System.currentTimeMillis();
        if (DEBUG && debuglevel > 8) {
            StringBuffer sb;
            for (int i = 0; i < (SMALL_PRIME_COUNT / 10); i++) {
                sb = new StringBuffer();
                for (int j = 0; j < 10; j++) {
                    sb.append(String.valueOf(SMALL_PRIME[i * 10 + j])).append(" ");
                }
                debug(sb.toString());
            }
        }
        if (DEBUG && debuglevel > 4) {
            debug("Generating first " + String.valueOf(SMALL_PRIME_COUNT)
                    + " primes took: " + String.valueOf(time) + " ms.");
        }
    }

    private static final Map knownPrimes = new WeakHashMap();

    // Constructor(s)
    // -------------------------------------------------------------------------

    /**
     * Trivial constructor to enforce Singleton pattern.
     */
    private Prime() {
        super();
    }

    // Class methods
    // -------------------------------------------------------------------------

    /**
     * <p>Trial division for the first 1000 small primes.</p>
     * <p/>
     * <p>Returns <code>true</code> if at least one small prime, among the first
     * 1000 ones, was found to divide the designated number. Retuens <code>false</code>
     * otherwise.</p>
     *
     * @param w the number to test.
     * @return <code>true</code> if at least one small prime was found to divide
     *         the designated number.
     */
    public static boolean hasSmallPrimeDivisor(BigInteger w) {
        BigInteger prime;
        for (int i = 0; i < SMALL_PRIME_COUNT; i++) {
            prime = SMALL_PRIME[i];
            if (w.mod(prime).equals(ZERO)) {
                if (DEBUG && debuglevel > 4) {
                    debug(prime.toString(16) + " | " + w.toString(16) + "...");
                }
                return true;
            }
        }
        if (DEBUG && debuglevel > 4) {
            debug(w.toString(16) + " has no small prime divisors...");
        }
        return false;
    }

    /**
     * <p>Java port of Colin Plumb primality test (Euler Criterion)
     * implementation for a base of 2 --from bnlib-1.1 release, function
     * primeTest() in prime.c. this is his comments; (bn is our w).</p>
     * <p/>
     * <p>"Now, check that bn is prime. If it passes to the base 2, it's prime
     * beyond all reasonable doubt, and everything else is just gravy, but it
     * gives people warm fuzzies to do it.</p>
     * <p/>
     * <p>This starts with verifying Euler's criterion for a base of 2. This is
     * the fastest pseudoprimality test that I know of, saving a modular squaring
     * over a Fermat test, as well as being stronger. 7/8 of the time, it's as
     * strong as a strong pseudoprimality test, too. (The exception being when
     * <code>bn == 1 mod 8</code> and <code>2</code> is a quartic residue, i.e.
     * <code>bn</code> is of the form <code>a^2 + (8*b)^2</code>.) The precise
     * series of tricks used here is not documented anywhere, so here's an
     * explanation. Euler's criterion states that if <code>p</code> is prime
     * then <code>a^((p-1)/2)</code> is congruent to <code>Jacobi(a,p)</code>,
     * modulo <code>p</code>. <code>Jacobi(a, p)</code> is a function which is
     * <code>+1</code> if a is a square modulo <code>p</code>, and <code>-1</code>
     * if it is not. For <code>a = 2</code>, this is particularly simple. It's
     * <code>+1</code> if <code>p == +/-1 (mod 8)</code>, and <code>-1</code> if
     * <code>m == +/-3 (mod 8)</code>. If <code>p == 3 (mod 4)</code>, then all
     * a strong test does is compute <code>2^((p-1)/2)</code>. and see if it's
     * <code>+1</code> or <code>-1</code>. (Euler's criterion says <i>which</i>
     * it should be.) If <code>p == 5 (mod 8)</code>, then <code>2^((p-1)/2)</code>
     * is <code>-1</code>, so the initial step in a strong test, looking at
     * <code>2^((p-1)/4)</code>, is wasted --you're not going to find a
     * <code>+/-1</code> before then if it <b>is</b> prime, and it shouldn't
     * have either of those values if it isn't. So don't bother.</p>
     * <p/>
     * <p>The remaining case is <code>p == 1 (mod 8)</code>. In this case, we
     * expect <code>2^((p-1)/2) == 1 (mod p)</code>, so we expect that the
     * square root of this, <code>2^((p-1)/4)</code>, will be <code>+/-1 (mod p)
     * </code>. Evaluating this saves us a modular squaring 1/4 of the time. If
     * it's <code>-1</code>, a strong pseudoprimality test would call <code>p</code>
     * prime as well. Only if the result is <code>+1</code>, indicating that
     * <code>2</code> is not only a quadratic residue, but a quartic one as well,
     * does a strong pseudoprimality test verify more things than this test does.
     * Good enough.</p>
     * <p/>
     * <p>We could back that down another step, looking at <code>2^((p-1)/8)</code>
     * if there was a cheap way to determine if <code>2</code> were expected to
     * be a quartic residue or not. Dirichlet proved that <code>2</code> is a
     * quadratic residue iff <code>p</code> is of the form <code>a^2 + (8*b^2)</code>.
     * All primes <code>== 1 (mod 4)</code> can be expressed as <code>a^2 +
     * (2*b)^2</code>, but I see no cheap way to evaluate this condition."</p>
     *
     * @param w the number to test.
     * @return <code>true</code> iff the designated number passes Euler criterion
     *         as implemented by Colin Plumb in his <i>bnlib</i> version 1.1.
     */
    public static boolean passEulerCriterion(final BigInteger w) {
        // first check if it's already a known prime
        WeakReference obj = (WeakReference) knownPrimes.get(w);
        if (obj != null && w.equals(obj.get())) {
            if (DEBUG && debuglevel > 4) {
                debug("found in known primes");
            }
            return true;
        }

        BigInteger w_minus_one = w.subtract(ONE);
        BigInteger e = w_minus_one;
        // l is the 3 least-significant bits of e
        int l = e.and(BigInteger.valueOf(7L)).intValue();
        int j = 1; // Where to start in prime array for strong prime tests
        BigInteger A;
        int k;

        if ((l & 7) != 0) {
            e = e.shiftRight(1);
            A = TWO.modPow(e, w);
            if ((l & 7) == 6) { // bn == 7 mod 8, expect +1
                if (A.bitCount() != 1) {
                    if (DEBUG && debuglevel > 4) {
                        debug(w.toString(16) + " fails Euler criterion #1...");
                    }
                    return false; // Not prime
                }
                k = 1;
            } else { // bn == 3 or 5 mod 8, expect -1 == bn-1
                A = A.add(ONE);
                if (!A.equals(w)) {
                    if (DEBUG && debuglevel > 4) {
                        debug(w.toString(16) + " fails Euler criterion #2...");
                    }
                    return false; // Not prime
                }
                k = 1;
                if ((l & 4) != 0) { // bn == 5 mod 8, make odd for strong tests
                    e = e.shiftRight(1);
                    k = 2;
                }
            }
        } else { // bn == 1 mod 8, expect 2^((bn-1)/4) == +/-1 mod bn
            e = e.shiftRight(2);
            A = TWO.modPow(e, w);
            if (A.bitCount() == 1) {
                j = 0; // Re-do strong prime test to base 2
            } else {
                A = A.add(ONE);
                if (!A.equals(w)) {
                    if (DEBUG && debuglevel > 4) {
                        debug(w.toString(16) + " fails Euler criterion #3...");
                    }
                    return false; // Not prime
                }
            }
            // bnMakeOdd(n) = d * 2^s. Replaces n with d and returns s.
            k = e.getLowestSetBit();
            e = e.shiftRight(k);
            k += 2;
        }
        // It's prime!  Now go on to confirmation tests

        // Now, e = (bn-1)/2^k is odd.  k >= 1, and has a given value with
        // probability 2^-k, so its expected value is 2.  j = 1 in the usual case
        // when the previous test was as good as a strong prime test, but 1/8 of
        // the time, j = 0 because the strong prime test to the base 2 needs to
        // be re-done.
        //for (int i = j; i < SMALL_PRIME_COUNT; i++) {
        for (int i = j; i < 13; i++) { // try only the first 13 primes
            A = SMALL_PRIME[i];
            A = A.modPow(e, w);
            if (A.bitCount() == 1) {
                continue; // Passed this test
            }
            l = k;
            while (true) {
//            A = A.add(ONE);
//            if (A.equals(w)) { // Was result bn-1?
                if (A.equals(w_minus_one)) { // Was result bn-1?
                    break; // Prime
                }
                if (--l == 0) { // Reached end, not -1? luck?
                    if (DEBUG && debuglevel > 4) {
                        debug(w.toString(16) + " fails Euler criterion #4...");
                    }
                    return false; // Failed, not prime
                }
                // This portion is executed, on average, once
//            A = A.subtract(ONE); // Put a back where it was
                A = A.modPow(TWO, w);
                if (A.bitCount() == 1) {
                    if (DEBUG && debuglevel > 4) {
                        debug(w.toString(16) + " fails Euler criterion #5...");
                    }
                    return false; // Failed, not prime
                }
            }
            // It worked (to the base primes[i])
        }
        if (DEBUG && debuglevel > 4) {
            debug(w.toString(16) + " passes Euler criterion...");
        }

        // store it in the known primes weak hash-map
        knownPrimes.put(w, new WeakReference(w));

        return true;
    }

    /**
     * <p>Checks Fermat's Little Theorem for base <i>b</i>; i.e.
     * <code><i>b</i>**(w-1) == 1 (mod w)</code>.</p>
     *
     * @param w the number to test.
     * @param t the number of random bases to test.
     * @return <code>true</code> iff <code><i>b</i>**(w-1) == 1 (mod w)</code>,
     *         for some <i>b</i>.
     */
    public static boolean passFermatLittleTheorem(final BigInteger w, int t) {
        final BigInteger w_minus_one = w.subtract(ONE);
        if (t <= 0) {
            t = 10; // XXX
        }
        if (!TWO.modPow(w_minus_one, w).equals(ONE)) {
            if (DEBUG && debuglevel > 4) {
                debug(w.toString(16) + " fails Fermat's little theorem for base 2");
            }
            return false;
        }
        for (int i = 0; i < t; i++) {
            byte[] buf = new byte[(w.bitLength() + 7) / 8 - 1];
            BigInteger base = null;
            do {
                PRNG.nextBytes(buf);
                base = new BigInteger(1, buf);
            } while (base.compareTo(TWO) < 0 || base.compareTo(w_minus_one) > 0);
            if (!base.modPow(w_minus_one, w).equals(ONE)) {
                if (DEBUG && debuglevel > 4) {
                    debug(w.toString(16) + " fails Fermat's little theorem for base "
                            + base.toString(16));
                }
                return false;
            }
        }
        if (DEBUG && debuglevel > 4) {
            debug(w.toString(16) + " passes Fermat's little theorem for " + t + " bases...");
        }
        return true;
    }

    /**
     * <p>Applies the Miller-Rabin strong probabilistic primality test.</p>
     * <p/>
     * <p>The HAC (Handbook of Applied Cryptography), Alfred Menezes & al. Note
     * 4.57 states that for <code>q</code>, <code>n=18</code> is enough while
     * for <code>p</code>, <code>n=6</code> (512 bits) or <code>n=3</cdoe> (1024
     * bits) are enough to yield <i>robust</i> primality tests. The values used
     * are from table 4.4 given in Note 4.49.</p>
     *
     * @param w the number to test.
     * @return <code>true</code> iff the designated number passes the Miller-
     *         Rabin probabilistic primality test for a computed number of rounds.
     */
    public static boolean passMillerRabin(BigInteger n, int t) {
        int nbytes = (n.bitLength() + 7) / 8;
        byte[] ab = new byte[nbytes];

        // 1. Write n - 1 = 2^s * r.
        BigInteger n_minus_1 = n.subtract(ONE);
        BigInteger r = n_minus_1;
        int s = 0;
        while (!r.testBit(0)) {
            r = r.shiftRight(1);
            s++;
        }

        // 2. For i from 1 to t, do:
        for (int i = 0; i < t; i++) {

            // 2.1 Choose a random integer a, 2 <= a <= n - 2.
            BigInteger a;
            do {
                PRNG.nextBytes(ab);
                a = new BigInteger(1, ab);
            } while (a.compareTo(TWO) < 0 || a.compareTo(n) > 0);

            // 2.2 Compute y = a^r mod n.
            BigInteger y = a.modPow(r, n);

            // If y != 1 and y != n - 1, then:
            if (!y.equals(ONE) && !y.equals(n_minus_1)) {
                for (int j = 1; j < s - 1 && !y.equals(n_minus_1); j++) {
                    // Compute y = y^2 mod n.
                    y = y.modPow(TWO, n);

                    // If y = 1 return "composite"
                    if (y.equals(ONE)) {
                        if (DEBUG && debuglevel > 4) {
                            debug(n.toString(16) + " fails Miller-Rabin test...");
                        }
                        return false;
                    }
                }
                // If y != n - 1 return "composite"
                if (!y.equals(n_minus_1)) {
                    if (DEBUG && debuglevel > 4) {
                        debug(n.toString(16) + " fails Miller-Rabin test...");
                    }
                    return false;
                }
            }
        }
        if (DEBUG && debuglevel > 4) {
            debug(n.toString(16) + " passes Miller-Rabin test...");
        }
        return true;
    }

    public static boolean isProbablePrime(BigInteger w) {
        return isProbablePrime(w, DEFAULT_CERTAINTY);
    }

    /**
     * <p>This implementation does not rely solely on the Miller-Rabin strong
     * probabilistic primality test to claim the primality of the designated
     * number. It instead, tries dividing the designated number by the first 1000
     * small primes, and if no divisor was found, invokes a port of Colin Plumb's
     * implementation of the Euler Criterion, then tries the Miller-Rabin test.</p>
     *
     * @param w             the integer to test.
     * @param certainty     the certainty with which to compute the test.
     * @param doMillerRabin if <code>true</code> and the designated integer was
     *                      already found to be a probable prime, then also do a Miller-Rabin test.
     * @return <code>true</code> iff the designated number has no small prime
     *         divisor passes the Euler criterion, and optionally a Miller-Rabin test.
     */
    public static boolean isProbablePrime(BigInteger w, int certainty) {
        // Nonnumbers are not prime.
        if (w == null) {
            return false;
        }

        // eliminate trivial cases when w == 0 or 1
        if (w.equals(ZERO) || w.equals(ONE)) {
            return false;
        }

        // Test if w is a known small prime.
        for (int i = 0; i < SMALL_PRIME_COUNT; i++) {
            if (w.equals(SMALL_PRIME[i])) {
                if (DEBUG && debuglevel > 4) {
                    debug(w.toString(16) + " is a small prime");
                }
                return true;
            }
        }

        // trial division with first 1000 primes
        if (hasSmallPrimeDivisor(w)) {
            if (DEBUG && debuglevel > 4) {
                debug(w.toString(16) + " has a small prime divisor. Rejected...");
            }
            return false;
        }

        // Do a check with Fermat's little theorem.
        if (passFermatLittleTheorem(w, certainty)) {
            if (DEBUG && debuglevel > 4) {
                debug(w.toString(16) + " passes Fermat's little theorem...");
            }
        } else {
            if (DEBUG && debuglevel > 4) {
                debug(w.toString(16) + " fails Fermat's little theorem. Rejected...");
            }
            return false;
        }

        // Euler's criterion.
        if (passEulerCriterion(w)) {
            if (DEBUG && debuglevel > 4) {
                debug(w.toString(16) + " passes Euler's criterion...");
            }
        } else {
            if (DEBUG && debuglevel > 4) {
                debug(w.toString(16) + " fails Euler's criterion. Rejected...");
            }
            return false;
        }

        // Miller-Rabin probabilistic primality test.
        if (passMillerRabin(w, certainty)) {
            if (DEBUG && debuglevel > 4) {
                debug(w.toString(16) + " passes Miller-Rabin PPT...");
            }
        } else {
            if (DEBUG && debuglevel > 4) {
                debug(w.toString(16) + " fails Miller-Rabin PPT. Rejected...");
            }
            return false;
        }

        if (DEBUG && debuglevel > 4) {
            debug(w.toString(16) + " is probable prime. Accepted...");
        }

        // now compare to JDK primality test
        if (DEBUG && debuglevel > 0 && !w.isProbablePrime(certainty)) {
            System.err.println("The gnu.crypto library and the JDK disagree on "
                    + "whether 0x" + w.toString(16) + " is a probable prime or not.");
            System.err.println("While this library claims it is, the JDK claims"
                    + " the opposite.");
            System.err.println("Please contact the maintainer of this library, "
                    + "and provide this message for further investigation. TIA");
        }

        return true;
    }
}
