/*
 * $Id$
 */

package edu.jas.module;

import java.util.List;
//import java.util.Iterator;
import java.util.ArrayList;
import java.io.IOException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;


import edu.jas.structure.RingElem;

import edu.jas.arith.BigRational;

import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolynomialList;
import edu.jas.poly.ExpVector;
import edu.jas.poly.TermOrder;

import edu.jas.ring.Reduction;
import edu.jas.ring.GroebnerBase;

import edu.jas.module.ModuleList;

/**
 * ModGroebnerBase Test using JUnit 
 * @author Heinz Kredel.
 */

public class ModGroebnerBaseTest extends TestCase {

    private static final Logger logger = Logger.getLogger(ModGroebnerBaseTest.class);

/**
 * main
 */
   public static void main (String[] args) {
          BasicConfigurator.configure();
          junit.textui.TestRunner.run( suite() );
   }

/**
 * Constructs a <CODE>ModGroebnerBaseTest</CODE> object.
 * @param name String
 */
   public ModGroebnerBaseTest(String name) {
          super(name);
   }

/**
 */ 
 public static Test suite() {
     TestSuite suite= new TestSuite(ModGroebnerBaseTest.class);
     return suite;
   }

   int port = 4711;
   String host = "localhost";

   GenPolynomialRing<BigRational> fac;

   PolynomialList<BigRational> F;
   List<GenPolynomial<BigRational>> G;

   GenPolynomial<BigRational> a;
   GenPolynomial<BigRational> b;
   GenPolynomial<BigRational> c;
   GenPolynomial<BigRational> d;
   GenPolynomial<BigRational> e;

   String[] vars;
   TermOrder tord;

   List<List<GenPolynomial<BigRational>>> L;
   List<GenPolynomial<BigRational>> V;
   ModuleList<BigRational> M;
   ModuleList<BigRational> N;

   int rl = 3; //4; //3; 
   int kl = 8;
   int ll = 5;
   int el = 2;
   float q = 0.2f; //0.4f

   protected void setUp() {
       BigRational coeff = new BigRational(9);
       vars = ExpVector.STDVARS( rl );
       tord = new TermOrder();
       fac = new GenPolynomialRing<BigRational>(coeff,rl,tord,vars);
       a = b = c = d = e = null;

       a = fac.random(kl, ll, el, q );
       b = fac.random(kl, ll, el, q );
       c = fac.random(kl, ll, el, q );
       d = fac.random(kl, ll, el, q );
       e = d; //fac.random(kl, ll, el, q );
   }

   protected void tearDown() {
       a = b = c = d = e = null;
       fac = null;
       vars = null;
       tord = null;
   }


/**
 * Test sequential GBase
 * 
 */
 public void testSequentialModGB() {

     L = new ArrayList<List<GenPolynomial<BigRational>>>();

     assertTrue("not isZERO( a )", !a.isZERO() );
     V = new ArrayList<GenPolynomial<BigRational>>();
     V.add(a); V.add(fac.getZERO()); V.add(fac.getONE());
     L.add(V);
     M = new ModuleList<BigRational>(fac,L);
     assertTrue("isGB( { (a,0,1) } )", ModGroebnerBase.isGB(M) );

     N = ModGroebnerBase.GB( M );
     assertTrue("isGB( { (a,0,1) } )", ModGroebnerBase.isGB(N) );

     assertTrue("not isZERO( b )", !b.isZERO() );
     V = new ArrayList<GenPolynomial<BigRational>>();
     V.add(b); V.add(fac.getONE()); V.add(fac.getZERO());
     L.add(V);
     M = new ModuleList(fac,L);
     //System.out.println("L = " + L.size() );

     N = ModGroebnerBase.GB( M );
     assertTrue("isDIRPGB( { (a,0,1),(b,1,0) } )", ModGroebnerBase.isGB(N) );
     //System.out.println("N = " + N );

     assertTrue("not isZERO( c )", !c.isZERO() );
     V = new ArrayList<GenPolynomial<BigRational>>();
     V.add(c); V.add(fac.getZERO()); V.add(fac.getZERO());
     L.add(V);
     M = new ModuleList<BigRational>(fac,L);
     //System.out.println("L = " + L.size() );

     N = ModGroebnerBase.GB( M );
     assertTrue("isDIRPGB( { (a,),(b,),(c,) } )", ModGroebnerBase.isGB(N) );
     //System.out.println("N = " + N );

     assertTrue("not isZERO( d )", !d.isZERO() );
     V = new ArrayList<GenPolynomial<BigRational>>();
     V.add(d); V.add(fac.getZERO()); V.add(fac.getZERO());
     L.add(V);
     M = new ModuleList<BigRational>(fac,L);
     //System.out.println("L = " + L.size() );

     N = ModGroebnerBase.GB( M );
     assertTrue("isDIRPGB( { (a,b,c,d) } )", ModGroebnerBase.isGB(N) );
     //System.out.println("N = " + N );

 }

}
