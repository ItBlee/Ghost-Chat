package com.itblee.constant;

import com.itblee.utils.IconUtil;

import javax.swing.*;

import static com.itblee.constant.ServerConstant.RESOURCE_PATH;

public interface Resource {

    String[] AVATARS_ENCODE = IconUtil.encode(new ImageIcon[] {
            new ImageIcon(RESOURCE_PATH + "images/avatar/bear.png"),
            new ImageIcon(RESOURCE_PATH + "images/avatar/cat.png"),
            new ImageIcon(RESOURCE_PATH + "images/avatar/chicken.png"),
            new ImageIcon(RESOURCE_PATH + "images/avatar/dog.png"),
            new ImageIcon(RESOURCE_PATH + "images/avatar/panda.png"),
            new ImageIcon(RESOURCE_PATH + "images/avatar/sea-lion.png"),
    });

}
