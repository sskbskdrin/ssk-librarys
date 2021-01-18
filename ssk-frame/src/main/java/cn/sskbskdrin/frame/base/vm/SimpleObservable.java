package cn.sskbskdrin.frame.base.vm;

import java.lang.ref.WeakReference;

/**
 * Created by keayuan on 2020/11/25.
 *
 * @author keayuan
 */
public class SimpleObservable<T> implements Observable<T> {
    private Supplier<T> supplier;
    private WeakReference<LifeOwner> ownerWeakReference;

    public SimpleObservable(Supplier<T> supplier, LifeOwner owner) {
        this.supplier = supplier;
        ownerWeakReference = new WeakReference<>(owner);
    }

    @Override
    public final void observe(Observer<T> observer) {
        try {
            supplier.run(new InternalObserver<>(ownerWeakReference.get(), observer));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class InternalObserver<V extends LifeOwner, T> extends BaseModel<T> implements Observer<T> {
        Observer<T> observer;

        private InternalObserver(V v, Observer<T> observer) {
            this.observer = observer;
            observe(v, this);
        }

        @Override
        public void onChanged(T t) {
            if (observer != null) {
                observer.onChanged(t);
            }
        }

        @Override
        protected void onInactive() {
            super.onInactive();
            removeObserver(this);
            observer = null;
        }
    }
}
