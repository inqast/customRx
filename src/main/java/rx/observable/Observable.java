package rx.observable;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import rx.disposable.DefaultDisposable;
import rx.disposable.Disposable;
import rx.observer.DisposableObserver;
import rx.observer.FilteredObserver;
import rx.observer.FlatMapObserver;
import rx.observer.MappedObserver;
import rx.observer.Observer;
import rx.observer.ScheduledObserver;
import rx.scheduler.Scheduler;

public class Observable<T> {

    private final Consumer<Observer<T>> consumer;

    private Observable(Consumer<Observer<T>> consumer) {
        this.consumer = consumer;
    }

    public static <T> Observable<T> create(Consumer<Observer<T>> consumer) {
        return new Observable<>(consumer);
    }

    public Disposable subscribe(Observer<T> observer) {
        AtomicBoolean disposed = new AtomicBoolean(false);
        Observer<T> disposableObserver = new DisposableObserver<>(observer, disposed);

        try {
            consumer.accept(disposableObserver);
        } catch (Throwable t) {
            if (!disposed.get()) {
                observer.onError(t);
            }
        }

        return new DefaultDisposable(disposed);
    }

    public Observable<T> subscribeOn(Scheduler scheduler) {
        return new Observable<>(observer -> {
            AtomicBoolean disposed = new AtomicBoolean(false);
            scheduler.execute(() -> {
                if (!disposed.get()) {
                    subscribe(new DisposableObserver<>(observer, disposed));
                }
            });
        });
    }

    public Observable<T> observeOn(Scheduler scheduler) {
        return new Observable<>(observer -> {
            AtomicBoolean disposed = new AtomicBoolean(false);
            subscribe(new ScheduledObserver<>(observer, scheduler, disposed));
        });
    }

    public <R> Observable<R> map(Function<T, R> mapper) {
        return new Observable<>(observer -> {
            AtomicBoolean disposed = new AtomicBoolean(false);
            subscribe(new MappedObserver<>(observer, mapper, disposed));
        });
    }

    public Observable<T> filter(Predicate<T> predicate) {
        return new Observable<>(observer -> {
            AtomicBoolean disposed = new AtomicBoolean(false);
            subscribe(new FilteredObserver<>(observer, predicate, disposed));
        });
    }

    public <R> Observable<R> flatMap(Function<? super T, ? extends Observable<? extends R>> mapper) {
        return new Observable<>(observer -> {
            AtomicBoolean disposed = new AtomicBoolean(false);
            subscribe(new FlatMapObserver<>(observer, mapper, disposed));
        });
    }
} 