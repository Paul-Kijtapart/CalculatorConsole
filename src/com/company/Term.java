package com.company;

import com.company.Exceptions.TermException;
import com.company.Exceptions.TermFormatException;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by aor on 2017-02-27.
 */
public class Term {
    protected Set<Variable> variablesSet;
    protected Float coefficient;
    protected Map<Character, Integer> baseToDegreeMap;


    public Term(String s) throws TermFormatException {
        if (!isValidInput(s)) {
            throw new TermFormatException("The input contains illegal Character.");
        }
        CoefficientAndBaseToDegreeMap coefficientAndBaseToDegreeMap = getBaseToDegreeMap(s);
        this.coefficient = coefficientAndBaseToDegreeMap.getCoefficient();
        this.baseToDegreeMap = coefficientAndBaseToDegreeMap.getBase_to_degree_map();

        this.variablesSet = new HashSet<>();
        for (Map.Entry<Character, Integer> entry : baseToDegreeMap.entrySet()) {
            Variable v = new Variable(entry.getKey(), entry.getValue());
            this.variablesSet.add(v);
        }
    }

    /* Return a Copy of given Term t */
    public Term(Term t) {
        this.variablesSet = new HashSet<>();
        variablesSet.addAll(t.getVariablesSet());
        this.coefficient = new Float(t.getCoefficient());
        this.baseToDegreeMap = new HashMap<>();
        baseToDegreeMap.putAll(t.getBaseToDegreeMap());
    }

