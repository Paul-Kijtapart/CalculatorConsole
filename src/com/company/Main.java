package com.company;

import com.company.Exceptions.EquationFormatException;
import com.company.Exceptions.TermException;
import com.company.Exceptions.TermFormatException;
import com.company.Tests.TermTest;

import java.io.Console;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        // write your code here
//        // TODO: Select Mode based on User input
//        Console console = System.console();
//        if (console == null) {
//            System.err.println("No console.");
//            System.exit(1);
//        }
//
//        System.out.println("Please select between two modes");
//        System.out.println("Type 1 : Interactive Mode");
//        System.out.println("Type 2 : File Mode");
//
//        while (true) {
//            String line = null;
//            line = console.readLine("Enter Your Mode: ");
//            // TODO check input mode
//
//            // TODO launch corresponding mode
//        }


//        try {
//            Equation e1 = new Equation("3.5xy + ( yx - xy ) - 2.12y");
////            Equation e2 = new Equation("3.5x + x");
////            System.out.println(e2);
//            System.out.println(e1.toString());
//        } catch (EquationFormatException e) {
//            e.printStackTrace();
//        } catch (TermException e) {
//            e.printStackTrace();
//        }


        Float f1 = 0.0f;
        Float f2 = 0f;
        Float f3 = -0.0f;
        Float f4 = -0f;
        System.out.println(f1.equals(f2));
        System.out.println(f2.equals(f4+0f));
        System.out.println(f1.equals(f3+0f));
        System.out.println(f1.equals(f4+0f));

//        System.out.println(f1.compareTo(f2) == 0);
//        System.out.println(f1.compareTo(f3 + 0f) == 0);
//        System.out.println(f1.compareTo(f4 + 0f) == 0);
    }
}
