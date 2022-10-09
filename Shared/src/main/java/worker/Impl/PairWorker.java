package worker.Impl;

import object.SecretKey;

import java.io.IOException;
import java.net.Socket;

public abstract class PairWorker<T> extends SecureWorker {
    protected T pair;
    protected boolean isPaired = false;

    public PairWorker(Socket socket, SecretKey secretKey) throws IOException {
        super(socket, secretKey);
    }

    public void breakPair() {
        setPair(null);
        unlockPair();
    }

    public void lockPair() {
        if (!isPaired)
            isPaired = true;
    }

    public void unlockPair() {
        if (isPaired)
            isPaired = false;
    }

    public boolean isPaired() {
        return isPaired;
    }

    public T getPair() {
        return pair;
    }

    public void setPair(T pair) {
        this.pair = pair;
        lockPair();
    }
}
