package com.company;


import java.util.Collection;
import java.util.Map;
import java.util.Stack;

/**
 * Created by aor on 2017-02-27.
 */
public class Equation {
    private Map<Variable, Integer> canonicalForm;

    public Equation(String equation) {
        this.canonicalForm = this.getCanonicalForm(equation);
    }

    /**
     * Return the CanonicalFrom of the given equation string
     *
     * @param equation
     * @return null if the given equation is not valid
     */
    public Map<Variable, Integer> getCanonicalForm(String equation) {
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
        Map<Variable, Integer> left_canonical_form = getCanonicalFormHelper(left_side);
        Map<Variable, Integer> right_canonical_form = getCanonicalFormHelper(right_side);
        left_canonical_form.putAll(right_canonical_form);
        return left_canonical_form;
    }

    private Map<Variable, Integer> getCanonicalFormHelper(String equation) {
        char[] chars = equation.toCharArray();
        Term term = null;
        Integer start_cut_index = null, end_cut_index = null;

        for (int i = 0, N = chars.length; i < N; i++) {

            char c = chars[i];
            Stack<Result> result_before_open_bracket_stack = new Stack<>();

            if (Character.isDigit(c) ||
                    Equation.isPowerSymbol(c) || Equation.isDotSymbol(c) ||
                    Character.isLetter(c)) {
                if (start_cut_index == null) {
                    start_cut_index = i;
                    end_cut_index = start_cut_index;
                } else {
                    end_cut_index = i;
                }
            } else if (Equation.isMathOperator(c)) {
//                term = new Term(equation.substring(start_cut_index, end_cut_index + 1));
                System.out.println(term.toString());
            } else if (Equation.isOpenBracket(c)) {

            } else if (Equation.isCloseBracket(c)) {

            }


        }

        return null;
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
        private char sign;
        private Map<Term, Integer> term_sum;
        private float constant_sum;

        public Result(float constant_sum, Map<Term, Integer> term_sum, char sign) {
            this.constant_sum = constant_sum;
            this.term_sum = term_sum;
            this.sign = sign;
        }
    }
}
