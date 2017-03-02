package com.company;

import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        if (args.length == 0) {
            // No Argument: start Interactive Mode
            new InteractiveMode();
        }

        // Given lists of fileName
        new FileMode(args);
    }

}
