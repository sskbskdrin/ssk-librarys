package cn.sskbskdrin.lib.demo.widget.swipe;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import cn.sskbskdrin.base.IFragment;
import cn.sskbskdrin.widget.swipe.SwipeLayout;
import cn.sskbskdrin.widget.swipe.SwipePosition;
import cn.sskbskdrin.widget.swipe.SwipeRefreshListener;

/**
 * Created by ayke on 2016/10/19 0019.
 */

abstract class BaseFragment extends IFragment implements SwipeRefreshListener {

    protected SwipeLayout mSwipeLayout;

    @Override
    protected void onInitView(View rootView,  Bundle savedInstanceState) {
        if (mRootView instanceof SwipeLayout) {
            mSwipeLayout = (SwipeLayout) mRootView;
        } else {
            for (int i = 0; i < ((ViewGroup) mRootView).getChildCount(); i++) {
                if (((ViewGroup) mRootView).getChildAt(i) instanceof SwipeLayout) {
                    mSwipeLayout = (SwipeLayout) ((ViewGroup) mRootView).getChildAt(i);
                    break;
                }
            }
        }
        mSwipeLayout.addSwipeRefreshListener(this);
        mSwipeLayout.addSwipeRefreshListener(SwipePosition.BOTTOM, this);
    }

    @Override
    public void onLoading(SwipePosition position) {
        mRootView.postDelayed(() -> {
            switch (position) {
                case TOP:
                    refreshTop();
                    break;
                case LEFT:
                    refreshLeft();
                    break;
                case RIGHT:
                    refreshRight();
                    break;
                case BOTTOM:
                    refreshBottom();
                    break;
                default:
            }
            mSwipeLayout.refreshComplete(position, true);
        }, 1000);
    }

    protected void refreshLeft() {
    }

    protected void refreshTop() {
    }

    protected void refreshRight() {
    }

    protected void refreshBottom() {
    }
}
