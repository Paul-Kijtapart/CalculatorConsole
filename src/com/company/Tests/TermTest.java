package com.company.Tests;

import com.company.Term;
import com.sun.source.tree.AssertTree;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testng.Assert;

import static org.junit.Assert.*;

/**
 * Created by aor on 2017-02-28.
 */
public class TermTest {
    Term term1, term2, term3,
            term4, term5, term6,
            term7, term8, term9;

    @Before
    public void setUp() throws Exception {
        term1 = new Term("3.5xy");
        term2 = new Term("xy");
        term3 = new Term("4.5xy");
        term4 = new Term("x^2yz");
        term5 = new Term("2x3y5z");

        // Reverse of term2 and term 5
        term6 = new Term("yx");
        term7 = new Term("5z2x3y");
    }

    @After
    public void tearDown() throws Exception {
        term1 = term2 = term3 =
                term4 = term5 = term6 =
                        term7 = term8 = term9 = null;
    }

    @Test
    public void testIsValidInput() throws Exception {
        Assert.assertTrue(Term.isValidInput("3.5xy"));
        Assert.assertTrue(Term.isValidInput("x^2"));
        Assert.assertTrue(Term.isValidInput("x^2yz"));
        Assert.assertTrue(Term.isValidInput("4.5x^23yz"));
        Assert.assertFalse(Term.isValidInput("-3.5x"));
        Assert.assertFalse(Term.isValidInput("x^(23)"));
        Assert.assertFalse(Term.isValidInput("x*y*z"));
    }

        @Test
    public void testToVariables() throws Exception {
        Assert.assertTrue(term2.equals(term6));
//        Assert.assertTrue(term5.equals(term7));
    }

//    @Test
//    public void testGetCoefficient() throws Exception {
//        Assert.assertEquals(term1.getCoefficient(), 3.5f);
//        Assert.assertEquals(term2.getCoefficient(), 1f);
//        Assert.assertEquals(term3.getCoefficient(), 4.5f);
//        Assert.assertEquals(term4.getCoefficient(), 1f);
//        Assert.assertEquals(term5.getCoefficient(), 30f);
//    }

}