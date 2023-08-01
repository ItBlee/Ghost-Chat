package com.itblee.utils;

public final class ValidateUtil {

    public ValidateUtil() {
        throw new AssertionError();
    }

    public static boolean isValidUsername(String name) {
        return ObjectUtil.requireNonNull(name)
                .length() <= PropertyUtil.getInt("validate.username.limit");
    }

    public static boolean isValidPassword(String password) {
        return StringUtil.isNotBlank(password) && !password.contains(" ");
    }

}
