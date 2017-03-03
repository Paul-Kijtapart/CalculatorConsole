package com.company;


import com.company.Exceptions.EquationFormatException;
import com.company.Exceptions.TermException;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by aor on 2017-02-27.
 */
public class Equation {
    private Map<Set<Variable>, Float> resultMap;

    // Constants
    public static final String OPEN_BRACKET = "([{";
    public static final String CLOSED_BRACKET = ")]}";
    public static final String ALLOWED_MATH_OPERATOR = "+-/*";
    public static final Map<Character, Character> MATCHED_BRACKETS;

    static {
        MATCHED_BRACKETS = new HashMap<>();
        MATCHED_BRACKETS.put(')', '(');
        MATCHED_BRACKETS.put('}', '{');
        MATCHED_BRACKETS.put(']', '[');
    }


    public Equation(String equation) throws EquationFormatException, TermException {
        if (!Equation.hasValidInput(equation)) {
            throw new EquationFormatException("The given equation contains illegal character.");
        } else if (!Equation.hasBalancedBracket(equation)) {
            throw new EquationFormatException("The given equation has unbalanced brackets.");
        }
        this.resultMap = compute(equation).termSumMap;
        this.plusConstantToAllCoefficients(0f);
        removeFromMapWithValue(this.resultMap, 0f);
    }

    /* Remove all Entry whose value equals to 0f */
    public void removeFromMapWithValue(Map<Set<Variable>, Float> map, Float value) {
        for (Iterator<Map.Entry<Set<Variable>, Float>> it = map.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Set<Variable>, Float> entry = it.next();
            if (entry.getValue().equals(value)) {
                it.remove();
            }
        }
    }


    public Result compute(String equation) throws TermException, EquationFormatException {
        String[] segments = equation.split("=");
        if (segments.length > 2 || segments.length < 1) {
            throw new EquationFormatException("There can be at most one equal sign.");
        }
        if (segments.length == 1) {
            return computeHelper2(segments[0]);
        }
        String left_side = segments[0];
        String right_side = segments[1];
        Result left_canonical_form = computeHelper2(left_side);
        Result right_canonical_form = computeHelper2(right_side);
        right_canonical_form.multiplyByConstant(-1);
        left_canonical_form.plusTo(right_canonical_form);
        return left_canonical_form;
    }

    private Result computeHelper2(String equation) throws TermException {
        char[] chars = equation.toCharArray();
        Result result = new Result();
        Character sign = '+';
        Integer start_cut_index = null, end_cut_index = null;
        Stack<Result> result_before_open_bracket_stack = new Stack<>();
        Result prev_result = new Result();
        char prev_sign = '+';

        for (int i = 0, N = chars.length; i < N; i++) {
            char c = chars[i];
            if (Character.isDigit(c) ||
                    Equation.isPowerSymbol(c) || Equation.isDotSymbol(c) ||
                    Character.isLetter(c) || (Equation.isMathOperator(c) && i - 1 >= 0 && Equation.isPowerSymbol(chars[i - 1]))) {
                if (start_cut_index == null) {
                    start_cut_index = i;
                    end_cut_index = start_cut_index;
                } else {
                    end_cut_index = i;
                }
            } else if (Equation.isMathOperator(c)) {
                if (start_cut_index == null || end_cut_index == null) {
                    sign = c;
                    continue;
                }
                // Apply TempResult to Result
                applyTempResult(start_cut_index, end_cut_index, equation,
                        sign, prev_result, prev_sign, result);

                // Update and Reset pointers
                prev_sign = sign;
                sign = c;
                start_cut_index = end_cut_index = null;
            } else if (isOpenBracket(c)) {
                // Save Current result and the sign before this open bracket
                result.sign = sign;
                result_before_open_bracket_stack.push(result);

                // Update and Reset pointers
                prev_result = new Result();
                prev_sign = '+';
                result = new Result();
                sign = '+';
                start_cut_index = end_cut_index = null;
            } else if (isCloseBracket(c)) {
                // Apply TempResult to final Result
                if (start_cut_index != null && end_cut_index != null) {
                    applyTempResult(start_cut_index, end_cut_index, equation,
                            sign, prev_result, prev_sign, result);
                }

                // Apply Result before open brackets to final result
                Result front_result = result_before_open_bracket_stack.pop();
                char front_sign = front_result.sign;

                // prev_result is now set to () right after FR * (), where * = front_sign
                // and FR is front_result
                prev_result = new Result(result);
                prev_sign = front_sign;

                result.operateWithResultAtOpenBracket(front_result, front_sign);

                // Update and Reset
                sign = '+';
                start_cut_index = end_cut_index = null;
            }
        }

        if (start_cut_index != null && end_cut_index != null) {
            applyTempResult(start_cut_index, end_cut_index, equation,
                    sign, prev_result, prev_sign, result);
        }
        return result;
    }


