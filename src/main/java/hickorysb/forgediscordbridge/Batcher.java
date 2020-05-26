package hickorysb.forgediscordbridge;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class Batcher<T> {
    private final Consumer<List<T>> consumer;
    private final long delayMs;
    private final int largeQueueSize;
    private final ScheduledExecutorService executor;

    private ScheduledFuture<?> nextFlush;
    private final BlockingQueue<T> queue = new LinkedBlockingQueue<>();

    /**
     * Constructor.
     *
     * Note that this creates a new single thread executor, so you should treat the Batcher as a singleton wherever
     * possible, and definitely don't create a lot of them.
     *
     * @param consumer       The list of queued messages will be passed to this Consumer after the delay.
     * @param delayMs        Wait for a pause this many milliseconds long, before calling the Consumer.
     * @param largeQueueSize If this many messages are queued up, then we'll call the Consumer ASAP. Note that this is
     *                       NOT a limit on the size of the List passed to the Consumer; more messages can slip in
     *                       prior to the Consumer getting called.
     * @param threadName     Name of the executor thread that runs the Consumer. (for debugging, obviously)
     */
    public Batcher(Consumer<List<T>> consumer, long delayMs, int largeQueueSize, String threadName) {
        this(consumer, delayMs, largeQueueSize, Executors.newSingleThreadScheduledExecutor(
                new ThreadFactoryBuilder().setNameFormat(threadName).setDaemon(true).build()
        ));
    }

    /**
     * This constructor takes an executor object, so you could use it to share an executor across multiple Batchers.
     *
     * @see Batcher#Batcher(Consumer, long, int, String)
     */
    public Batcher(Consumer<List<T>> consumer, long delayMs, int largeQueueSize, ScheduledExecutorService executor) {
        this.consumer = consumer;
        this.delayMs = delayMs;
        this.largeQueueSize = largeQueueSize;
        this.executor = executor;
    }

    /**
     * Queue up an item. If the time since the last item is shorter than `delayMs`, then this effectively resets the
     * timer, so that another `delayMs` ms must pass with no more queued items before the Consumer is called. But if
     * adding this item to the queue causes the queue to reach `largeQueueSize` then the Consumer will be called ASAP.
     *
     * @param item The item to be queued.
     */
    public void queue(T item) {
        if (nextFlush != null) {
            nextFlush.cancel(false);
        }
        this.queue.add(item);
        // If the queue has gotten large, then queue up the next flush just 1 ms in the future. This will give some
        // wiggle room for any code that is calling queue in a tight loop, but will send the messages the instant there
        // is a tiny pause.
        long delay = this.queue.size() >= largeQueueSize ? 1 : delayMs;
        nextFlush = executor.schedule(this::flushQueue, delay, TimeUnit.MILLISECONDS);
    }

    private void flushQueue() {
        List<T> messages = new ArrayList<>(this.queue.size());
        this.queue.drainTo(messages);
        if (!messages.isEmpty()) {
            consumer.accept(messages);
        }
    }
}