package worker.Impl;

import java.io.IOException;
import java.net.Socket;

public abstract class PairWorker<T> extends SecureWorker {
    protected T pair;
    protected boolean isPaired = false;

    public PairWorker(Socket socket, String secretKey) throws IOException {
        super(socket, secretKey);
    }

    public void breakPair() {
        setPair(null);
        lockPair(false);
    }

    public void lockPair(boolean b) {
        isPaired = b;
    }

    public boolean isPaired() {
        return isPaired;
    }

    public T getPair() {
        return pair;
    }

    public void setPair(T pair) {
        this.pair = pair;
        lockPair(true);
    }
}
