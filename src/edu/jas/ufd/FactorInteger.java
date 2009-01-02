/*
 * $Id$
 */

package edu.jas.ufd;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import edu.jas.arith.BigInteger;
import edu.jas.arith.ModInteger;
import edu.jas.arith.ModIntegerRing;
import edu.jas.arith.PrimeList;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.structure.RingFactory;
import edu.jas.util.KsubSet;


/**
 * Integer coefficients factorization algorithms.
 * @author Heinz Kredel
 */

public class FactorInteger //<C extends GcdRingElem<C> > 
        extends FactorAbstract<BigInteger> {


    private static final Logger logger = Logger.getLogger(FactorInteger.class);


    private final boolean debug = true || logger.isInfoEnabled();


    /**
     * GenPolynomial base factorization of a squarefree polynomial.
     * @param P squarefree and primitive! GenPolynomial<BigInteger>.
     * @return [p_1,...,p_k] with P = prod_{i=1, ..., k} p_i.
     */
    @Override
    public List<GenPolynomial<BigInteger>> baseFactorsSquarefree(
            GenPolynomial<BigInteger> P) {
        if (P == null) {
            throw new RuntimeException(this.getClass().getName() + " P == null");
        }
        List<GenPolynomial<BigInteger>> factors = new ArrayList<GenPolynomial<BigInteger>>();
        if (P.isZERO()) {
            return factors;
        }
        if (P.isONE()) {
            factors.add(P);
            return factors;
        }
        GenPolynomialRing<BigInteger> pfac = P.ring;
        if (pfac.nvar > 1) {
            throw new RuntimeException(this.getClass().getName()
                    + " only for univariate polynomials");
        }
        // compute norm
        BigInteger an = P.maxNorm();
        BigInteger ac = P.leadingBaseCoefficient();
        //compute factor coefficient bounds
        ExpVector degv = P.degreeVector();
        BigInteger M = an.multiply(PolyUtil.factorBound(degv));
        M = M.multiply(ac.multiply(ac.fromInteger(8)));
        System.out.println("M = " + M);

        //initialize prime list and degree vector
        PrimeList primes = new PrimeList(PrimeList.Range.small);
        int pn = 30; //primes.size();
        ModIntegerRing cofac = new ModIntegerRing(13, true);
        RingFactory<ModInteger> mcofac = new ModIntegerRing(13, true);
        GreatestCommonDivisorAbstract<ModInteger> engine = (GreatestCommonDivisorAbstract<ModInteger>) GCDFactory
                .<ModInteger> getImplementation(mcofac);
        GenPolynomial<ModInteger> am = null;
        GenPolynomialRing<ModInteger> mfac;
        final int TT = 5;
        List<GenPolynomial<ModInteger>>[] modfac = (List<GenPolynomial<ModInteger>>[]) new List[TT];
        List<GenPolynomial<BigInteger>>[] intfac = (List<GenPolynomial<BigInteger>>[]) new List[TT];
        List<GenPolynomial<ModInteger>> mlist = null;
        List<GenPolynomial<BigInteger>> ilist = null;
        int i = 0;
        if (debug) {
            logger.debug("an  = " + an);
            logger.debug("ac  = " + ac);
            logger.debug("M   = " + M);
            logger.info("degv = " + degv);
        }
        Iterator<java.math.BigInteger> pit = primes.iterator();
        //         for ( int k = 0; k < 100; k++ ) {
        //             if ( pit.hasNext() ) {
        //                 java.math.BigInteger p = pit.next();
        //             }
        //         }
        ModInteger nf = null;
        for (int k = 0; k < TT; k++) {
            // for ( java.math.BigInteger p : primes ) {
            while (pit.hasNext()) {
                java.math.BigInteger p = pit.next();
                //System.out.println("next run ++++++++++++++++++++++++++++++++++");
                if (++i >= pn) {
                    logger.error("prime list exhausted, pn = " + pn);
                    throw new RuntimeException("prime list exhausted");
                }
                //             if ( i < 2 ) {
                //                 p = new java.math.BigInteger("23");
                //             }
                cofac = new ModIntegerRing(p, true);
                nf = cofac.fromInteger(ac.getVal());
                if (nf.isZERO()) {
                    System.out.println("unlucky prime = " + p);
                    continue;
                }
                // initialize polynomial factory and map polynomial
                mfac = new GenPolynomialRing<ModInteger>(cofac, pfac);
                am = PolyUtil.<ModInteger> fromIntegerCoefficients(mfac, P);
                if (!am.degreeVector().equals(degv)) { // allways true
                    System.out.println("unlucky prime = " + p);
                    continue;
                }
                GenPolynomial<ModInteger> ap = PolyUtil.<ModInteger> baseDeriviative(am);
                if (ap.isZERO()) {
                    System.out.println("unlucky prime = " + p);
                    continue;
                }
                GenPolynomial<ModInteger> g = engine.baseGcd(am, ap);
                if (g.isONE()) {
                    System.out.println("**lucky prime = " + p);
                    break;
                }
            }
            // now am is squarefree mod p, so factor mod p
            FactorModular mengine = new FactorModular();
            if (!nf.isONE()) {
                System.out.println("nf = " + nf);
                am = am.divide(nf);
            }
            mlist = mengine.baseFactorsSquarefree(am);
            System.out.println("modlist  = " + mlist);
            if (mlist.size() <= 1) {
                factors.add(P);
                return factors;
            }
            if (!nf.isONE()) {
                GenPolynomial<ModInteger> mp = mlist.get(0);
                System.out.println("mp = " + mp);
                mp = mp.multiply(nf);
                System.out.println("mp = " + mp);
                mlist.set(0, mp);
            }
            modfac[k] = mlist;
        }

        int min = Integer.MAX_VALUE;
        for (int k = 0; k < TT; k++) {
            int s = modfac[k].size();
            System.out.println("mod s = " + s);
            if (s < min) {
                min = s;
                mlist = modfac[k];
            }
        }

        for (int k = 0; k < TT; k++) {
            factors = new ArrayList<GenPolynomial<BigInteger>>();
            mlist = modfac[k];
            GenPolynomial<BigInteger> PP = P;
            System.out.println("modlist  = " + mlist);
            // lift via Hensel
            ilist = PolyUfdUtil.liftHenselQuadratic(PP, M, mlist);
            System.out.println("intlist  = " + ilist);

            // combine trial factors
            int dl = (ilist.size() + 1) / 2;
            GenPolynomial<BigInteger> u = PP;
            for (int j = 1; j <= dl; j++) {
                KsubSet<GenPolynomial<BigInteger>> ps = new KsubSet<GenPolynomial<BigInteger>>(
                        ilist, j);
                for (List<GenPolynomial<BigInteger>> flist : ps) {
                    //System.out.println("flist = " + flist);
                    GenPolynomial<BigInteger> trial = pfac.getONE();
                    for (int kk = 0; kk < flist.size(); kk++) {
                        trial = trial.multiply(flist.get(kk));
                    }
                    //System.out.println("t   = " + trial); 
                    //System.out.println("p rem = " + PolyUtil.<BigInteger>basePseudoRemainder(u, trial));
                    if (PolyUtil.<BigInteger> basePseudoRemainder(u, trial).isZERO()) {
                        System.out.println("trial    = " + trial);
                        factors.add(trial);
                        u = PolyUtil.<BigInteger> basePseudoDivide(u, trial); //u.divide( trial );
                        if (ilist.removeAll(flist)) {
                            System.out.println("new ilist= " + ilist);
                            dl = (ilist.size() + 1) / 2;
                            j = 1;
                            if (ilist.size() > 0) {
                                ps = new KsubSet<GenPolynomial<BigInteger>>(ilist, j);
                            }
                            break;
                        } else {
                            System.out.println("error removing flist from ilist = "
                                    + ilist);
                        }
                    }
                }
            }
            if (!u.isONE() && !u.equals(P)) {
                System.out.println("rest u = " + u);
                factors.add(u);
            }
            if (factors.size() == 0) {
                System.out.println("irred u = " + u);
                factors.add(PP);
            }
            intfac[k] = factors;
        }

        int max = 0;
        for (int k = 0; k < TT; k++) {
            int s = intfac[k].size();
            System.out.println("int s = " + s);
            if (s > max) {
                max = s;
                ilist = intfac[k];
            }
        }
        factors = ilist;
        return factors;
    }

}
