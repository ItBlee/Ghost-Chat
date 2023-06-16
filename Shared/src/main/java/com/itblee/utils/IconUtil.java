package com.itblee.utils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;

public final class IconUtil {

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
            BufferedImage image = new BufferedImage(imageIcon.getIconWidth(), imageIcon.getIconHeight(), BufferedImage.TYPE_INT_RGB);
            ByteArrayOutputStream b = new ByteArrayOutputStream();

            Graphics g;
            g = image.createGraphics();
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
            return new ImageIcon(image);
        } catch (IOException ex) {
            System.out.println(ex.getLocalizedMessage());
            return null;
        }
    }
    public static ImageIcon[] loadSequence(String dir, String indexFormat, String extension, int size) {
        return loadSequence(dir, indexFormat, extension, size, 0);
    }

    public static ImageIcon[] loadSequence(String dir, String indexFormat, String extension, int size, int startIndex) {
        ImageIcon[] images = new ImageIcon[size];
        for (int i = 0; i < images.length; ++i) {
            String formatted = String.format(indexFormat, i + startIndex);
            images[i] = new ImageIcon(dir + formatted + extension);
        }
        return images;
    }

}
