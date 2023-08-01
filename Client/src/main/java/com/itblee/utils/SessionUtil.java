package com.itblee.utils;

import com.itblee.core.helper.ClientHelper;
import com.itblee.security.Certificate;
import com.itblee.security.User;

import java.util.*;

import static com.itblee.constant.ClientConstant.RESOURCE_PATH;

public class SessionUtil {

    public static Certificate load() {
        String path = RESOURCE_PATH + PropertyUtil.getString("session.store.path");
        String sessionInfo = FileUtil.readLineThenDelete(path);
        if (sessionInfo == null)
            return null;
        StringTokenizer tokenizer = new StringTokenizer(sessionInfo, "|");
        try {
            UUID uuid = UUID.fromString(tokenizer.nextToken());
            String secretKey = tokenizer.nextToken();
            Date date = DateUtil.stringToDate(tokenizer.nextToken());
            long timeout = PropertyUtil.getInt("session.timeout");
            if (DateUtil.between(new Date(), date) >= timeout)
                return null;
            return new Certificate(uuid, secretKey);
        } catch (Exception e) {
            return null;
        }
    }

    public static void save() {
        String path = RESOURCE_PATH + PropertyUtil.getString("session.store.path");
        User user = ClientHelper.getUser();
        String line = user.getUid()
                + "|" + user.getSecretKey()
                + "|" + DateUtil.dateToString(new Date());
        FileUtil.write(path, Collections.singleton(line), true);
    }

}
