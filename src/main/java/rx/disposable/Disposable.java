package rx.disposable;

public interface Disposable {
    boolean isDisposed();
    void dispose();
} 