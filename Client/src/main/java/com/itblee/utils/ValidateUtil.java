package com.itblee.utils;

import com.itblee.constant.ClientConstant;
import com.itblee.security.Certificate;

public final class ValidateUtil {

    public ValidateUtil() {
        throw new AssertionError();
    }

    public static boolean isValidUserName(String name) {
        return name.length() <= ClientConstant.NAME_LIMIT && StringUtil.isNotBlank(name);
    }

    public static boolean isValid(Certificate certificate) {
        return certificate != null
                && certificate.getUid() != null
                && StringUtil.isNotBlank(certificate.getSecretKey());
    }

}
