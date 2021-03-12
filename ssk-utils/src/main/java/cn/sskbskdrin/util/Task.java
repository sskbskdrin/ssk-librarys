package cn.sskbskdrin.util;

import android.os.Handler;
import android.os.Looper;
import android.util.Pair;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;

import cn.sskbskdrin.util.function.BiConsumer;
import cn.sskbskdrin.util.function.Consumer;

public final class Task<V> extends FutureTask<V> {
    private final Pair<BiConsumer<Task<V>, V>, Executor> success;
    private final Pair<BiConsumer<Task<V>, Throwable>, Executor> error;
    private final Pair<Consumer<Task<V>>, Executor> cancel;
    private final Pair<Consumer<Task<V>>, Executor> complete;

    private final String tag;

    private Task(Builder<V> builder) {
        super(builder.callable);
        success = builder.success;
        error = builder.error;
        cancel = builder.cancel;
        complete = builder.complete;
        tag = builder.tag;
    }

    @Override
    protected void done() {
        if (isCancelled() && checkPair(cancel)) {
            cancel.second.execute(() -> cancel.first.accept(Task.this));
        } else {
            try {
                V v = get();
                if (checkPair(success)) {
                    success.second.execute(() -> success.first.accept(Task.this, v));
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                if (checkPair(error)) {
                    error.second.execute(() -> error.first.accept(Task.this, e));
                }
            }
        }
        if (checkPair(complete)) {
            complete.second.execute(() -> complete.first.accept(Task.this));
        }
    }

    private boolean checkPair(Pair<?, ?> pair) {
        return !(pair == null || pair.first == null || pair.second == null);
    }

    public boolean cancel() {
        return super.cancel(true);
    }

    public String getTag() {
        return tag;
    }

    public static final class Builder<V> {
        private static final Handler mainHandler = new Handler(Looper.getMainLooper());
        private static final Executor mainExecutor = command -> {
            if (command != null) {
                mainHandler.post(command);
            }
        };

        private Pair<BiConsumer<Task<V>, V>, Executor> success;
        private Pair<BiConsumer<Task<V>, Throwable>, Executor> error;
        private Pair<Consumer<Task<V>>, Executor> cancel;
        private Pair<Consumer<Task<V>>, Executor> complete;

        private final Callable<V> callable;
        private String tag;

        public Builder(Callable<V> callable) {
            this.callable = callable;
        }

        public Builder<V> tag(String tag) {
            this.tag = tag;
            return this;
        }

        public Builder<V> success(BiConsumer<Task<V>, V> consumer) {
            return success(consumer, mainExecutor);
        }

        public Builder<V> success(BiConsumer<Task<V>, V> consumer, Executor executor) {
            success = Pair.create(consumer, executor);
            return this;
        }

        public Builder<V> error(BiConsumer<Task<V>, Throwable> consumer) {
            return error(consumer, mainExecutor);
        }

        public Builder<V> error(BiConsumer<Task<V>, Throwable> consumer, Executor executor) {
            error = Pair.create(consumer, executor);
            return this;
        }

        public Builder<V> cancel(Consumer<Task<V>> consumer) {
            return cancel(consumer, mainExecutor);
        }

        public Builder<V> cancel(Consumer<Task<V>> consumer, Executor executor) {
            cancel = Pair.create(consumer, executor);
            return this;
        }

        public Builder<V> complete(Consumer<Task<V>> consumer) {
            return complete(consumer, mainExecutor);
        }

        public Builder<V> complete(Consumer<Task<V>> consumer, Executor executor) {
            complete = Pair.create(consumer, executor);
            return this;
        }

        public Task<V> build() {
            return new Task<>(this);
        }
    }
}