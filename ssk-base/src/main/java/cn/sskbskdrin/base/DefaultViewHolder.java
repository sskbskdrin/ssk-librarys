package cn.sskbskdrin.base;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

public class DefaultViewHolder<B> implements IViewHolder<B> {

    protected final View itemView;
    protected B bean;
    protected int position;

    private Consumer<B> consumer;

    public DefaultViewHolder(View view) {
        if (view == null) throw new NullPointerException("item view not null");
        itemView = view;
        ViewGroup.LayoutParams lp = itemView.getLayoutParams();
        if (lp == null) {
            lp = new ViewGroup.LayoutParams(-1, -2);
            itemView.setLayoutParams(lp);
        }
    }

    public DefaultViewHolder(Context context, int layoutId) {
        this(View.inflate(context, layoutId, null));
    }

    public DefaultViewHolder<B> setConsumer(Consumer<B> consumer) {
        this.consumer = consumer;
        return this;
    }

    @Override
    public void setItem(B b, int position) {
        this.bean = b;
        this.position = position;
    }

    @Override
    public void updateView(B b, int position) {
        if (consumer != null) {
            consumer.accept(this, b, position);
        }
    }

    @Override
    public View itemView() {
        return itemView;
    }

    @Override
    public B bean() {
        return bean;
    }

    @Override
    public int position() {
        return position;
    }
}