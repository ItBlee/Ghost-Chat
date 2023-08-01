package com.itblee.utils;

import com.itblee.security.Certificate;

public final class ValidateUtil {

    public ValidateUtil() {
        throw new AssertionError();
    }

    public static boolean isValidUsername(String name) {
        return name.length() <= PropertyUtil.getInt("validate.username.limit")
                && StringUtil.isNotBlank(name);
    }

    public static boolean isValidPassword(String password) {
        return StringUtil.isNotBlank(password);
    }

    public static boolean isValidCertificate(Certificate certificate) {
        return certificate != null
                && certificate.getUid() != null
                && StringUtil.isNotBlank(certificate.getSecretKey());
    }

}
