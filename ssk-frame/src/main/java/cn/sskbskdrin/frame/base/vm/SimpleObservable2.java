package cn.sskbskdrin.frame.base.vm;

import java.lang.ref.WeakReference;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by keayuan on 2020/11/25.
 *
 * @author keayuan
 */
public class SimpleObservable2<V extends LifeOwner, T> implements Observable2<V, T> {
    private Supplier<T> supplier;
    private WeakReference<V> ownerWeakReference;
    private boolean io;

    private static Executor executor = new ThreadPoolExecutor(0, 10, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

    public SimpleObservable2(Supplier<T> supplier, V owner) {
        this.supplier = supplier;
        ownerWeakReference = new WeakReference<>(owner);
    }

    public SimpleObservable2(Supplier<T> supplier, V owner, boolean io) {
        this.supplier = supplier;
        ownerWeakReference = new WeakReference<>(owner);
        this.io = io;
    }

    @Override
    public final void observe(Observer2<V, T> observer2) {
        InternalObserver<V, T> observer = new InternalObserver<>(ownerWeakReference, observer2);
        if (io) {
            executor.execute(() -> {
                try {
                    supplier.run(observer);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ownerWeakReference = null;
            });
        } else {
            try {
                supplier.run(observer);
            } catch (Exception e) {
                e.printStackTrace();
            }
            ownerWeakReference = null;
        }
    }

    public static <V extends LifeOwner, T> Observable2<V, T> request(V v, Supplier<T> supplier) {
        return new SimpleObservable2<>(supplier, v);
    }

    private static class InternalObserver<V extends LifeOwner, T> extends BaseModel<T> implements Observer<T> {
        private Observer2<V, T> mObserver2;
        private WeakReference<V> ownerWeakReference;

        private InternalObserver(WeakReference<V> vWeak, Observer2<V, T> observer2) {
            mObserver2 = observer2;
            ownerWeakReference = vWeak;
            observe(vWeak.get(), this);
        }

        @Override
        public void onChanged(T t) {
            if (mObserver2 != null && ownerWeakReference.get() != null) {
                mObserver2.onChanged(ownerWeakReference.get(), t);
            }
        }

        @Override
        protected void onInactive() {
            super.onInactive();
            removeObserver(this);
            mObserver2 = null;
        }
    }
}
