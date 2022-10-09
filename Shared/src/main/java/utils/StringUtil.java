package utils;

import org.davidmoten.text.utils.WordWrap;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Xử lý chuỗi
 */
public class StringUtil {

    public static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty() || s.isBlank()|| s.equalsIgnoreCase("null");
    }

    public static String wrapText(String text){
        return WordWrap.from(text).maxWidth(40).insertHyphens(true).wrap();
    }

    public static String applyWrapForGUI(String text) {
        return "<html>" + wrapText(text).replace("\n","<br/>") + "</html>";
    }

    /**
     * Copy chuỗi
     * @param textToCopy chuỗi muốn copy
     */
    public static void copyToClipboard(String textToCopy) {
        StringSelection stringSelection = new StringSelection(textToCopy);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

    /**
     * Chuyển Key sang String
     */
    public static String getStringFromKey(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    /**
     * Chuyển Byte[] sang String
     */
    public static String getStringFromBytes(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * Chuyển String sang Byte[]
     */
    public static byte[] getBytesFromString(String str) {
        return Base64.getDecoder().decode(str);
    }

    /**
     * Hàm băm chuỗi
     */
    public static String applySha256(String str, String strSalt) {
        String hashString;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(strSalt.getBytes());
            byte[] bytes = md.digest(str.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes)
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            hashString = sb.toString();
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
        return hashString;
    }
}
