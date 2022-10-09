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
        if (certificate.getUid() == null
            || certificate.getSecretKey() == null
            || StringUtil.isNullOrEmpty(certificate.getUsername()))
            return false;
        return true;
    }
}
