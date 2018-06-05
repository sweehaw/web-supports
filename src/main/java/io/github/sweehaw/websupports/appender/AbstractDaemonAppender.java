package io.github.sweehaw.websupports.appender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author sweehaw
 */
public abstract class AbstractDaemonAppender<E> implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractDaemonAppender.class);
    private static final int TIMEOUT = 60;

    private final BlockingQueue<E> queue;

    private AtomicBoolean start = new AtomicBoolean(false);
    private ExecutorService threadPool = Executors.newCachedThreadPool();

    AbstractDaemonAppender(int maxQueueSize) {
        this.queue = new LinkedBlockingQueue<>(maxQueueSize);
    }

    private void execute() {
        if (threadPool.isShutdown()) {
            threadPool = Executors.newCachedThreadPool();
        }
        threadPool.execute(this);
    }

    void log(E eventObject) {
        if (!this.queue.offer(eventObject)) {
            LOG.warn("Message queue is full. Ignored the message:" + System.lineSeparator() + eventObject.toString());
        } else if (start.compareAndSet(false, true)) {
            execute();
        }
    }

    @Override
    public void run() {

        try {
            for (; ; ) {
                append(this.queue.take());
            }
        } catch (InterruptedException e) {
            run();
        } catch (Exception e) {
            close();
        }
    }

    /**
     * append logger
     *
     * @param rawData ILoggingEvent
     */
    abstract protected void append(E rawData);

    protected void close() {
        synchronized (threadPool) {
            if (!threadPool.isShutdown()) {
                shutdownAndAwaitTermination(threadPool);
            }
        }
    }

    private static void shutdownAndAwaitTermination(ExecutorService pool) {
        pool.shutdown();
        try {
            if (!pool.awaitTermination(TIMEOUT, TimeUnit.SECONDS)) {
                pool.shutdownNow();
                if (!pool.awaitTermination(TIMEOUT, TimeUnit.SECONDS)) {
                    System.err.println("Pool did not terminate");
                }
            }
        } catch (InterruptedException ie) {
            pool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}