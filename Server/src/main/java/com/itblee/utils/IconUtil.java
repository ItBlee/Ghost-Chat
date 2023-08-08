package com.itblee.utils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;
import java.util.Base64;

public final class IconUtil {

    static final String[] EXTENSIONS = new String[] {
            "png", "gif", "jpeg", "jpg", "bmp"
    };

    static final Color COLOR_TRANSPARENT = new Color(0, 0, 0, 0);

    public static String encode(ImageIcon imageIcon) {
        return Base64.getEncoder().encodeToString(imageToByteArray(imageIcon));
    }

    public static String[] encode(ImageIcon[] imageIcon) {
        return Arrays.stream(imageIcon)
                .map(IconUtil::encode)
                .toArray(String[]::new);
    }

    public static byte[] imageToByteArray(ImageIcon imageIcon) {
        try {
            BufferedImage image = new BufferedImage(imageIcon.getIconWidth(), imageIcon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
            ByteArrayOutputStream b = new ByteArrayOutputStream();

            Graphics g = image.createGraphics();
            imageIcon.paintIcon(null, g, 0,0);
            ImageIO.write(image, "png", b );
            g.dispose();

            return b.toByteArray();
        } catch (IOException ex) {
            System.out.println(ex.getLocalizedMessage());
            return null;
        }
    }

    public static ImageIcon decode(String encodeImage) {
        if (StringUtil.isBlank(encodeImage))
            return null;
        try {
            byte[] btDataFile = Base64.getDecoder().decode(encodeImage);
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(btDataFile));
            BufferedImage alphaImg = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
            alphaImg.createGraphics().drawImage(image, 0, 0, COLOR_TRANSPARENT, null);
            return new ImageIcon(alphaImg);
        } catch (IOException ex) {
            System.out.println(ex.getLocalizedMessage());
            return null;
        }
    }

    private static FilenameFilter getFilter(String[] extensions) {
        return (dir, name) -> Arrays.stream(extensions).anyMatch(ext -> name.endsWith("." + ext));
    }

    public static ImageIcon[] loadSequence(String dir) {
        return loadSequence(dir, EXTENSIONS);
    }

    public static ImageIcon[] loadSequence(String dir, String[] extensions) {
        File folder = new File(dir);
        if (!folder.isDirectory())
            throw new IllegalArgumentException("require folder path");
        if (extensions == null)
            extensions = EXTENSIONS;
        File[] files = folder.listFiles(getFilter(extensions));
        if (files == null || files.length == 0)
            throw new IllegalArgumentException("Folder contain no image");

        ImageIcon[] images = new ImageIcon[files.length];
        for (int i = 0; i < images.length; ++i) {
            images[i] = new ImageIcon(files[i].getPath());
        }
        return images;
    }

}
