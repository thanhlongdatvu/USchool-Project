package com.example.datvu.support;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KiemTra {
    public static boolean kiemTraEmail(String email)
    {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }
    public  static  boolean KiemTraPhone(String phone)
    {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[0][[3][5][7][8]]{1}[0-9]{8}$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(phone);
        return matcher.matches();
    }
}
