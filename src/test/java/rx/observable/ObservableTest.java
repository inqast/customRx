package rx.observable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import rx.observer.DefaultObserver;

class ObservableTest {

    @Test
    void testBasicObservable() {
        // Arrange
        DefaultObserver<Integer> observer = new DefaultObserver<>();

        Observable<Integer> observable = Observable.create(obs -> {
            obs.onNext(1);
            obs.onNext(2);
            obs.onNext(3);
            obs.onComplete();
        });

        // Act
        observable.subscribe(observer);

        // Assert
        assertEquals(List.of(1, 2, 3), observer.getItems());
        assertNull(observer.getError());
        assertTrue(observer.isCompleted());
    }

    @Test
    void testErrorHandling() {
        // Arrange
        DefaultObserver<Integer> observer = new DefaultObserver<>();
        RuntimeException error = new RuntimeException("Test error");

        Observable<Integer> observable = Observable.create(obs -> {
            obs.onNext(1);
            throw error;
        });

        // Act
        observable.subscribe(observer);

        // Assert
        assertEquals(List.of(1), observer.getItems());
        assertEquals(error, observer.getError());
        assertFalse(observer.isCompleted());
    }

    @Test
    void testMapOperator() {
        // Arrange
        DefaultObserver<String> observer = new DefaultObserver<>();

        Observable<Integer> observable = Observable.create(obs -> {
            obs.onNext(1);
            obs.onNext(2);
            obs.onNext(3);
            obs.onComplete();
        });

        // Act
        observable
                .map(i -> "Number: " + i)
                .subscribe(observer);

        // Assert
        assertEquals(List.of("Number: 1", "Number: 2", "Number: 3"), observer.getItems());
        assertNull(observer.getError());
        assertTrue(observer.isCompleted());
    }

    @Test
    void testFilterOperator() {
        // Arrange
        DefaultObserver<Integer> observer = new DefaultObserver<>();

        Observable<Integer> observable = Observable.create(obs -> {
            obs.onNext(1);
            obs.onNext(2);
            obs.onNext(3);
            obs.onNext(4);
            obs.onNext(5);
            obs.onComplete();
        });

        // Act
        observable
                .filter(i -> i % 2 == 0)
                .subscribe(observer);

        // Assert
        assertEquals(List.of(2, 4), observer.getItems());
        assertNull(observer.getError());
        assertTrue(observer.isCompleted());
    }

    @Test
    void testMapAndFilterCombination() {
        // Arrange
        DefaultObserver<String> observer = new DefaultObserver<>();

        Observable<Integer> observable = Observable.create(obs -> {
            obs.onNext(1);
            obs.onNext(2);
            obs.onNext(3);
            obs.onNext(4);
            obs.onNext(5);
            obs.onComplete();
        });

        // Act
        observable
                .filter(i -> i % 2 == 0)
                .map(i -> "Even: " + i)
                .subscribe(observer);

        // Assert
        assertEquals(List.of("Even: 2", "Even: 4"), observer.getItems());
        assertNull(observer.getError());
        assertTrue(observer.isCompleted());
    }
} 