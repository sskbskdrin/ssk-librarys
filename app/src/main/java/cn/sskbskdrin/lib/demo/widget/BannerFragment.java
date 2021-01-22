package cn.sskbskdrin.lib.demo.widget;

import android.os.Bundle;
import android.view.View;

import cn.sskbskdrin.base.IFragment;
import cn.sskbskdrin.lib.demo.R;
import cn.sskbskdrin.lib.demo.Utils;
import cn.sskbskdrin.lib.demo.simple.SimpleAdapter;
import cn.sskbskdrin.widget.BannerIndicatorView;
import cn.sskbskdrin.widget.BannerView;

/**
 * Created by keayuan on 2020/4/8.
 *
 * @author keayuan
 */
public class BannerFragment extends IFragment {
    @Override
    protected int getLayoutId() {
        return R.layout.fr_banner;
    }

    @Override
    protected void onInitView(View rootView,  Bundle savedInstanceState) {
        BannerView bannerView = getView(R.id.banner);
        bannerView.setAdapter(new SimpleAdapter<>(Utils.getSimpleList(6)));
        BannerIndicatorView indicatorView = getView(R.id.indicator);
        indicatorView.setCount(6);
        bannerView.setOnScrollListener(indicatorView);
        bannerView.setOnItemClickListener((parent, view, position) -> showToast("click " + position));
    }
}
