package rx.disposable;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import rx.observable.Observable;
import rx.observer.DefaultObserver;

class DisposableTest {
    @Test
    void testDisposableNotDisposed() {
        // Arrange
        AtomicBoolean disposed = new AtomicBoolean(false);
        DefaultDisposable disposable = new DefaultDisposable(disposed);

        // Assert
        assertFalse(disposable.isDisposed());
        assertFalse(disposed.get());
    }

    @Test
    void testDisposableDisposed() {
        // Arrange
        AtomicBoolean disposed = new AtomicBoolean(false);
        DefaultDisposable disposable = new DefaultDisposable(disposed);

        // Act
        disposable.dispose();

        // Assert
        assertTrue(disposable.isDisposed());
        assertTrue(disposed.get());
    }

    @Test
    void testObservableDisposable() {
        // Arrange
        DefaultObserver<Integer> observer = new DefaultObserver<>();
        AtomicBoolean completed = new AtomicBoolean(false);

        Observable<Integer> observable = Observable.create(obs -> {
            for (int i = 0; i < 1000; i++) {
                if (i == 5) {
                    completed.set(true);
                }
                obs.onNext(i);
            }
            obs.onComplete();
        });

        // Act
        Disposable disposable = observable.subscribe(observer);
        while (!completed.get()) {
            Thread.yield();
        }

        disposable.dispose();

        // Assert
        assertTrue(disposable.isDisposed());
        assertEquals(1000, observer.getItems().size());
    }
} 