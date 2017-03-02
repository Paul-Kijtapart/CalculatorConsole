package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by aor on 2017-03-01.
 */
public class News360Calculator {
    private BufferedReader in;

    public News360Calculator() {
        System.out.println("There are two modes available.");
        System.out.println("Type 1 : Interactive Mode");
        System.out.println("Type 2 : File Mode");

        try {
            in = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                System.out.println("Please Enter Mode Number: ");
                String line = null;
                line = in.readLine();

                if (News360Calculator.isExitCommand(line)) {
                    System.out.println("Thank you for using News360Calculator.");
                    break;
                }

                if (!validateInput(line)) {
                    System.out.println("Please input only 1 or 2.");
                    continue;
                }

                Integer selectedMode = line.charAt(0) - '0';
                if (selectedMode == null) {
                    System.out.println("Invalid selectedMode: Please input only 1 or 2.");
                    continue;
                }
                startMode(selectedMode);
            }
            in.close();
        } catch (IOException e) {
            System.out.println("News360Calculator : " + e.toString());
        }
    }

    public static boolean isExitCommand(String line) {
        if (line.equals("quit") || line.equals("exit")) {
            return true;
        }
        return false;
    }

    private void startMode(Integer selectedMode) {
        switch (selectedMode) {
            case 1:
               new InteractiveMode();
                break;
            case 2:

                break;
            default:
                System.out.println("Mode number : " + selectedMode + " is not available.");
        }
    }

    private boolean validateInput(String line) {
        if (line.length() != 1 || !Character.isDigit(line.charAt(0))) {
            return false;
        }
        return true;
    }

}
