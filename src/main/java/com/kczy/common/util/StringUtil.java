package com.kczy.common.util;

/**
 * @author shirui
 */
public class StringUtil {

    public static void printHexString(byte[] bytes) {
        for (int i = 1; i <= bytes.length; i++) {
            System.out.print(Integer.toHexString(0xff & bytes[i - 1]));
            if (i % 32 == 0) {
                System.out.println();
            } else {
                System.out.print(" ");
            }
        }
        System.out.println();
    }
}
