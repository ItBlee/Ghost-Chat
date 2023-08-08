package com.itblee.constant;

import com.itblee.utils.IconUtil;

import javax.swing.*;
import java.awt.*;

import static com.itblee.constant.ClientConstant.RESOURCE_PATH;

public interface Resource {

    Color COLOR_TRANSPARENT = new Color(0, 0, 0, 0);
    Color COLOR_GRAY_BLUE = new Color(109, 176, 208);
    Color COLOR_LIGHT_BLUE = new Color(134, 238, 252);
    Color COLOR_BLUE = new Color(1, 133, 226);
    Color COLOR_DEEP_BLUE = new Color(1, 178, 254);
    Color COLOR_DARK_BLUE = new Color(32,20,96);
    Color COLOR_DARK_SEMI_BLUE = new Color(9,6,75);
    Color COLOR_LIGHT_GREEN = new Color(1, 254, 149);
    Color COLOR_ORANGE = new Color(246,88,61);
    Color COLOR_LIGHT_RED = new Color(250, 115, 115);
    Color COLOR_LIGHT_GREY = new Color(204, 204, 204);
    Color COLOR_WHITE_DEEP_GREY = new Color(249, 253, 255);
    Color COLOR_WHITE_GREY = new Color(238,241,249);
    Color COLOR_SEMI_BLACK = new Color(16, 13, 20);

    Font FONT_ARIA_PLAIN_11 = new Font("Arial", Font.PLAIN, 11);
    Font FONT_ARIA_PLAIN_12 = new Font("Arial", Font.PLAIN, 12);
    Font FONT_ARIA_PLAIN_14 = new Font("Arial", Font.PLAIN, 14);
    Font FONT_ARIA_PLAIN_16 = new Font("Arial", Font.PLAIN, 16);
    Font FONT_ARIA_BOLD_12 = new Font("Arial", Font.BOLD, 12);
    Font FONT_ARIA_BOLD_26 = new Font("Arial", Font.BOLD, 26);
    Font FONT_SEGOE_SEMI_PLAIN_14 = new Font("Segoe UI Semibold", Font.PLAIN, 14);
    Font FONT_SEGOE_BOLD_12 = new Font("Segoe UI", Font.BOLD, 12);
    Font FONT_SEGOE_BOLD_16 = new Font("Segoe UI", Font.BOLD, 16);
    Font FONT_SEGOE_BOLD_17 = new Font("Segoe UI", Font.BOLD, 17);
    Font FONT_SEGOE_BOLD_26 = new Font("Segoe UI", Font.BOLD, 26);
    Font FONT_COOPER_BLACK_PLAIN_14 = new Font("Cooper Black", Font.PLAIN, 14);

    ImageIcon[] COVER_LOADING_INTRO = IconUtil.loadSequence(RESOURCE_PATH + "images/loading/intro");
    ImageIcon[] COVER_LOADING_OUTRO = IconUtil.loadSequence(RESOURCE_PATH + "images/loading/outro");
}
