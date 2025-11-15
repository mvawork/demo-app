package ru.menshevva.demoapp.utils;

import java.math.BigInteger;
import java.time.LocalDate;

public class StrUtils {

    public static BigInteger strToBigInt(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return new BigInteger(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static String localDateToStr(LocalDate value) {
        return  value == null ? "" : value.toString();
    }
}
