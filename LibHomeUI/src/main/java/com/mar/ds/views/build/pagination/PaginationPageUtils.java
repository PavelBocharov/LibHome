package com.mar.ds.views.build.pagination;

import lombok.experimental.UtilityClass;

import java.util.Map;

@UtilityClass
public class PaginationPageUtils {

    public static char UPPER_MINUS = '⁻';
    public static char UPPER_NUMB_0 = '⁰';
    public static char UPPER_NUMB_1 = '¹';
    public static char UPPER_NUMB_2 = '²';
    public static char UPPER_NUMB_3 = '³';
    public static char UPPER_NUMB_4 = '⁴';
    public static char UPPER_NUMB_5 = '⁵';
    public static char UPPER_NUMB_6 = '⁶';
    public static char UPPER_NUMB_7 = '⁷';
    public static char UPPER_NUMB_8 = '⁸';
    public static char UPPER_NUMB_9 = '⁹';

    public static Map<Character, Character> intToUpperChar = Map.of(
            '1', UPPER_NUMB_1,
            '2', UPPER_NUMB_2,
            '3', UPPER_NUMB_3,
            '4', UPPER_NUMB_4,
            '5', UPPER_NUMB_5,
            '6', UPPER_NUMB_6,
            '7', UPPER_NUMB_7,
            '8', UPPER_NUMB_8,
            '9', UPPER_NUMB_9,
            '0', UPPER_NUMB_0
    );

    public static String getUpperNumb(Long number) {
        if (number == null) {
            return "";
        }

        StringBuilder upperNumb = new StringBuilder();

        if (number < 0) {
            upperNumb.append(UPPER_MINUS);
        }

        char[] charNumbs = String.valueOf(Math.abs(number)).toCharArray();
        for (char numb : charNumbs) {
            upperNumb.append(intToUpperChar.get(numb));
        }

        return upperNumb.toString();
    }

    public static String getUpperNumbWithBrackets(Long number) {
        if (number == null) {
            return "";
        }
        return getUpperNumb(number);
    }

}
