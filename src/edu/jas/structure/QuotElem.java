/*
 * $Id$
 */

package edu.jas.structure;


/**
 * Quotient element interface. 
 * Defines selectors for numerator and denominator.
 * @param C base element type
 * @author Heinz Kredel
 */
public interface QuotElem<C extends RingElem<C>> {


    /**
     * Numerator.
     */
    public C numerator();


    /**
     * Denominator.
     */
    public C denominator();

}