    /* Looking at one sign and one result at a time:
    * Eg: 3.5xy-2y, this function would trigger when index is at 5 when char c == '-'
    * we would be looking at 3.5xy with sign = '+'
    * if sign is '+', we are able to add this result(3.5xy) to the total result
    * */
    public void applyTempResult(int start_cut_index, int end_cut_index, String equation,
                                char sign, Result prev_result, Character prev_sign, Result result) throws TermException {
        // Get TempResult based on current cut-term
        Result tempResult = new Result(new Term(equation.substring(start_cut_index, end_cut_index + 1)));
        Result product = null;
        switch (sign) {
            case '-':
                prev_result.setResult(new Result(tempResult));
                tempResult.multiplyByConstant(-1);
                result.plusTo(tempResult);
                break;
            case '+':
                prev_result.setResult(tempResult);
                result.plusTo(tempResult);
                break;
            case '*':
                // Take PR out of R
                removePreviouslyMergedResult(prev_result, prev_sign, result);

                // Apply new product to R
                product = prev_result.multiply(tempResult);
                result.operateWith(product, prev_sign);

                // No Change to prev_sign
                prev_result.setResult(new Result(result));
                break;
            case '/':
                // Take PR out of R
                removePreviouslyMergedResult(prev_result, prev_sign, result);
                // Apply new product to R
                product = prev_result.divideBy(tempResult);
                result.operateWith(product, prev_sign);

                // No Change to prev_sign
                prev_result.setResult(new Result(result));
                break;
        }
    }

    /*  Negate the effect of prev_result from Result.
        Eg: initially we have result - prev_result * ..., where '-' is prev_sign
        to negate the effect of prev_result: result = result + prev_result
        Eg2: result * prev_result * ... can be negated by result = result / prev_result
    * */

    private void removePreviouslyMergedResult(Result prev_result, char prev_sign, Result result) throws TermException {
        Result result_without_pr = null;
        switch (prev_sign) {
            case '+':
                prev_result.multiplyByConstant(-1);
                result.plusTo(prev_result);
                prev_result.multiplyByConstant(-1);
                break;
            case '-':
                result.plusTo(prev_result);
                break;
            case '*':
                result_without_pr = result.divideBy(prev_result);
                result.setResult(result_without_pr);
                break;
            case '/':
                result_without_pr = result.multiply(prev_result);
                result.setResult(result_without_pr);
                break;
        }
    }

    /* Check whether the given input contains Not-Allowed symbols or characters */
    public static boolean hasValidInput(String s) {
        Pattern invalid_pattern = Pattern.compile("[^0-9a-zA-Z\\^\\+\\-\\*\\/\\.\\s\\(\\)\\=]");
        Matcher matcher = invalid_pattern.matcher(s);
        while (matcher.find()) {
            System.err.println("illegal char found : " + matcher.group());
            return false;
        }
        return true;
    }

    /* Check whether the given input has balanced matching brackets */
    public static boolean hasBalancedBracket(String s) {
        Stack<Character> openBracketStack = new Stack<>();
        char[] chars = s.toCharArray();
        for (int i = 0, N = chars.length; i < N; i++) {
            char c = chars[i];
            if (isOpenBracket(c)) {
                openBracketStack.push(c);
            } else if (isCloseBracket(c)) {
                if (openBracketStack.isEmpty()) {
                    return false;
                }
                Character matched_open_bracket = MATCHED_BRACKETS.get(c);
                if (matched_open_bracket == null) {
                    return false;
                }
                if (matched_open_bracket != openBracketStack.pop()) {
                    return false;
                }
            }
        }
        if (!openBracketStack.isEmpty()) {
            // Having more open brackets than close brackets
            return false;
        }
        return true;
    }

