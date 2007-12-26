/*
 * $Id$
 */

package edu.jas.ring;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import edu.jas.structure.RingElem;
import edu.jas.structure.RegularRingElem;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;

import edu.jas.ring.OrderedRPairlist;

/**
 * R-Groebner Base sequential algorithm.
 * Implements R-Groebner bases and GB test.
 * <b>Note:</b> Minimal reduced GBs are not unique.
 * see BWK, section 10.1.
 * @author Heinz Kredel
 */

public class RGroebnerBaseSeq<C extends RegularRingElem<C>> 
       extends GroebnerBaseAbstract<C>  {


    private static final Logger logger = Logger.getLogger(RGroebnerBaseSeq.class);
    private final boolean debug = logger.isDebugEnabled();



    /**
     * Reduction engine.
     */
    protected RReduction<C> red;  // shadow super.red ??


    /**
     * Constructor.
     */
    public RGroebnerBaseSeq() {
        this( new RReductionSeq<C>() );
    }


    /**
     * Constructor.
     * @param red D-Reduction engine
     */
    public RGroebnerBaseSeq(RReduction<C> red) {
        super(red);
        this.red = red;
    }


    /**
     * R-Groebner base test.
     * @typeparam C coefficient type.
     * @param modv module variable number.
     * @param F polynomial list.
     * @return true, if F is a D-Groebner base, else false.
     */
    public boolean isGB(int modv, List<GenPolynomial<C>> F) {  
        if ( ! red.isBooleanClosed(F) ) {
           return false;
        }
        GenPolynomial<C> pi, pj, s, h, d;
        for ( int i = 0; i < F.size(); i++ ) {
            pi = F.get(i);
            for ( int j = i+1; j < F.size(); j++ ) {
                pj = F.get(j);
                if ( ! red.moduleCriterion( modv, pi, pj ) ) {
                   continue;
                }
                // works ?
                //if ( ! red.criterion4( pi, pj ) ) { 
                //   continue;
                //}
                s = red.SPolynomial( pi, pj );
                if ( ! s.isZERO() ) {
                   s = red.normalform( F, s );
                }
                if ( ! s.isZERO() ) {
                   System.out.println("p"+i+" = "+pi);
                   System.out.println("p"+j+" = "+pj);
                   System.out.println("s-pol = " + red.SPolynomial( pi, pj ) );
                   System.out.println("s-pol("+i+","+j+") != 0: " + s);
                   return false;
                }
            }
        }
        return true;
    }


    /**
     * R-Groebner base using pairlist class.
     * @typeparam C coefficient type.
     * @param modv module variable number.
     * @param F polynomial list.
     * @return GB(F) a R-Groebner base of F.
     */
    public List<GenPolynomial<C>> 
             GB( int modv, 
                 List<GenPolynomial<C>> F ) {  
        GenPolynomial<C> p;
        List<GenPolynomial<C>> G = new ArrayList<GenPolynomial<C>>();

        List<GenPolynomial<C>> bcF = red.reducedBooleanClosure(F);
        logger.info("#bcF-#F = " + (bcF.size()-F.size()));
        F = bcF;

        OrderedRPairlist<C> pairlist = null; 
        int l = F.size();
        ListIterator<GenPolynomial<C>> it = F.listIterator();
        while ( it.hasNext() ) { 
            p = it.next();
            if ( !p.isZERO() ) {
               p = p.monic(); //p.abs(); // not monic, monic if boolean closed
               if ( p.isONE() ) {
                  G.clear(); G.add( p );
                  return G; // since no threads are activated
               }
               G.add( p ); //G.add( 0, p ); //reverse list
               if ( pairlist == null ) {
                  pairlist = new OrderedRPairlist<C>( modv, p.ring );
               }
               // putOne not required
               pairlist.put( p );
            } else { 
               l--;
            }
        }
        if ( l <= 1 ) {
           return G; // since no threads are activated
        }

        Pair<C> pair;
        GenPolynomial<C> pi;
        GenPolynomial<C> pj;
        GenPolynomial<C> S;
        GenPolynomial<C> D;
        GenPolynomial<C> H;
        List<GenPolynomial<C>> bcH;
        //int len = G.size();
        //System.out.println("len = " + len);
        while ( pairlist.hasNext() ) {
              pair = pairlist.removeNext();
              //System.out.println("pair = " + pair);
              if ( pair == null ) continue; 

              pi = pair.pi; 
              pj = pair.pj; 
              if ( false && logger.isDebugEnabled() ) {
                 logger.debug("pi    = " + pi );
                 logger.debug("pj    = " + pj );
              }

              // S-polynomial -----------------------
              if ( true ) {
              //if ( pair.getUseCriterion3() ) { // correct ?
              //if ( pair.getUseCriterion4() ) { // correct ?
                  S = red.SPolynomial( pi, pj );
                  //System.out.println("S_d = " + S);
                  if ( S.isZERO() ) {
                      pair.setZero();
                      continue;
                  }
                  if ( logger.isDebugEnabled() ) {
                      logger.debug("ht(S) = " + S.leadingExpVector() );
                  }

                  H = red.normalform( G, S );
                  if ( H.isZERO() ) {
                      pair.setZero();
                      continue;
                  }
                  //H = H.monic(); // only for boolean closed H
                  if ( logger.isDebugEnabled() ) {
                      logger.debug("ht(H) = " + H.leadingExpVector() );
                  }

                  if ( H.isONE() ) {
                      G.clear(); G.add( H );
                      return G; // since no threads are activated
                  }
                  if ( logger.isDebugEnabled() ) {
                      logger.debug("H = " + H );
                  }
                  if ( !H.isZERO() ) {
                      logger.info("Sred = " + H);
                      //len = G.size();
                      l++;
                      bcH = red.reducedBooleanClosure(G,H);
                      logger.info("#bcH = " + bcH.size());
                      G.addAll( bcH );
                      for ( GenPolynomial<C> h: bcH ) {
                          pairlist.put( h.monic() );
                      }
                      if ( debug ) {
                         if ( !pair.getUseCriterion3() || !pair.getUseCriterion4() ) {
                            logger.info("H != 0 but: " + pair);
                         }
                      }
                  }
              }
        }
        logger.debug("#sequential list = " + G.size());
        G = minimalGB(G);
        //G = red.irreducibleSet(G);
        logger.info("pairlist #put = " + pairlist.putCount() 
                  + " #rem = " + pairlist.remCount()
                    // + " #total = " + pairlist.pairCount()
                   );
        return G;
    }


    /**
     * Minimal ordered Groebner basis.
     * @typeparam C coefficient type.
     * @param Gp a Groebner base.
     * @return a reduced Groebner base of Gp.
     */
    public List<GenPolynomial<C>> 
                minimalGB(List<GenPolynomial<C>> Gp) {  
        if ( Gp == null || Gp.size() <= 1 ) {
            return Gp;
        }
        // remove zero polynomials
        List<GenPolynomial<C>> G
            = new ArrayList<GenPolynomial<C>>( Gp.size() );
        for ( GenPolynomial<C> a : Gp ) { 
            if ( a != null && !a.isZERO() ) { // always true in GB()
               // already positive a = a.abs();
               G.add( a );
            }
        }
        if ( G.size() <= 1 ) {
           //wg monic do not  return G;
        }
        // remove top reducible polynomials
        GenPolynomial<C> a, b;
        List<GenPolynomial<C>> F;
        List<GenPolynomial<C>> bcH;
        F = new ArrayList<GenPolynomial<C>>( G.size() );
        while ( G.size() > 0 ) {
            a = G.remove(0); b = a;
            if ( red.isTopReducible(G,a) || red.isTopReducible(F,a) ) {
               // drop polynomial 
               if ( true || debug ) {
                  List<GenPolynomial<C>> ff;
                  ff = new ArrayList<GenPolynomial<C>>( G );
                  ff.addAll(F);
                  a = red.normalform( ff, a );
                  if ( !a.isZERO() ) {
                     System.out.println("minGB nf(a) != 0 " + a);
                     bcH = red.reducedBooleanClosure(G,a);
                     if ( bcH.size() > 1 ) {
                        System.out.println("minGB bcH size = " + bcH.size());
                        F.add(b); // do not replace
                     } else {
                        System.out.println("minGB add bc(a): a = " + a + ", bc(a) = " + bcH.get(0));
                        F.addAll( bcH );
                     }
                  } else {
                     System.out.println("minGB dropped " + b);
                  }
               }
            } else {
                F.add(a);
            }
        }
        G = F;
        if ( G.size() <= 1 ) {
           // wg monicreturn G;
        }
        // reduce remaining polynomials
        int len = G.size();
        int el = 0;
        while ( el < len ) {
            a = G.remove(0); b = a;
            //System.out.println("doing " + a.length());
            a = red.normalform( G, a );
            //a = red.normalform( F, a );
            bcH = red.reducedBooleanClosure(G,a);
            if ( bcH.size() > 1 ) {
               System.out.println("minGB bcH size = " + bcH.size());
               G.add( b ); // do not reduce
            } else {
               F.addAll( bcH );
            }
            el++;
        }
        // make monic if possible
        F = new ArrayList<GenPolynomial<C>>( G.size() );
        for ( GenPolynomial<C> p : G ) {
            a = p.monic().abs();
            if ( p.length() != a.length() ) {
               System.out.println("minGB #p != #a: a = " + a + ", p = " + p);
               a = p; // dont make monic for now
            }
            F.add( a );
        }
        G = F;

        /* stratify: collect polynomials with equal leading terms */
        ExpVector e, f;
        F = new ArrayList<GenPolynomial<C>>( G.size() );
        for ( int i = 0; i < G.size(); i++ ) {
            a = G.get(i);
            if ( a == null || a.isZERO() ) {
               continue;
            }
            e = a.leadingExpVector();
            for ( int j = i+1; j < G.size(); j++ ) {
                b = G.get(j);
                if ( b == null || b.isZERO() ) {
                   continue;
                }
                f = b.leadingExpVector();
                if ( e.equals(f) ) {
                   System.out.println("minGB e == f: " + a + ", " + b);
                   a = a.sum(b);
                   G.set(j,null);
                }
            }
            F.add( a );
        }
        G = F;

        /* info on boolean algebra element blocks */
        Map<C,List<GenPolynomial<C>>> bd = new TreeMap<C,List<GenPolynomial<C>>>();
        for ( GenPolynomial<C> p : G ) { 
            C cf = p.leadingBaseCoefficient();
            cf = cf.idempotent();
            List<GenPolynomial<C>> block = bd.get( cf );
            if ( block == null ) {
               block = new ArrayList<GenPolynomial<C>>();
            }
            block.add( p ); 
            bd.put( cf, block );
        }
        System.out.println("\nminGB bd:");
        for( C k: bd.keySet() ) {
           System.out.println("\nkey = " + k + ":");
           System.out.println("val = " + bd.get(k));
        }
        System.out.println();

        return G;
    }

}
