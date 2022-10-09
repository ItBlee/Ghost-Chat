package core;

import java.util.UUID;

public class Adapter {

    public boolean isRegisteredSession(UUID uuid) {
        return Launcher.getInstance().getSessions().containsKey(uuid);
    }

}
