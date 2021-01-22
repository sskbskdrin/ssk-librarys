package cn.sskbskdrin.lib.demo.widget.swipe;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import cn.sskbskdrin.lib.demo.R;
import cn.sskbskdrin.widget.swipe.SwipePosition;

/**
 * Created by ayke on 2016/9/26 0026.
 */

public class WebFragment extends BaseFragment {
    private WebView content;

    @Override
    protected int getLayoutId() {
        return R.layout.f_swipe_web;
    }

    @Override
    protected void onInitView(View rootView, Bundle savedInstanceState) {
        super.onInitView(rootView, savedInstanceState);
        content = getView(R.id.web_content);
        content.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                mSwipeLayout.refreshComplete(SwipePosition.TOP, true);
            }
        });
        refreshTop();
        mSwipeLayout.setRefreshing();
    }

    @Override
    protected void refreshTop() {
        content.loadUrl("https://www.baidu.com/");
    }
}
