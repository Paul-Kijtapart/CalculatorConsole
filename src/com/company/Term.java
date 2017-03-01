package com.company;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by aor on 2017-02-27.
 */
public class Term {
    private Collection<Variable> variables;
    private Float coefficient;


    public Term(String s) {
        this.coefficient = 1f;
        if (isValidInput(s)) {
//            this.variables = toVariables(s);
        }
    }

    public Collection<Variable> toVariables(String s) {
        Collection<Variable> res = new LinkedList<>();
        int precision = 1;
        Character sign = '+';
        Variable v = Variable.getEmptyVariable();
        char[] chars = s.toCharArray();
        Float current_coefficient = 0f;

        for (int i = 0, N = chars.length; i < N; i++) {
            char c = chars[i];
            if (Character.isDigit(c)) {
                if (sign == '^') {
                    v.appendDegree(c);
                    sign = '+';
                } else if (sign == '.') {
                    precision *= 10;
                    current_coefficient = (10 * current_coefficient) + c - '0';
                } else {
                    current_coefficient = (10 * current_coefficient) + c - '0';
                }
            } else if (isTermSymbol(c)) {
                sign = c;
            } else if (Character.isLetter(c)) {
                if (v.getBase() != null) {
                    if (v.getDegree() == 0) {
                        v.setDegree(1);
                    }
                    Variable nv = new Variable(v);
                    res.add(nv);
                    v.setEmpty();
                }
                v.setBase(c);
                if (current_coefficient == 0) {
                    current_coefficient = 1f;
                }
                this.coefficient *= current_coefficient / precision;
                current_coefficient = 0f;
                precision = 1;
            }
        }

        if (v.getBase() != null) {
            if (v.getDegree() == 0) {
                v.setDegree(1);
            }
            if (current_coefficient == 0) {
                current_coefficient = 1f;
            }
            this.coefficient *= current_coefficient / precision;
            Variable nv = new Variable(v);
            res.add(nv);
        }
        return res;
    }

    /* Check whether the given string s is valid */
    public static boolean isValidInput(String s) {
        Pattern invalid_pattern = Pattern.compile("[^0-9a-zA-Z\\^\\.\\s]");
        Matcher matcher = invalid_pattern.matcher(s);
        while (matcher.find()) {
            return false;
        }
        return true;
    }

    private static boolean isTermSymbol(char c) {
        return (c == '.' || c == '^');
    }

    public Term plus(Term term_2) {
        Term ans = new Term(" ");
        return ans;
    }

    public Term minus(Term term_2) {
        Term ans = new Term(" ");
        return ans;
    }

    public Term multiply(Term term_2) {
        Term ans = new Term(" ");
        return ans;
    }

    public Term dividedBy(Term term_2) {
        Term ans = new Term(" ");
        return ans;
    }


    @Override
    public String toString() {
        return "Term{ " + Float.toString(this.coefficient) +
                " variables=" + variables +
                '}';
    }




}
