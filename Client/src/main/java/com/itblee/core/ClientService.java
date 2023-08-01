package com.itblee.core;

import com.itblee.core.helper.TransferHelper;
import com.itblee.security.Certificate;
import com.itblee.security.EncryptUtil;
import com.itblee.security.User;
import com.itblee.transfer.DataKey;
import com.itblee.transfer.Packet;
import com.itblee.transfer.Request;
import com.itblee.transfer.StatusCode;
import com.itblee.utils.JsonParser;
import com.itblee.utils.StringUtil;

import java.io.IOException;

public class ClientService {

    public void verify() throws IOException, InterruptedException {
        Client client = Client.getInstance();
        User user = client.getUser();
        Packet resp;
        do {
            Worker worker;
            if (user.getCertificate() != null && user.getUid() != null
                    && StringUtil.isBlank(user.getCertificate().getSecretKey())) {
                worker = changeSecretKey();
            } else worker = requestSession();
            resp = worker.await();
        } while (!resp.is(StatusCode.CREATED) && !resp.is(StatusCode.OK));
    }

    public Worker requestSession() throws IOException {
        Client client = Client.getInstance();
        Worker worker = client.connectSSL();

        Certificate certificate = new Certificate(null, EncryptUtil.generateSecretKey());
        client.getUser().setCertificate(certificate);

        Packet request = TransferHelper.get();
        request.setHeader(Request.SESSION);
        request.putData(DataKey.SECRET_KEY, client.getUser().getSecretKey());
        worker.send(JsonParser.toJson(request));
        return worker;
    }

    public Worker changeSecretKey() throws IOException {
        Client client = Client.getInstance();
        User user = client.getUser();
        if (user.getCertificate() == null || user.getUid() == null)
            throw new IllegalStateException();
        Worker worker = client.connectSSL();
        String newKey = EncryptUtil.generateSecretKey();
        user.getCertificate().setSecretKey(newKey);
        Packet request = TransferHelper.get();
        request.setHeader(Request.SESSION_KEY);
        request.putData(DataKey.SESSION_ID, user.getUid());
        request.putData(DataKey.SECRET_KEY, newKey);
        worker.send(JsonParser.toJson(request));
        return worker;
    }

}
