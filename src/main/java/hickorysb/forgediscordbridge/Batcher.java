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

    public Batcher(Consumer<List<T>> consumer, long delayMs, int largeQueueSize, String threadName) {
        this(consumer, delayMs, largeQueueSize, Executors.newSingleThreadScheduledExecutor(
                new ThreadFactoryBuilder().setNameFormat(threadName).setDaemon(true).build()
        ));
    }

    public Batcher(Consumer<List<T>> consumer, long delayMs, int largeQueueSize, ScheduledExecutorService executor) {
        this.consumer = consumer;
        this.delayMs = delayMs;
        this.largeQueueSize = largeQueueSize;
        this.executor = executor;
    }

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