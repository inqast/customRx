package rx.scheduler;

import java.util.concurrent.Executors;

public class IOThreadScheduler extends AbstractScheduler {

    private static final IOThreadScheduler INSTANCE = new IOThreadScheduler();

    private IOThreadScheduler() {
        super(Executors.newCachedThreadPool());
    }

    public static IOThreadScheduler getInstance() {
        return INSTANCE;
    }
} 