    /* Plus given num to the coefficient of all keys in resultMap*/
    public void plusConstantToAllCoefficients(Float num) {
        Set<Set<Variable>> keys = this.resultMap.keySet();
        for (Set<Variable> k : keys) {
            Float coefficient = resultMap.get(k);
            resultMap.put(k, coefficient + num);
        }
    }


    private static boolean isCloseBracket(char c) {
        return CLOSED_BRACKET.indexOf(c) > -1;
    }

    private static boolean isOpenBracket(char c) {
        return OPEN_BRACKET.indexOf(c) > -1;
    }

    private static boolean isMathOperator(char c) {
        return ALLOWED_MATH_OPERATOR.indexOf(c) > -1;
    }

    public static boolean isPowerSymbol(char c) {
        return ('^' == c);
    }

    public static boolean isDotSymbol(char c) {
        return ('.' == c);
    }

    public Map<Set<Variable>, Float> getResultMap() {
        return resultMap;
    }

    public String toCanonicalString() {
        StringBuilder res = new StringBuilder();
        int i = 0;
        DecimalFormat df = new DecimalFormat("#.####");
        df.setRoundingMode(RoundingMode.CEILING);
        for (Map.Entry<Set<Variable>, Float> entry : this.resultMap.entrySet()) {
            if (i != 0) {
                if (entry.getValue() > 0) {
                    res.append('+');
                }
            }
            Set<Variable> vars = entry.getKey();
            if (entry.getValue() == -1f &&
                    !vars.isEmpty()) {
                res.append('-');
            } else if (!entry.getValue().equals(1f)) {
                // Control the precision to be printed

                res.append(df.format(entry.getValue()));
            }

            for (Variable v : vars) {
                res.append(v.toString());
            }
            i += 1;
        }
        if (resultMap.isEmpty()) {
            res.append('0');
        }
        res.append(" = 0");
        return res.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Equation equation = (Equation) o;

        return resultMap != null ? resultMap.equals(equation.resultMap) : equation.resultMap == null;
    }

