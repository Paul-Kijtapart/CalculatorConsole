package com.company;

import com.company.Exceptions.EquationFormatException;
import com.company.Exceptions.TermException;

import java.io.*;

/**
 * Created by aor on 2017-03-01.
 */
public class FileMode {

    public FileMode(String[] fileNames) {
        System.out.println("File Mode started.");
        for (String fn : fileNames) {
            parseFile(fn);
        }
        System.out.println("Exit FileMode");
    }

    private void parseFile(String fileName) {
        System.out.println("Processing " + fileName);
        File file = new File(fileName);
        File outFile = new File(fileName + ".out");
        BufferedReader in = null;
        BufferedWriter out = null;
        try {
            in = new BufferedReader(new FileReader(file));
            out = new BufferedWriter(new FileWriter(outFile));
            String line = null;
            while ((line = in.readLine()) != null) {
                try {
                    Equation eq = new Equation(line);
                    String canonicalString = eq.toCanonicalString();
                    out.append(canonicalString);
                    out.append('\n');
                    out.flush();
                } catch (EquationFormatException e) {
                    e.printStackTrace();
                } catch (TermException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Finish writing to file : " + outFile.toString());
        } catch (FileNotFoundException e) {
            System.out.println("File Mode : " + e.toString());
        } catch (IOException e) {
            System.out.println("File Mode : " + e.toString());
        } finally {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
