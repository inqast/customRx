package rx.disposable;

import java.util.concurrent.atomic.AtomicBoolean;

public class DefaultDisposable implements Disposable {

    private final AtomicBoolean disposed;

    public DefaultDisposable(AtomicBoolean disposed) {
        this.disposed = disposed;
    }

    @Override
    public boolean isDisposed() {
        return disposed.get();
    }

    @Override
    public void dispose() {
        disposed.set(true);
    }
} 