    @Override
    public int hashCode() {
        return resultMap != null ? resultMap.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Equation{" + resultMap + '}';
    }

    class Result {
        private Map<Set<Variable>, Float> termSumMap; // ConstantSum is mapped to EmptyVariablesSet key
        private char sign;

        public Result() {
            this.sign = '+';
            this.termSumMap = new HashMap<>();
            Set<Variable> constantKey = new HashSet<>();
            termSumMap.put(constantKey, 0f);
        }

        /* Return a Deep copy of given result */
        public Result(Result result) {
            this.termSumMap = new HashMap<>(result.termSumMap);
            this.sign = result.sign;
        }

        /* Wrap term */
        public Result(Term term) {
            this.sign = '+';
            this.termSumMap = new HashMap<>();
            termSumMap.put(term.getTermVariables().getVariables(), term.getCoefficient());
        }

        /* Plus the values of this result by the given term */
        public void addTerm(Term term) {
            float coefficient = term.getCoefficient();
            Set<Variable> vars = term.getTermVariables().getVariables();
            addToTermSum(vars, coefficient);
        }

        /* Minus the values of this result by the given term*/
        public void removeTerm(Term term) {
            float coefficient = term.getCoefficient();
            Set<Variable> vars = term.getTermVariables().getVariables();
            removeFromTermSum(vars, coefficient);
        }

        /* Minus the value of the termSumMap's element whose value matches key vars */
        private void removeFromTermSum(Set<Variable> vars, float coefficient) {
            Float sum_coefficient = termSumMap.get(vars);
            if (sum_coefficient == null) {
                termSumMap.put(vars, -1 * coefficient);
            } else {
                termSumMap.put(vars, sum_coefficient - coefficient);
            }
        }

        /* Add the given key vars and its corresponding value coefficient to termSumMap */
        private void addToTermSum(Set<Variable> vars, float coefficient) {
            Float sum_coefficient = termSumMap.get(vars);
            if (sum_coefficient == null) {
                termSumMap.put(vars, coefficient);
            } else {
                termSumMap.put(vars, coefficient + sum_coefficient);
            }
        }

        /* Multiply the given constant to all values of the termSumMap (In-Place) */
        public void multiplyByConstant(float num) {
            Set<Set<Variable>> keys = termSumMap.keySet();
            for (Set<Variable> k : keys) {
                float coefficient = termSumMap.get(k);
                termSumMap.put(k, num * coefficient);
            }
        }

        /**
         * Merge the Keys of both results and update corresponding coefficient (In-place)
         *
         * @param resultTarget
         */
        public void plusTo(Result resultTarget) {
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

       /* The format : front_result front_sign this
        * Eg: FR * (this), where * = is front_sign and FR is front_result
         * */
        public void operateWithResultAtOpenBracket(Result front_result, char front_sign) throws TermException {
            // Update this Result base on front sign
            switch (front_sign) {
                case '+':
                    this.plusTo(front_result);
                    break;
                case '-':
                    this.multiplyByConstant(-1);
                    this.plusTo(front_result);
                    break;
                case '*':
                    Result product = this.multiply(front_result);
                    this.setResult(product);
                    break;
                case '/':
                    Result division = front_result.divideBy(this);
                    this.setResult(division);
                    break;
                default:
                    System.err.println("Unrecognized sign to operate with Equation Result.");
            }
        }

        // check: probe re-place above: this sign result eg this - result, this * result
        public void operateWith(Result result, char sign) throws TermException {
            switch (sign) {
                case '+':
                    this.plusTo(result);
                    break;
                case '-':
                    result.multiplyByConstant(-1);
                    this.plusTo(result);
                    break;
                case '*':
                    Result product = this.multiply(result);
                    this.setResult(product);
                    break;
                case '/':
                    Result division = this.divideBy(result);
                    this.setResult(division);
                    break;
            }
        }

        /* Set the properties of this Result to point to given result */
        private void setResult(Result result) {
            this.termSumMap = result.termSumMap;
            this.sign = result.sign;
        }

        /* Multiply this result by the result of target and return in new Result (both original results untouched) */
        public Result multiply(Result target) throws TermException {
            Result res = new Result();
            Map<Set<Variable>, Float> baseMap = this.termSumMap;
            Map<Set<Variable>, Float> targetMap = target.termSumMap;
            for (Map.Entry<Set<Variable>, Float> base_entry : baseMap.entrySet()) {
                Term base_term = new Term(base_entry.getValue(), base_entry.getKey());
                for (Map.Entry<Set<Variable>, Float> target_entry : targetMap.entrySet()) {
                    Term target_term = new Term(target_entry.getValue(), target_entry.getKey());
                    Term product_term = base_term.multiply(target_term);
                    res.addTerm(product_term);
                }
            }
            return res;
        }

        /* Multiply this result by given term and return the computed value in new Result (Both original untouched)*/
        public Result multiplyTerm(Term term) throws TermException {
            Result res = new Result();
            for (Map.Entry<Set<Variable>, Float> entry : this.termSumMap.entrySet()) {
                Term base_term = new Term(entry.getValue(), entry.getKey());
                Term product_term = base_term.multiply(term);
                res.addTerm(product_term);
            }
            return res;
        }

        /* Divide this result by given term and return the computed value in new Result (Both original untouched)*/
        public Result divideByTerm(Term term) throws TermException {
            Result res = new Result();
            for (Map.Entry<Set<Variable>, Float> entry : this.termSumMap.entrySet()) {
                Term base_term = new Term(entry.getValue(), entry.getKey());
                Term product_term = base_term.dividedBy(term);
                res.addTerm(product_term);
            }
            return res;
        }

        /* Divide this result by the result of target and return the computed value new Result (both original results untouched) */
        public Result divideBy(Result target) throws TermException {
            Result res = new Result();
            Map<Set<Variable>, Float> baseMap = this.termSumMap;
            Map<Set<Variable>, Float> targetMap = target.termSumMap;
            for (Map.Entry<Set<Variable>, Float> base_entry : baseMap.entrySet()) {
                Term base_term = new Term(base_entry.getValue(), base_entry.getKey());
                for (Map.Entry<Set<Variable>, Float> target_entry : targetMap.entrySet()) {
                    Term target_term = new Term(target_entry.getValue(), target_entry.getKey());
                    Term product_term = base_term.dividedBy(target_term);
                    res.addTerm(product_term);
                }
            }
            return res;
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