    private CoefficientAndBaseToDegreeMap getBaseToDegreeMap(String s) {
        CoefficientAndBaseToDegreeMap res = new CoefficientAndBaseToDegreeMap();
        int precision = 1;
        Character sign = '+';
        Variable v = Variable.getEmptyVariable();
        char[] chars = s.toCharArray();
        Float current_coefficient = 0f;
        boolean foundDigit = false;

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
                    foundDigit = true;
                    current_coefficient = (10 * current_coefficient) + c - '0';
                }
            } else if (isTermSymbol(c)) {
                sign = c;
            } else if (Character.isLetter(c)) {
                if (v.getBase() != null) {
                    // Add Variables to the Term set
                    if (v.getDegree() == 0) {
                        v.setDegree(1);
                    }
                    res.putVariable(v.getBase(), v.getDegree());
                    v.setEmpty();
                }
                v.setBase(c);
                if (current_coefficient == 0 && !foundDigit) {
                    current_coefficient = 1f;
                }
                foundDigit = false;
                res.multiplyCoefficientBy(current_coefficient / precision);
                current_coefficient = 0f;
                precision = 1;
            }
        }
        if (res.hasEmptyBaseToDegreeMap() && foundDigit) {
            // This Term is a Constant
            if (current_coefficient == 0f) {
                res.coefficient = 0f;
            } else {
                res.multiplyCoefficientBy(current_coefficient / precision);
            }
        } else if (v.getBase() != null) {
            // Add Variables to the Term set
            if (v.getDegree() == 0) {
                v.setDegree(1);
            }
            if (current_coefficient == 0) {
                current_coefficient = 1f;
            }
            res.multiplyCoefficientBy(current_coefficient / precision);
            res.putVariable(v.getBase(), v.getDegree());
        }

        return res;
    }

    public Term plus(Term term_2) throws TermException {
        Term res = new Term(this);
        if (!res.getVariablesSet().equals(term_2.getVariablesSet())) {
            return null;
        }
        res.setCoefficient(res.getCoefficient() + term_2.getCoefficient());
        return res;
    }

    public Term minus(Term term_2) throws TermException {
        Term res = new Term(this);
        if (!res.getVariablesSet().equals(term_2.getVariablesSet())) {
            return null;
        }
        res.setCoefficient(res.getCoefficient() - term_2.getCoefficient());
        return res;
    }

    /* Multiply the Coefficient of this Term by num (In-Place) */
    public void multiplyConstant(float num) {
        this.coefficient *= num;
    }

    /* Create a copy of this term and Multiple its Coefficient by num of term_2 and Plus its Degree by term_2's Degree */
    public Term multiply(Term term_2) throws TermException {
        Term res = new Term(this);
        res.setCoefficient(res.getCoefficient() * term_2.getCoefficient());
        Map<Character, Integer> res_map = res.getBaseToDegreeMap();
        Map<Character, Integer> target_map = term_2.getBaseToDegreeMap();
        for (Map.Entry<Character, Integer> entry : target_map.entrySet()) {
            Integer power = res_map.get(entry.getKey());
            if (power == null) {
                res_map.put(entry.getKey(), entry.getValue());
            } else {
                res_map.put(entry.getKey(), power + entry.getValue());
            }
        }
        res.variablesSet.clear();
        loadSetVariablesFromMap(res_map, res.variablesSet);
        return res;
    }

    private void loadSetVariablesFromMap(Map<Character, Integer> baseToDegreeMap, Set<Variable> res) {
        for (Map.Entry<Character, Integer> entry : baseToDegreeMap.entrySet()) {
            Variable v = new Variable(entry.getKey(), entry.getValue());
            res.add(v);
        }
    }

    /* Create a copy of this term and Divide its Coefficient by num of term_2 and Minus its Degree by term_2's Degree */
    public Term dividedBy(Term term_2) throws TermException {
        Term res = new Term(this);
        res.setCoefficient(res.getCoefficient() / term_2.getCoefficient());
        Map<Character, Integer> res_map = res.getBaseToDegreeMap();
        Map<Character, Integer> target_map = term_2.getBaseToDegreeMap();
        for (Map.Entry<Character, Integer> entry : target_map.entrySet()) {
            Integer power = res_map.get(entry.getKey());
            if (power == null) {
                res_map.put(entry.getKey(), entry.getValue());
            } else {
                res_map.put(entry.getKey(), power - entry.getValue());
            }
        }
        res.variablesSet.clear();
        loadSetVariablesFromMap(res_map, res.variablesSet);
        return res;
    }

    /* Divide the Coefficient of this Term by num (In-Place) */
    public void divideByConstant(float num) {
        this.coefficient /= num;
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

    public Map<Character, Integer> getBaseToDegreeMap() {
        return baseToDegreeMap;
    }

    public Float getCoefficient() {
        return this.coefficient;
    }

    public Set<Variable> getVariablesSet() {
        return variablesSet;
    }

    public void setCoefficient(Float coefficient) {
        this.coefficient = coefficient;
    }

    @Override
    public String toString() {
        return "Term{ " + Float.toString(this.coefficient) +
                " " + variablesSet +
                '}';
    }

    /* Store Coefficient and Map of Base to Degree of all variablesSet this Term contains */
    class CoefficientAndBaseToDegreeMap {
        private Float coefficient;
        private Map<Character, Integer> base_to_degree_map;

        public CoefficientAndBaseToDegreeMap() {
            this.coefficient = 1f;
            this.base_to_degree_map = new HashMap<>();
        }

        public void putVariable(Character base, Integer degree) {
            Integer power = this.base_to_degree_map.get(base);
            if (power == null) {
                this.base_to_degree_map.put(base, degree);
            } else {
                this.base_to_degree_map.put(base, power + degree);
            }
        }

        public boolean hasEmptyBaseToDegreeMap() {
            return this.base_to_degree_map.isEmpty();
        }

        public void multiplyCoefficientBy(Float val) {
            this.coefficient *= val;
        }

        public Float getCoefficient() {
            return coefficient;
        }

        public Map<Character, Integer> getBase_to_degree_map() {
            return base_to_degree_map;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Term term = (Term) o;

        if (variablesSet != null ? !variablesSet.equals(term.variablesSet) : term.variablesSet != null) return false;
        return coefficient != null ? coefficient.equals(term.coefficient) : term.coefficient == null;

    }

    @Override
    public int hashCode() {
        int result = variablesSet != null ? variablesSet.hashCode() : 0;
        result = 31 * result + (coefficient != null ? coefficient.hashCode() : 0);
        return result;
    }
}
