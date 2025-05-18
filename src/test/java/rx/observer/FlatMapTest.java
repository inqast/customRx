package rx.observer;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import rx.observable.Observable;

class FlatMapTest {

    @Test
    void testFlatMap() {
        // Arrange
        DefaultObserver<String> observer = new DefaultObserver<>();

        Observable<Integer> source = Observable.create(obs -> {
            obs.onNext(1);
            obs.onNext(2);
            obs.onComplete();
        });

        Observable<String> result = source.flatMap(i -> Observable.create(obs -> {
            obs.onNext("Value: " + i);
            obs.onNext("Double: " + (i * 2));
            obs.onComplete();
        }));

        result.subscribe(observer);

        assertEquals(List.of("Value: 1", "Double: 2", "Value: 2", "Double: 4"), observer.getItems());
        assertNull(observer.getError());
        assertTrue(observer.isCompleted());
    }

    @Test
    void testFlatMapError() {
        // Arrange
        DefaultObserver<String> observer = new DefaultObserver<>();
        RuntimeException error = new RuntimeException("Test error");

        Observable<Integer> source = Observable.create(obs -> {
            obs.onNext(1);
            obs.onNext(2);
            obs.onComplete();
        });

        Observable<String> result = source.flatMap(i -> {
            if (i == 2) {
                throw error;
            }
            return Observable.create(obs -> {
                obs.onNext("Value: " + i);
                obs.onComplete();
            });
        });

        result.subscribe(observer);

        assertEquals(List.of("Value: 1"), observer.getItems());
        assertEquals(error, observer.getError());
        assertFalse(observer.isCompleted());
    }

    @Test
    void testFlatMapInnerError() {
        // Arrange
        DefaultObserver<String> observer = new DefaultObserver<>();
        RuntimeException error = new RuntimeException("Test error");

        Observable<Integer> source = Observable.create(obs -> {
            obs.onNext(1);
            obs.onNext(2);
            obs.onComplete();
        });

        Observable<String> result = source.flatMap(i -> Observable.create(obs -> {
            if (i == 2) {
                throw error;
            }
            obs.onNext("Value: " + i);
            obs.onComplete();
        }));

        // Act
        result.subscribe(observer);

        // Assert
        assertEquals(List.of("Value: 1"), observer.getItems());
        assertEquals(error, observer.getError());
        assertFalse(observer.isCompleted());
    }
} 