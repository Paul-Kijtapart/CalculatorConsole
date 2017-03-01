package com.company;


import com.company.Exceptions.EquationFormatException;
import com.company.Exceptions.TermException;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by aor on 2017-02-27.
 */
public class Equation {
    private Map<Set<Variable>, Float> resultMap;


    public Equation(String equation) throws EquationFormatException, TermException {
        if (!Equation.hasValidInput(equation)) {
            throw new EquationFormatException("The given equation contains illegal character.");
        } else if (!Equation.hasBalancedBracket(equation)) {
            throw new EquationFormatException("The given equation has unbalanced brackets.");
        }
        this.resultMap = compute(equation).termSumMap;
    }

    /**
     * Return the CanonicalFrom of the given equation string
     *
     * @param equation
     * @return null if the given equation is not valid
     */
    public Result compute(String equation) throws TermException {
        String[] segments = equation.split("=");
        if (segments.length > 2 || segments.length < 1) {
            return null;
        }
        if (segments.length == 1) {
            return computeHelper(segments[0]);
        }
        String left_side = segments[0];
        String right_side = segments[1];
        Result left_canonical_form = computeHelper(left_side);
        Result right_canonical_form = computeHelper(right_side);
        right_canonical_form.multiplyByConstant(-1);
        left_canonical_form.addTo(right_canonical_form);
        return left_canonical_form;
    }

    private Result computeHelper(String equation) throws TermException {
        char[] chars = equation.toCharArray();
        Term term = null;
        Integer start_cut_index = null, end_cut_index = null;
        Result result = new Result();
        Stack<Result> result_before_open_bracket_stack = new Stack<>();
        Term prev_term = null;
        char prev_sign = 1;
        char sign = '+';

        for (int i = 0, N = chars.length; i < N; i++) {
            char c = chars[i];
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
                if (start_cut_index != null && end_cut_index != null) {
                    term = new Term(equation.substring(start_cut_index, end_cut_index + 1));
                    switch (sign) {
                        case '+':
                            result.addTerm(term);
                            break;
                        case '-':
                            term.multiplyConstant(-1);
                            result.addTerm(term);
                            break;
                        case '*':
                            prev_term.multiplyConstant(prev_sign == '-' ? -1 : 1);
                            result.removeTerm(prev_term);
                            term.multiply(prev_term);
                            result.addTerm(term);
                            break;
                        case '/':
                            prev_term.multiplyConstant(prev_sign == '-' ? -1 : 1);
                            result.removeTerm(prev_term);
                            term.dividedBy(prev_term);
                            result.addTerm(term);
                            break;
                    }
                }
                prev_term = term;
                prev_sign = sign;
                sign = c;
                term = null;
                start_cut_index = end_cut_index = null;
            } else if (isOpenBracket(c)) {
                // Save Current result and Reset result and sign
                result_before_open_bracket_stack.push(result);
                result = new Result();
                sign = '+';
                start_cut_index = end_cut_index = null;
            } else if (isCloseBracket(c)) {
                // Update Result
                Result front_result = result_before_open_bracket_stack.pop();
                char front_sign = front_result.getSign();
                result.operateWith(front_result, front_sign);
                sign = prev_sign = '+';
                term = prev_term = null;
                start_cut_index = end_cut_index = null;
            }
        }

        if (start_cut_index != null && end_cut_index != null) {
            term = new Term(equation.substring(start_cut_index, end_cut_index + 1));
            switch (sign) {
                case '+':
                    result.addTerm(term);
                    break;
                case '-':
                    term.multiplyConstant(-1);
                    result.addTerm(term);
                    break;
                case '*':
                    prev_term.multiplyConstant(prev_sign == '-' ? -1 : 1);
                    result.removeTerm(prev_term);
                    term.multiply(prev_term);
                    result.addTerm(term);
                    break;
                case '/':
                    prev_term.multiplyConstant(prev_sign == '-' ? -1 : 1);
                    result.removeTerm(prev_term);
                    term.dividedBy(prev_term);
                    result.addTerm(term);
                    break;
            }
        }
        return result;
    }

    public static boolean hasValidInput(String s) {
        Pattern invalid_pattern = Pattern.compile("[^0-9a-zA-Z\\^\\+\\-\\*\\/\\.\\s\\(\\)]");
        Matcher matcher = invalid_pattern.matcher(s);
        while (matcher.find()) {
            System.err.println("illegal char found : " + matcher.group());
            return false;
        }
        return true;
    }

    public static boolean hasBalancedBracket(String s) {
        // TODO
        return true;
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

    @Override
    public String toString() {
        return "Equation{" +
                "resultMap=" + resultMap +
                '}';
    }

    class Result {
        // ConstantSum is mapped to EmptyVariablesSet key
        private Map<Set<Variable>, Float> termSumMap;
        private char sign;

        public Result() {
            this.sign = '+';
            this.termSumMap = new HashMap<>();
        }

        public Result(Character sign) {
            this.sign = sign;
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

        public void multiplyByConstant(float num) {
            Set<Set<Variable>> keys = termSumMap.keySet();
            for (Set<Variable> k : keys) {
                float coefficient = termSumMap.get(k);
                termSumMap.put(k, num * coefficient);
            }
        }

        /**
         * Merge the Keys of both results and update corresponding coefficient (In-place)
         * @param resultTarget
         */
        public void addTo(Result resultTarget) {
            Map<Set<Variable>, Float> targetMap = resultTarget.termSumMap;
            for (Map.Entry<Set<Variable>, Float> entry : targetMap.entrySet()) {
                Float coefficient = this.termSumMap.get(entry.getKey());
                if (coefficient == null) {
                    this.termSumMap.put(entry.getKey(), entry.getValue());
                } else {
                    this.termSumMap.put(entry.getKey(), coefficient + entry.getValue());
                }
            }
        }

        public char getSign() {
            return sign;
        }

        public void operateWith(Result front_result, char front_sign) {
            // Update this Result base on front sign
            switch (front_sign) {
                case '+':
                    this.addTo(front_result);
                    break;
                case '-':
                    this.multiplyByConstant(-1);
                    this.addTo(front_result);
                    break;
                case '*':
                    break;
                case '/':
                    break;
                default:
                    System.err.println("Unrecognized sign to operate with Equation Result.");
            }
        }

        @Override
        public String toString() {
            return "Result{" +
                    "sign=" + sign +
                    ", termSumMap=" + termSumMap +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Result result = (Result) o;

            return termSumMap != null ? termSumMap.equals(result.termSumMap) : result.termSumMap == null;

        }

        @Override
        public int hashCode() {
            return termSumMap != null ? termSumMap.hashCode() : 0;
        }
    }
}
