package com.okoedion_lucky;

import java.math.BigInteger;

public class Question4 {

    public static void main(String[] args) {
        // TODO  USER NOTE:   - CHANGE THE "currentInput" VARIABLE BELOW TO ANY NUMBER 'N' where 22≤N≤100 . DEFAULT VALUE IS 25
        int currentInput = 25;
        String factorial = computeFactorial(currentInput);
        System.out.println(factorial);
    }

    static String computeFactorial(int value) {
        BigInteger factorial = new BigInteger(String.valueOf(value));
        if(factorial.intValue() == 1) {
            return "1";
        }
        factorial = factorial.multiply(new BigInteger(computeFactorial(value - 1)));
        return factorial.toString();
    }
}
