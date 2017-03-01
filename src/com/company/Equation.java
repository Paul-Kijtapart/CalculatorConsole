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
            return getCanonicalFormHelper(segments[0]);
        }
        String left_side = segments[0];
        String right_side = segments[1];
        Result left_canonical_form = getCanonicalFormHelper(left_side);
        Result right_canonical_form = getCanonicalFormHelper(right_side);
        right_canonical_form.multiplyByConstant(-1);
        left_canonical_form.addTo(right_canonical_form);
        return left_canonical_form;
    }

    private Result getCanonicalFormHelper(String equation) throws TermException {
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
                    switch (c) {
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
            }
        }
        return result;
    }

    public static boolean hasValidInput(String s) {
        Pattern invalid_pattern = Pattern.compile("[^0-9a-zA-Z\\^\\+\\-\\*\\/\\.]");
        Matcher matcher = invalid_pattern.matcher(s);
        while (matcher.find()) {
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

    public static boolean hasBalancedBrackets(String s) {


        return false;
    }

    class Result {
        // ConstantSum is mapped to EmptyVariablesSet key
        private Map<Set<Variable>, Float> termSumMap;
        private char afterResultSign;

        public Result() {
            this.afterResultSign = '+';
            this.termSumMap = new HashMap<>();
        }

        public Result(Character afterResultSign) {
            this.afterResultSign = afterResultSign;
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
    }
}
