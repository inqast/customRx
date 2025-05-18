package rx.observer;

public interface Observer<T> {
    void onNext(T item);
    void onComplete();
    void onError(Throwable t);
} 