package com.itblee.utils;

import org.davidmoten.text.utils.WordWrap;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

public final class StringUtil {

    public StringUtil() {
        throw new AssertionError();
    }

    public static boolean isBlank(final CharSequence cs) {
        if (cs == null || cs.length() == 0)
            return true;
        for (int i = 0; i < cs.length(); i++) {
            if (!Character.isWhitespace(cs.charAt(i)))
                return false;
        }
        return true;
    }

    public static boolean isNotBlank(final CharSequence cs) {
        return !isBlank(cs);
    }

    public static String requireNonBlank(final String cs) {
        if (isBlank(cs))
            throw new IllegalArgumentException();
        return cs;
    }

    public static boolean containsBlank(final CharSequence[] arr) {
        if (arr == null || arr.length == 0)
            return true;
        for (CharSequence charSequence : arr) {
            if (isBlank(charSequence))
                return true;
        }
        return false;
    }

    public static boolean containsIgnoreCase(String str, String search)     {
        if(str == null || search == null)
            return false;
        final int length = search.length();
        if (length == 0)
            return true;
        for (int i = str.length() - length; i >= 0; i--) {
            if (str.regionMatches(true, i, search, 0, length))
                return true;
        }
        return false;
    }

    public static String wrapText(String text){
        return WordWrap.from(text).maxWidth(40).insertHyphens(true).wrap();
    }

    public static String applyWrapForGUI(String text) {
        return "<html>" + wrapText(text).replace("\n","<br/>") + "</html>";
    }

    public static void copyToClipboard(String textToCopy) {
        String format = textToCopy.replace("<html>", "")
                .replace("</html>", "")
                .replace("<br/>", " ");
        StringSelection stringSelection = new StringSelection(format);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

}
