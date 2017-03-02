package com.company;

import com.company.Exceptions.EquationFormatException;
import com.company.Exceptions.TermException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by aor on 2017-03-01.
 */
public class InteractiveMode {

    public InteractiveMode() {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("InteractiveMode started.");
        try {
            while (true) {
                String line = null;
                System.out.println("Please type a Math Equation or quit if you wish to exit");

                line = in.readLine();
                if (News360Calculator.isExitCommand(line)) {
                    System.out.println("Exiting Interactive Mode.");
                    break;
                }
                Equation eq = null;
                try {
                    eq = new Equation(line);
                } catch (EquationFormatException e) {
                    System.out.println("InteractiveMode : " + e.getMessage());
                    continue;
                } catch (TermException e) {
                    System.out.println("InteractiveMode : " + e.getMessage());
                    continue;
                }

                System.out.println("Canonical From : " + eq.toCanonicalString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
