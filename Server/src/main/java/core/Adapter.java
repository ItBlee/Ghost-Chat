package core;

import security.Certificate;

public class Adapter {

    public boolean isRegisteredSession(String uid) {
        for (Certificate certificate:Launcher.getInstance().getSessions())
            if (certificate.getUid().equalsIgnoreCase(uid) && certificate.isAuthenticated())
                return true;
        return false;
    }

}
