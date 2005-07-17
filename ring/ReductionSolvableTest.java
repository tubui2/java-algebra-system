/*
 * $Id$
 */

package edu.jas.ring;

import java.util.List;
import java.util.ArrayList;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator;

import edu.jas.arith.BigRational;
import edu.jas.arith.BigComplex;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.poly.GenSolvablePolynomialRing;
import edu.jas.poly.PolynomialList;
import edu.jas.poly.RelationTable;
import edu.jas.poly.WeylRelations;


/**
 * Reduction Test using JUnit 
 * @author Heinz Kredel.
 */

public class ReductionSolvableTest extends TestCase {

/**
 * main
 */
   public static void main (String[] args) {
          BasicConfigurator.configure();
          junit.textui.TestRunner.run( suite() );
   }

/**
 * Constructs a <CODE>ReductionSolvableTest</CODE> object.
 * @param name String
 */
   public ReductionSolvableTest(String name) {
          super(name);
   }

/**
 */ 
 public static Test suite() {
     TestSuite suite= new TestSuite(ReductionSolvableTest.class);
     return suite;
   }

   //private final static int bitlen = 100;

   GenSolvablePolynomialRing<BigRational> fac;

   RelationTable table;

   GenSolvablePolynomial<BigRational> a;
   GenSolvablePolynomial<BigRational> b;
   GenSolvablePolynomial<BigRational> c;
   GenSolvablePolynomial<BigRational> d;
   GenSolvablePolynomial<BigRational> e;

   List<GenSolvablePolynomial<BigRational>> L;
   PolynomialList<BigRational> F;
   PolynomialList<BigRational> G;

   int rl = 4; 
   int kl = 10;
   int ll = 5;
   int el = 3;
   float q = 0.4f;

   protected void setUp() {
       a = b = c = d = e = null;
       fac = new GenSolvablePolynomialRing<BigRational>( new BigRational(0), rl );
   }

