package com.company;

import com.company.Exceptions.EquationException;
import com.company.Exceptions.EquationFormatException;
import com.company.Exceptions.TermException;
import com.company.Exceptions.TermFormatException;
import com.company.Tests.TermTest;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        // write your code here
//        News360Calculator calculator = new News360Calculator();

        try {
            Equation eq1 = new Equation("(2z-3y)*(3x-2y)");
            System.out.println(eq1);
        } catch (EquationFormatException e) {
            e.printStackTrace();
        } catch (TermException e) {
            e.printStackTrace();
        } catch (EquationException e) {
            e.printStackTrace();
        }
    }

}
