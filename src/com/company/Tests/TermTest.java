package com.company.Tests;

import com.company.Term;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testng.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by aor on 2017-02-28.
 */
public class TermTest {
    Term term1, term2, term3,
            term4, term5, term6,
            term7, term8, term9,
            term10, term11, term12,
            term13, term14, term15;

    Term zero_term_1;

    Term constant1, constant2, constant3,
            constant4, constant5, constant6,
            zero_constant_1, zero_constant_2;


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

        // Same Base with Different Degree
        term8 = new Term("x^2y");
        term9 = new Term("x^2y^3");

        // for test Plus
        term10 = new Term("5.5xy");
        term11 = new Term("60xyz");
        term12 = new Term("2xy3zxx");
        term13 = new Term("6yx^3z");

        // Constants multiply with term
        term14 = new Term("2yx");

        // Term with coefficnet = 0
        zero_term_1 = new Term("0xyz");

        // Constants
        constant1 = new Term("1 2 345");
        constant2 = new Term("3. 4 5");
        constant3 = new Term("2");
        constant4 = new Term("6.9");
        constant5 = new Term("12348.45");
        constant6 = new Term("12348.45");
        zero_constant_1 = new Term(" 0000.0 0 0 ");
        zero_constant_2 = new Term("0");
    }

    @After
    public void tearDown() throws Exception {
        term1 = term2 = term3 =
                term4 = term5 = term6 =
                        term7 = term8 = term9 =
                                term10 = term11 = term12 =
                                        term13 = term14 = term15 =
                                                null;
        zero_term_1 = null;

        constant1 = constant2 = constant3 =
                constant4 = constant5 = constant6 =
                        zero_constant_1 = zero_constant_2 = null;
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
        Assert.assertTrue(Term.isValidInput("2xy3zxx"));
        Assert.assertTrue(Term.isValidInput("1 2 345"));
        Assert.assertTrue(Term.isValidInput("3. 4 5"));
        Assert.assertTrue(Term.isValidInput(" 0000.0 0 0 "));
        Assert.assertTrue(Term.isValidInput("0"));
        Assert.assertTrue(Term.isValidInput("0yx"));
    }


    @Test
    public void testEquals() throws Exception {
        Assert.assertFalse(term1.equals(term6), "Same Variable, Different Coefficient");
        Assert.assertTrue(term5.equals(term7), "Input in different order");
        Assert.assertFalse(term1.equals(term2));
        Assert.assertFalse(term1.equals(term8));
        Assert.assertFalse(term8.equals(term9));
        Assert.assertTrue(term12.equals(term13));
        Assert.assertTrue(constant5.equals(constant6));
        Assert.assertEquals(zero_constant_1, zero_constant_2);
        Assert.assertNotEquals(zero_term_1, term2);
    }


    @Test
    public void testGetCoefficient() throws Exception {
        Assert.assertEquals(term1.getCoefficient(), 3.5f);
        Assert.assertEquals(term2.getCoefficient(), 1f);
        Assert.assertEquals(term3.getCoefficient(), 4.5f);
        Assert.assertEquals(term4.getCoefficient(), 1f);
        Assert.assertEquals(term5.getCoefficient(), 30f);
        Assert.assertEquals(term12.getCoefficient(), 6f);
        Assert.assertEquals(constant1.getCoefficient(), 12345f);
        Assert.assertEquals(constant2.getCoefficient(), 3.45f);
        Assert.assertEquals(constant5.getCoefficient(), 12348.45f);
        Assert.assertEquals(zero_constant_1.getCoefficient(), 0f);
        Assert.assertEquals(zero_term_1.getCoefficient(), 0f);
    }


    @Test
    public void testPlus() throws Exception {
        Assert.assertNull(term1.plus(term4), "diff coefficient, dif vars");
        Assert.assertNotNull(term1.plus(term2), "diff coefficient, same vars");
        Assert.assertNull(term2.plus(term11), "same coefficient, different vars");
        Assert.assertEquals(term1.plus(term2), term3);
        Assert.assertEquals(term1.plus(term6), term3);
        Assert.assertEquals(term3.plus(term6), term10);
        Assert.assertEquals(term5.plus(term7), term11);
        Assert.assertEquals(constant1.plus(constant2), constant5, "constant plus");
        Assert.assertNull(constant1.plus(term1));
        Assert.assertNull(zero_constant_1.plus(zero_term_1));
    }

    @Test
    public void testMinus() throws Exception {
        Assert.assertEquals(term3.minus(term2), term1, "test cf: 4.5 -1 = 3.5");
        Assert.assertNotNull(term2.minus(term1), "xy - yx = term with cf = 0");
        Assert.assertEquals(term2.minus(term6).getCoefficient(), 0f, " xy - yx = term with cf = 0");
        Assert.assertEquals(term6.minus(term1).getCoefficient(), -2.5f, "yx - 3.5xy = -2.5xy");
        Assert.assertEquals(term5.minus(term7).getCoefficient(), 0f);
    }

    @Test
    public void testGetBaseToDegreeMap() throws Exception {
        Map<Character, Integer> m1 = new HashMap<>();
        m1.put('x', 2);
        m1.put('y', 1);
        Assert.assertTrue(m1.equals(term8.getBaseToDegreeMap()));
        Assert.assertFalse(m1.equals(term2.getBaseToDegreeMap()));
    }

    @Test
    public void testMultiplyConstant() throws Exception {
        term2.multiplyConstant(2);
        Assert.assertEquals(term2, term14);
        term5.multiplyConstant(2);
        term7.multiplyConstant(2);
        Assert.assertEquals(term5, term11);
        Assert.assertEquals(term7, term11);
    }

    @Test
    public void testMultiply() throws Exception {
        Assert.assertEquals(constant3.multiply(term6), term14);
        Assert.assertEquals(constant3.multiply(term2), term14);
        Assert.assertEquals(constant3.multiply(term5), term11);
        Assert.assertEquals(constant3.multiply(term7), term11);
    }

    @Test
    public void testDivideByConstant() throws Exception {
        term11.divideByConstant(2);
        term14.divideByConstant(2);
        Assert.assertEquals(term11, term5);
        Assert.assertEquals(term11, term7);
        Assert.assertEquals(term14, term2);
    }

    @Test
    public void testDividedBy() throws Exception {
        Assert.assertEquals(term11.dividedBy(constant3), term5);
        Assert.assertEquals(term11.dividedBy(constant3), term7);
        Assert.assertEquals(term14.dividedBy(constant3), term2);
    }
}