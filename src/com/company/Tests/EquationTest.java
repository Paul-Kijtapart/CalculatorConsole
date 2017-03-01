package com.company.Tests;

import com.company.Equation;
import com.company.Exceptions.EquationFormatException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by aor on 2017-03-01.
 */
public class EquationTest {
    Equation e1;

    @Before
    public void setUp() throws Exception {
        e1 = new Equation("3.5xy + ( yx - xy ) - 2.12y");
    }

    @After
    public void tearDown() throws Exception {
        e1 = null;
    }

    @Test
    public void testHasValidInput() throws Exception {
        Assert.assertTrue(Equation.hasValidInput("3.5xy - 2x + 3 - 2"));
        Assert.assertTrue(Equation.hasValidInput("3.5xy + ( yx - xy ) - 2.12y"));
        Assert.assertTrue(Equation.hasValidInput(""));
        Assert.assertTrue(Equation.hasValidInput(""));

        // TODO: check throw exception
    }

    @Test
    public void testHasBalancedBracket() throws Exception {

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
    public void testCompute() throws Exception {

    }
}