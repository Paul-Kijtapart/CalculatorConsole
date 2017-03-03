package com.company.Tests;

import com.company.Variable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testng.Assert;

/**
 * Created by aor on 2017-02-27.
 */
public class VariableTest {
    Variable variable_1, variable_2,
            variable_3, variable_4,
            variable_5, variable_6,
            variable_7, variable_8;

    @Before
    public void setUp() throws Exception {
        variable_1 = new Variable('y');
        variable_2 = new Variable('x');
        variable_3 = new Variable('y');
        variable_4 = new Variable('z');
        variable_5 = new Variable('x', 13);
        variable_6 = new Variable('y', 13);
        variable_7 = new Variable('x', 13);
    }

    @After
    public void tearDown() throws Exception {
        variable_1 = variable_2 = variable_3 =
                variable_4 = variable_5 = variable_6 =
                                variable_7 = variable_8 = null;
    }

    @org.junit.Test
    public void testEquals() throws Exception {
        Assert.assertFalse(variable_1.equals(variable_2));
        Assert.assertTrue(variable_1.equals(variable_3));
        Assert.assertFalse(variable_1.equals(variable_4));
        Assert.assertFalse(variable_5.equals(variable_6));
        Assert.assertTrue(variable_5.equals(variable_7));
        Assert.assertFalse(variable_5.equals(variable_2));
    }

    @Test
    public void testAppendDegree() throws Exception {
        Assert.assertNull(variable_1.getDegree());
        Assert.assertNull(variable_2.getDegree());
        Assert.assertTrue(variable_5.getDegree() == 13);
        variable_1.appendDegree('1');
        variable_2.appendDegree('c');
        variable_5.appendDegree('1');
        Assert.assertTrue(variable_1.getDegree() == 1);
        Assert.assertNull(variable_2.getDegree(), "expected var2 to have same degree after");
        Assert.assertTrue(variable_5.getDegree() == 131);
    }
}