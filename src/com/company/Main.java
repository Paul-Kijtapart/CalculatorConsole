package com.company;

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


//        Map<Collection<String>, Integer> gg = new HashMap<>();
//        Collection<String> gg_s1 = new ArrayList<>();
//        gg_s1.add("hi");
//        gg_s1.add("ok");
//        gg.put(gg_s1, 1);
//
//
//        Map<Collection<String>, Integer> gg2 = new HashMap<>();
//        Collection<String> no_s1 = new ArrayList<>();
//        no_s1.add(new String("hi"));
//        no_s1.add("ok");
//        gg2.put(no_s1, 1);
//
//        System.out.println(gg.equals(gg2));


//        String s1 = "x^2 + 3.5xy + y y^2 - xy + y";
//        String s2 = "x = 1";
//        String s3 = "x - (y^2 - x) = 0";
//        String s4 = "x - (0 - (0 - x)) = 0";

//        Equation e1 = new Equation(s1);

//        String[] temp = s1.split("=");
//        System.out.println(Arrays.toString(temp));

//        System.out.println(1.5f / 5);
//        System.out.println(4.0f / 3);

//        Term term = new Term("2x3y5z");
//        System.out.println(term.getCoefficient());

//        Term term6 = new Term("hi+_+");
//        System.out.println(term6);

        Map<Set<Variable>, Float> map = new HashMap<>();
        try {
//            Term term = new Term("3.5xy");
//            Term term1 = new Term("3 . 1 5");
            Term constant3 = new Term("2");
            Term term2 = new Term("xy");
            Term term14 = new Term("2xy");

            Term temp = term2.multiply(constant3);

            System.out.println(temp.toString());
            System.out.println(temp.equals(term14));


        } catch (TermException err) {
            err.printStackTrace();
        }



//        Set<Integer> gg = new HashSet<>();
//        Set<Integer> no = new HashSet<>();
//        System.out.println(gg.equals(no));




    }
}
