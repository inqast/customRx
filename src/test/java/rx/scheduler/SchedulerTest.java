package rx.scheduler;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import rx.observable.Observable;
import rx.observer.DefaultObserver;

class SchedulerTest {

    @Test
    void testSubscribeOn() throws InterruptedException {
        // Arrange
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger threadId = new AtomicInteger();
        DefaultObserver<Integer> observer = new DefaultObserver<>();

        Observable<Integer> observable = Observable.create(obs -> {
            threadId.set((int) Thread.currentThread().threadId());
            obs.onNext(1);
            obs.onNext(2);
            obs.onNext(3);
            obs.onComplete();
            latch.countDown();
        });

        observable
                .subscribeOn(IOThreadScheduler.getInstance())
                .subscribe(observer);

        assertTrue(latch.await(1, TimeUnit.SECONDS));
        assertNotEquals(Thread.currentThread().threadId(), threadId.get());
        assertEquals(List.of(1, 2, 3), observer.getItems());
        assertTrue(observer.isCompleted());
    }

    @Test
    void testObserveOn() throws InterruptedException {
        // Arrange
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger threadId = new AtomicInteger();
        DefaultObserver<Integer> observer = new DefaultObserver<>();

        Observable<Integer> observable = Observable.create(obs -> {
            obs.onNext(1);
            obs.onNext(2);
            obs.onNext(3);
            obs.onComplete();
            latch.countDown();
        });

        // Act
        observable
                .observeOn(ComputationScheduler.getInstance())
                .subscribe(observer);

        // Assert
        assertTrue(latch.await(1, TimeUnit.SECONDS));
        assertNotEquals(Thread.currentThread().threadId(), threadId.get());
        assertEquals(List.of(1, 2, 3), observer.getItems());
        assertTrue(observer.isCompleted());
    }

    @Test
    void testSingleThreadScheduler() throws InterruptedException {
        // Arrange
        CountDownLatch latch = new CountDownLatch(1);
        DefaultObserver<Integer> observer = new DefaultObserver<>();

        Observable<Integer> observable = Observable.create(obs -> {
            obs.onNext(1);
            obs.onNext(2);
            obs.onNext(3);
            obs.onComplete();
            latch.countDown();
        });

        // Act
        observable
                .subscribeOn(SingleThreadScheduler.getInstance())
                .subscribe(observer);
        
        // Assert
        assertTrue(latch.await(1, TimeUnit.SECONDS));
        assertEquals(List.of(1, 2, 3), observer.getItems());
        assertTrue(observer.isCompleted());
    }
} 