   protected void tearDown() {
       a = b = c = d = e = null;
       fac = null;
   }


/**
 * Test constants and empty list reduction
 * 
 */
 public void testRatReduction0() {
     L = new ArrayList<GenSolvablePolynomial<BigRational>>();

     a = fac.random(kl, ll, el, q );
     c = fac.getONE();
     d = fac.getZERO();

     e = Reduction.<BigRational>leftNormalform( L, c );
     assertTrue("isONE( e )", e.isONE() ); 

     e = Reduction.<BigRational>leftNormalform( L, d );
     assertTrue("isZERO( e )", e.isZERO() ); 


     L.add( c );
     e = Reduction.<BigRational>leftNormalform( L, c );
     assertTrue("isZERO( e )", e.isZERO() ); 

     // e = Reduction.leftNormalform( L, a );
     // assertTrue("isZERO( e )", e.isZERO() ); 

     e = Reduction.<BigRational>leftNormalform( L, d );
     assertTrue("isZERO( e )", e.isZERO() ); 


     L = new ArrayList<GenSolvablePolynomial<BigRational>>();
     L.add( d );
     e = Reduction.<BigRational>leftNormalform( L, c );
     assertTrue("isONE( e )", e.isONE() ); 

     e = Reduction.<BigRational>leftNormalform( L, d );
     assertTrue("isZERO( e )", e.isZERO() ); 
 }


/**
 * Test constants and empty list reduction
 * 
 */
 public void testWeylRatReduction0() {
     L = new ArrayList<GenSolvablePolynomial<BigRational>>();

     WeylRelations<BigRational> wl = new WeylRelations<BigRational>(fac);
     wl.generate();

     a = fac.random(kl, ll, el, q );
     c = fac.getONE();
     d = fac.getZERO();

     e = Reduction.<BigRational>leftNormalform( L, c );
     assertTrue("isONE( e )", e.isONE() ); 

     e = Reduction.<BigRational>leftNormalform( L, d );
     assertTrue("isZERO( e )", e.isZERO() ); 


     L.add( c );
     e = Reduction.<BigRational>leftNormalform( L, c );
     assertTrue("isZERO( e )", e.isZERO() ); 

     e = Reduction.<BigRational>leftNormalform( L, a );
     assertTrue("isZERO( e )", e.isZERO() ); 

     e = Reduction.<BigRational>leftNormalform( L, d );
     assertTrue("isZERO( e )", e.isZERO() ); 


     L = new ArrayList<GenSolvablePolynomial<BigRational>>();
     L.add( d );
     e = Reduction.<BigRational>leftNormalform( L, c );
     assertTrue("isONE( e )", e.isONE() ); 

     e = Reduction.<BigRational>leftNormalform( L, d );
     assertTrue("isZERO( e )", e.isZERO() ); 
 }



/**
 * Test ReductionMod with constants and empty list reduction
 * 
 */
/*
 public void testRatReduction1() {
     L = new ArrayList<GenSolvablePolynomial<BigRational>>();

     a = fac.random(kl, ll, el, q );
     c = fac.getONE();
     d = fac.getZERO();

     e = Reduction.<BigRational>leftNormalformMod( L, c );
     assertTrue("isONE( e )", e.isONE() ); 

     e = Reduction.<BigRational>leftNormalformMod( L, d );
     assertTrue("isZERO( e )", e.isZERO() ); 


     L.add( c );
     e = Reduction.<BigRational>leftNormalformMod( L, c );
     assertTrue("isZERO( e )", e.isZERO() ); 

     // e = Reduction.<BigRational>leftNormalform( L, a );
     // assertTrue("isZERO( e )", e.isZERO() ); 

     e = Reduction.<BigRational>leftNormalformMod( L, d );
     assertTrue("isZERO( e )", e.isZERO() ); 


     L = new ArrayList<GenSolvablePolynomial<BigRational>>();
     L.add( d );
     e = Reduction.<BigRational>leftNormalformMod( L, c );
     assertTrue("isONE( e )", e.isONE() ); 

     e = Reduction.<BigRational>leftNormalformMod( L, d );
     assertTrue("isZERO( e )", e.isZERO() ); 
 }
 */


/**
 * Test Rat reduction
 * 
 */
 public void testRatReduction() {

     a = fac.random(kl, ll, el, q );
     b = fac.random(kl, ll, el, q );

     assertTrue("not isZERO( a )", !a.isZERO() );

     L = new ArrayList<GenSolvablePolynomial<BigRational>>();
     L.add(a);

     e = Reduction.<BigRational>leftNormalform( L, a );
     assertTrue("isZERO( e )", e.isZERO() );

     assertTrue("not isZERO( b )", !b.isZERO() );

     L.add(b);
     e = Reduction.<BigRational>leftNormalform( L, a );
     assertTrue("isZERO( e ) some times", e.isZERO() ); 
 }


/**
 * Test Weyl Rat reduction
 * 
 */
 public void testWeylRatReduction() {
     L = new ArrayList<GenSolvablePolynomial<BigRational>>();

     WeylRelations<BigRational> wl = new WeylRelations<BigRational>(fac);
     wl.generate();

     a = fac.random(kl, ll, el, q );
     b = fac.random(kl, ll, el, q );

     assertTrue("not isZERO( a )", !a.isZERO() );

     L.add(a);

     e = Reduction.<BigRational>leftNormalform( L, a );
     assertTrue("isZERO( e )", e.isZERO() );

     assertTrue("not isZERO( b )", !b.isZERO() );

     L.add(b);
     e = Reduction.<BigRational>leftNormalform( L, a );
     assertTrue("isZERO( e ) some times", e.isZERO() ); 
 }


/**
 * Test Rat reduction Mod
 * 
 */
/*
 public void testRatReductionMod() {

     a = fac.random(kl, ll, el, q );
     b = fac.random(kl, ll, el, q );

     assertTrue("not isZERO( a )", !a.isZERO() );

     L = new ArrayList<GenSolvablePolynomial<BigRational>>();
     L.add(a);

     e = Reduction.<BigRational>leftNormalformMod( L, a );
     assertTrue("isZERO( e )", e.isZERO() );

     assertTrue("not isZERO( b )", !b.isZERO() );

     L.add(b);
     e = Reduction.<BigRational>leftNormalformMod( L, a );
     assertTrue("isZERO( e ) some times", e.isZERO() ); 
 }
 */

}
