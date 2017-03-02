package com.company.Tests;

import com.company.Equation;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by aor on 2017-03-01.
 */
public class EquationTest {
    Equation eq_actual_1, eq_actual_2, eq_actual_3, eq_actual_4,
            eq_actual_5;

    Equation eq_expected_2, eq_expected_3, eq_expected_4, eq_expected_5;

    @Before
    public void setUp() throws Exception {
        eq_actual_1 = new Equation("3.5xy + ( yx - xy ) - 2.12y");
        eq_actual_2 = new Equation("x^2 + 3.5xy + y = y^2 - xy + y");
        eq_actual_3 = new Equation("x = 1");
        eq_actual_4 = new Equation("x - (y^2 - x) = 0");
        eq_actual_5 = new Equation("x - (0 - (0 - x)) = ");

        eq_expected_2 = new Equation("x^2 - y^2 + 4.5xy = 0");
        eq_expected_3 = new Equation("x - 1 = 0");
        eq_expected_4 = new Equation("2x - y^2 = 0");
        eq_expected_5 = new Equation("0 = 0");
    }

    @After
    public void tearDown() throws Exception {
        eq_actual_1 = eq_actual_2 = eq_actual_3 = eq_actual_4 =
                eq_actual_5 = null;

        eq_expected_2 = eq_expected_3 = eq_expected_4 =
                eq_expected_5 = null;
    }

    @Test
    public void testHasValidInput() throws Exception {
        Assert.assertTrue(Equation.hasValidInput("3.5xy - 2x + 3 - 2"));
        Assert.assertTrue(Equation.hasValidInput("3.5xy + ( yx - xy ) - 2.12y"));
        Assert.assertTrue(Equation.hasValidInput("x = 1"));
        Assert.assertTrue(Equation.hasValidInput("x - (y^2 - x) = 0"));
        Assert.assertTrue(Equation.hasValidInput("x - (0 - (0 - x)) = "));
    }

    @Test
    public void testHasBalancedBracket() throws Exception {
        Assert.assertTrue(Equation.hasBalancedBracket("(2+3)+{7*2}-[3.5xy -y]"));
        Assert.assertTrue(Equation.hasBalancedBracket("({[x^2 * 4^2]})"));
        Assert.assertFalse(Equation.hasBalancedBracket("((50xy -2y)"));
        Assert.assertFalse(Equation.hasBalancedBracket("(2x-5z}"));
    }

    @Test
    public void testIsPowerSymbol() throws Exception {
        Assert.assertTrue(Equation.isPowerSymbol('^'));
        Assert.assertFalse(Equation.isPowerSymbol('.'));
        Assert.assertFalse(Equation.isPowerSymbol('*'));
    }

    @Test
    public void testIsDotSymbol() throws Exception {
        Assert.assertTrue(Equation.isDotSymbol('.'));
        Assert.assertFalse(Equation.isDotSymbol('*'));
        Assert.assertFalse(Equation.isDotSymbol('/'));
    }

    @Test
    public void testEquals() throws Exception {
        Assert.assertEquals(eq_actual_2, eq_expected_2);
        Assert.assertEquals(eq_actual_3, eq_expected_3);
        Assert.assertEquals(eq_actual_4, eq_expected_4);
        Assert.assertEquals(eq_actual_5, eq_expected_5);
    }

    @Test
    public void testRemoveFromMapWithValue() throws Exception {
        Assert.assertEquals(eq_expected_5.getResultMap().size(), 0);
    }


}