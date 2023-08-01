package com.itblee.gui;

import com.itblee.gui.component.Dialog;
import com.itblee.core.helper.ClientHelper;
import com.itblee.core.function.Choice;
import com.itblee.utils.IconUtil;
import com.itblee.utils.StringUtil;

import javax.swing.*;

import static com.itblee.constant.ClientConstant.RESOURCE_PATH;

public class Alert {

    private static final ImageIcon[] IMAGE_DIALOG_INVITE = IconUtil.loadSequence(RESOURCE_PATH + "images/dialog/invite");
    private static final ImageIcon[] IMAGE_DIALOG_CONFIRM = IconUtil.loadSequence(RESOURCE_PATH + "images/dialog/confirm");
    private static final ImageIcon[] IMAGE_DIALOG_DECLINE = IconUtil.loadSequence(RESOURCE_PATH + "images/dialog/decline");

    public static void showInvite(String message, Choice choice) {
        String content = StringUtil.applyWrapForGUI(message);
        Dialog.builder()
                .setOwner(ClientHelper.getFrame())
                .setMessage(content)
                .setIcon(IMAGE_DIALOG_INVITE)
                .setAcceptTitle("INVITE")
                .setDeclineTitle("IGNORE")
                .reply(choice)
                .build()
                .display();
    }

    public static void showInviteFail() {
        String content = StringUtil.applyWrapForGUI("Friend Offline...");
        Dialog.builder()
                .setOwner(ClientHelper.getFrame())
                .setMessage(content)
                .setIcon(IMAGE_DIALOG_DECLINE)
                .setAcceptTitle("NEXT")
                .build()
                .display();
    }

    public static void showConfirm(String message, Choice choice) {
        String content = StringUtil.applyWrapForGUI(message);
        Dialog.builder()
                .setOwner(ClientHelper.getFrame())
                .setMessage(content)
                .setIcon(IMAGE_DIALOG_CONFIRM)
                .setAcceptTitle("ACCEPT")
                .setDeclineTitle("DECLINE")
                .reply(choice)
                .build()
                .display();
    }

    public static void showError(String message) {
        showError(message, null);
    }

    public static void showError(String message, Choice choice) {
        String content = StringUtil.applyWrapForGUI(message);
        Dialog.builder()
                .setOwner(ClientHelper.getFrame())
                .setMessage(content)
                .setIcon(IMAGE_DIALOG_DECLINE)
                .setAcceptTitle("GOT IT")
                .reply(choice)
                .build()
                .display();
    }

    public static void loadDialogResources() {
    }

}
