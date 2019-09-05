package mockrest.root.osgi.runtime.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author prince.arora
 */
public class AsyncTaskExecutor {

    private static final Logger logger = LoggerFactory.getLogger(AsyncTaskExecutor.class);

    private ExecutorService executorService = Executors.newCachedThreadPool();

    public static AsyncTaskExecutor EXECUTOR;

    static {
        EXECUTOR = new AsyncTaskExecutor();
    }

    public static AsyncTaskExecutor executor() {
        return EXECUTOR;
    }

    public Future<Object> execute(Runnable runnable) {
        return executorService.submit(Executors.callable(runnable));
    }

}
