package com.company;


import com.company.Exceptions.TermException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * Created by aor on 2017-02-27.
 */
public class Equation {
    private Map<Variable, Integer> canonicalForm;

    public Equation(String equation) {
    }

    /**
     * Return the CanonicalFrom of the given equation string
     *
     * @param equation
     * @return null if the given equation is not valid
     */
    public Result getCanonicalForm(String equation) throws TermException {
        // TODO: Check if equation contains any [ ^a-zA-Z\\^\\+\\-\\*\\/ ]

        // TODO: Check if the input brackets are balanced

        // TODO: Make sure the equation string is valid by this point

        // TODO: Compute Canonical Term
        String[] segments = equation.split("=");
        if (segments.length > 2 || segments.length < 1) {
            return null;
        }

        if (segments.length == 1) {
            return getCanonicalFormHelper(segments[0]);
        }

        String left_side = segments[0];
        String right_side = segments[1];
        Result left_canonical_form = getCanonicalFormHelper(left_side);
        Result right_canonical_form = getCanonicalFormHelper(right_side);
        return left_canonical_form;
    }

    private Result getCanonicalFormHelper(String equation) throws TermException {
        char[] chars = equation.toCharArray();
        Term term = null;
        Integer start_cut_index = null, end_cut_index = null;
        Result result = new Result();
        Stack<Result> result_before_open_bracket_stack = new Stack<>();
        Term prev_term = null;
        int prev_sign = 1;
        char sign = '+';
        boolean isConstant = true;
        float number = 0f;

        for (int i = 0, N = chars.length; i < N; i++) {
            char c = chars[i];

            if (Character.isDigit(c) ||
                    Equation.isPowerSymbol(c) || Equation.isDotSymbol(c) ||
                    Character.isLetter(c)) {
                if (Character.isLetter(c)) {
                    isConstant = false;
                }
                if (start_cut_index == null) {
                    start_cut_index = i;
                    end_cut_index = start_cut_index;
                } else {
                    end_cut_index = i;
                }
            } else if (Equation.isMathOperator(c)) {
                term = new Term(equation.substring(start_cut_index, end_cut_index + 1));
                switch (c) {
                    case '+':
                        result.addTerm(term);
                        break;
                    case '-':
                        term.multiplyConstant(-1);
                        result.addTerm(term);
                        break;
                    case '*':
                        prev_term.multiplyConstant(prev_sign);
                        result.removeTerm(prev_term);
                        term.multiply(prev_term);
                        result.addTerm(term);
                        break;
                    case '/':
                        prev_term.multiplyConstant(prev_sign);
                        result.removeTerm(prev_term);
                        term.dividedBy(prev_term);
                        result.addTerm(term);
                        break;
                }
                prev_term = term;
                prev_sign = sign;
                isConstant = true;
                sign = c;
                term = null;
            } else if (Equation.isOpenBracket(c)) {

            } else if (Equation.isCloseBracket(c)) {

            }


        }

        return null;
    }

//    private void handleTermHelper(Term term, String equation, int start_cut_index, int end_cut_index,
//                                  char c, Result result, Term prev_term, int prev_sign)
//            throws TermException {
//
//        }
//    }
//
//    private void handleConstantHelper(float number, Result result) {
//
//    }

    public Float appendDigit(float number, char digit) {
        if (!Character.isDigit(digit)) {
            return null;
        }
        return (10 * number) + digit - '0';
    }

    private static boolean isCloseBracket(char c) {
        return (c == ')');
    }

    private static boolean isOpenBracket(char c) {
        return (c == '(');
    }

    private static boolean isMathOperator(char c) {
        return (c == '+' || c == '-' || c == '*' || c == '/');
    }

    public static boolean isPowerSymbol(char c) {
        return ('^' == c);
    }

    public static boolean isDotSymbol(char c) {
        return ('.' == c);
    }

    public static boolean hasBalancedBrackets(String s) {


        return false;
    }

    class Result {
        private Map<Set<Variable>, Float> termSumMap;
        private float constant_sum;
        private char sign;

        public Result() {
            this.sign = '+';
            this.termSumMap = new HashMap<>();
        }

        public void addTerm(Term term) {
            float coefficient = term.getCoefficient();
            Set<Variable> vars = term.getVariablesSet();
            addToTermSum(vars, coefficient);
        }

        public void removeTerm(Term term) {
            float coefficient = term.getCoefficient();
            Set<Variable> vars = term.getVariablesSet();
            removeFromTermSum(vars, coefficient);
        }

        private void removeFromTermSum(Set<Variable> vars, float coefficient) {
            Float sum_coefficient = termSumMap.get(vars);
            if (sum_coefficient == null) {
                termSumMap.put(vars, -coefficient);
            } else {
                termSumMap.put(vars, sum_coefficient - coefficient);
            }
        }

        private void addToTermSum(Set<Variable> vars, float coefficient) {
            Float sum_coefficient = termSumMap.get(vars);
            if (sum_coefficient == null) {
                termSumMap.put(vars, coefficient);
            } else {
                termSumMap.put(vars, coefficient + sum_coefficient);
            }
        }
    }
}
