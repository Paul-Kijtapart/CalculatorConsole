package com.company;

import com.company.Exceptions.VariableFormatException;

/**
 * Created by aor on 2017-02-27.
 */
public class Variable {
    private Character base;
    private Integer degree;

    // Constant
    public static final Variable EmptyVariable = Variable.getEmptyVariable();

    public Variable(Character base, Integer degree) {
        this.base = base;
        this.degree = degree;
    }

    public Variable(Character base) {
        this.base = base;
        this.degree = null;
    }

    public Variable(Variable v) {
        this.base = v.base;
        this.degree = v.degree;
    }

    public static Variable getEmptyVariable() {
        return new Variable(null, null);
    }

    /* Reset Base, Degree to their default values */
    public void setEmpty() {
        this.setBase(null);
        this.setDegree(null);
    }

    /* Append character digit c to the end of Degree */
    public void appendDegree(Character c) {
        if (!Character.isDigit(c)) {
            System.err.println("Cannot append Non-Digit character to Degree");;
            return;
        }
        if (degree == null) {
            degree = 0;
        }
        this.degree = (10 * this.degree) + c - '0';
    }

    @Override
    public String toString() {
        return String.format("%s ^ %d ", this.base, this.degree);
    }

    public void setBase(Character base) {
        this.base = base;
    }

    public void setDegree(Integer degree) {
        this.degree = degree;
    }

    public Character getBase() {
        return base;
    }

    public Integer getDegree() {
        return degree;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Variable variable = (Variable) o;

        if (base != null ? !base.equals(variable.base) : variable.base != null) return false;
        return degree != null ? degree.equals(variable.degree) : variable.degree == null;

    }

    @Override
    public int hashCode() {
        int result = base != null ? base.hashCode() : 0;
        result = 31 * result + (degree != null ? degree.hashCode() : 0);
        return result;
    }
}
