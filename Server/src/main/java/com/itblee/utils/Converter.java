package com.itblee.utils;

import com.itblee.core.User;
import com.itblee.model.Message;
import com.itblee.repository.document.Log;
import com.itblee.repository.document.UserDetail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Converter {

    public static User convertUser(UserDetail src, User des) {
        des.setUsername(src.getUsername());
        if (src.getStatus() == 0)
            des.ban();
        des.setCreatedDate(src.getCreatedDate());
        return des;
    }

    public static String convertTime(long milliseconds) {
        String minutes = String.valueOf(TimeUnit.MILLISECONDS.toMinutes(milliseconds));
        String seconds = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(milliseconds) % 60);
        if (Integer.parseInt(minutes) < 10) {
            minutes = "0" + minutes;
        }
        if (Integer.parseInt(seconds) < 10) {
            seconds = "0" + seconds;
        }
        return minutes + ":" + seconds;
    }

    public static List<Message> extractHistoryChat(Collection<Log> logs) {
        List<Message> messages = new ArrayList<>();
        for (Log log : logs) {
            Message message = new Message();
            message.setSender(log.getUsername());
            message.setSentDate(DateUtil.dateToString(log.getCreatedDate()));
            message.setBody(log.getDetail().replace("Message: ", ""));
            messages.add(message);
        }
        return messages;
    }

}
