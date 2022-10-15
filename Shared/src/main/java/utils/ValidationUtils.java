package utils;

import object.Certificate;

public class ValidationUtils {

    public static boolean isValidCertificate(Certificate certificate) {
        if (certificate == null)
            return false;
        if (!certificate.isAuthenticated())
            return false;
        if (StringUtil.isNullOrEmpty(certificate.getSecretKey().getKey()))
            return false;
        if (certificate.getUid() == null || StringUtil.isNullOrEmpty(certificate.getUsername()))
            return false;
        return true;
    }

    public static boolean isValidUsername(String username) {
        return true;
    }
    public static boolean isValidPassword(String password) {
        return true;
    }

}
