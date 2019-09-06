package mockrest.root.osgi.runtime.task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Executor to run a task/action asynchronously.
 * Uses Cached threadpool to manage threads for asyn tasks.
 *
 * @author prince.arora
 */
public class AsyncTaskExecutor {

    //executor with cached thread pool.
    private ExecutorService executorService = Executors.newCachedThreadPool();

    private static AsyncTaskExecutor EXECUTOR;

    static {
        EXECUTOR = new AsyncTaskExecutor();
    }

    public static AsyncTaskExecutor executor() {
        return EXECUTOR;
    }

    /**
     * Execute action asynchronously, using executor service.
     *
     * Requires an implementation of functional interface {@link Task} or
     * a lamda function to contain actual action to be performed in another thread.
     *
     * @param task
     */
    public void execute(Task task) {
        this.executorService.submit(
                new Runnable() {
                    @Override
                    public void run() {
                        task.run();
                    }
                }
        );
    }

}
