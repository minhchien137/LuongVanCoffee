package com.pro.shopfee.utils;

import java.text.NumberFormat;
import java.util.Locale;

public class StringUtil {

    public static boolean isEmpty(String input) {
        return input == null || input.isEmpty() || ("").equals(input.trim());
    }

    public static boolean isValidEmail(CharSequence target) {
        if (target == null)
            return false;
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static String getDoubleNumber(int number) {
        if (number < 10) {
            return "0" + number;
        } else return "" + number;
    }

    public static String formatPrice(double price) {
        Locale localeVN = new Locale("vi", "VN");
        NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);
        return currencyVN.format(price);
    }
}
