package utils;

import object.SecretKey;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;

public class SecurityUtil {
    public static final String ALGORITHM = "AES";
    public static final int KEY_BIT_LENGTH = 16; //secretKey phải 16 bit

    /**
     * Tạo secretKey
     */
    public static SecretKey generateKey() throws NoSuchAlgorithmException {
        String secretKey;
        do {
            String keyID = UUID.randomUUID().toString();
            MessageDigest salt = MessageDigest.getInstance("SHA-256");
            salt.update(keyID.getBytes(StandardCharsets.UTF_8));
            secretKey = Long.toHexString(ByteBuffer.wrap(salt.digest()).getLong());
        } while (secretKey.length() != KEY_BIT_LENGTH);
        return new SecretKey(secretKey);
    }

    /**
     * Mã hóa chuỗi bằng thuật toán AES với secret key
     * @param plaintext chuỗi cần mã hóa
     * @param secretKey khóa bí mật
     * @return chuỗi đã bị mã hóa
     */
    public static String encrypt(String plaintext, SecretKey secretKey) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(secretKey.getKey().getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] byteEncrypted = cipher.doFinal(plaintext.getBytes());
        return Base64.getEncoder().encodeToString(byteEncrypted);
    }


    /**
     * Giải mã chuỗi bị mã hóa bằng thuật toán AES với secret key
     * @param ciphertext chuỗi bị mã hóa
     * @param secretKey khóa bí mật
     * @return chuỗi giải mã
     */
    public static String decrypt(String ciphertext, SecretKey secretKey) throws Exception {
        byte[] byteEncrypted = Base64.getDecoder().decode(ciphertext);
        SecretKeySpec skeySpec = new SecretKeySpec(secretKey.getKey().getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] byteDecrypted = cipher.doFinal(byteEncrypted);
        return new String(byteDecrypted);
    }
}
