package mockrest.root.osgi.runtime.task;

/**
 * task to be passed in {@link AsyncTaskExecutor#execute(Task)} for
 * Asynchronous execution.
 *
 * @author prince.arora
 */
@FunctionalInterface
public interface Task {

    /**
     * function to contain actual action/code that
     * needs to be executed in different thread.
     */
    void run();
}
