package cn.sskbskdrin.base.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.sskbskdrin.base.IView;
import cn.sskbskdrin.util.function.BiConsumer;
import cn.sskbskdrin.util.function.Consumer;
import cn.sskbskdrin.util.function.Function;

/**
 * Created by keayuan on 2021/7/22.
 *
 * @author keayuan
 */
public class IHolder<B> implements IView {

    private final View itemView;
    private B bean;
    private int position;
    private int type;

    private Consumer<? super IHolder<B>> click;
    private Function<? super IHolder<B>, Boolean> longClick;
    private Consumer<? super IHolder<B>> consumer;

    private BiConsumer<? super IHolder<B>, Boolean> checkChange;
    private boolean isCheck;
    private Object attach;

    public IHolder(View view) {
        itemView = view;
    }

    protected IHolder(IBuilder<B, ? super IHolder<B>> builder) {
        itemView = builder.view;
        consumer = builder.consumer;
        click = builder.click;
        longClick = builder.longClick;
        checkChange = builder.checkChange;
        attach = builder.attach;
    }


    public void setItem(B b, int position, int type) {
        this.bean = b;
        this.position = position;
        this.type = type;
    }

    public void updateView() {
        if (consumer != null) {
            consumer.accept(this);
        }
    }

    public final View rootView() {
        return itemView;
    }

    public B bean() {
        return bean;
    }

    public int position() {
        return position;
    }

    public int type() {
        return type;
    }

    public void onClickItem() {
        if (click != null) {
            click.accept(this);
        }
    }

    public boolean onLongClickItem() {
        if (longClick != null) {
            return longClick.apply(this);
        }
        return false;
    }

    public Object getAttach() {
        return attach;
    }

    @Override
    public Context getContext() {
        return itemView.getContext();
    }

    @Override
    public <V extends View> V getView(int id) {
        return IView.getView(itemView, id);
    }

    public static class Builder<B> extends IBuilder<B, IHolder<B>> {

        public Builder(ViewGroup parent) {
            super(parent);
        }

        @Override
        public IHolder<B> build() {
            return new IHolder<>(this);
        }
    }

    public static abstract class IBuilder<B, VH extends IHolder<B>> {
        protected final ViewGroup parent;
        protected View view;
        protected Consumer<VH> click;
        protected Function<VH, Boolean> longClick;
        protected BiConsumer<VH, Boolean> checkChange;
        protected Consumer<VH> consumer;
        protected Object attach;

        protected IBuilder(ViewGroup parent) {
            this.parent = parent;
        }

        public IBuilder<B, VH> view(int layoutId) {
            this.view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
            return this;
        }

        public IBuilder<B, VH> view(View view) {
            this.view = view;
            return this;
        }

        public IBuilder<B, VH> updateView(Consumer<VH> consumer) {
            this.consumer = consumer;
            return this;
        }

        public IBuilder<B, VH> clickItem(Consumer<VH> listener) {
            this.click = listener;
            return this;
        }

        public IBuilder<B, VH> clickItemLong(Function<VH, Boolean> listener) {
            this.longClick = listener;
            return this;
        }

        public IBuilder<B, VH> checkChange(BiConsumer<VH, Boolean> listener) {
            checkChange = listener;
            return this;
        }

        public IBuilder<B, VH> attach(Object attach) {
            this.attach = attach;
            return this;
        }

        public abstract VH build();
    }
}
