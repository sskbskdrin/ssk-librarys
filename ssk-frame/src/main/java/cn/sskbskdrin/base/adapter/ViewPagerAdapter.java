package cn.sskbskdrin.base.adapter;

import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.viewpager.widget.PagerAdapter;

/**
 * 一般的viewpager的适配器
 * Created by Czy on 2017/2/22.
 */

public class ViewPagerAdapter extends PagerAdapter {

    private List<? extends View> viewArrayList;

    //PagerTitle
    private List<String> tittleArrayList;

    public ViewPagerAdapter(List<? extends View> viewArrayList) {
        this.viewArrayList = viewArrayList;
    }

    public ViewPagerAdapter(List<? extends View> viewArrayList, List<String> tittleArrayList) {
        this.viewArrayList = viewArrayList;
        this.tittleArrayList = tittleArrayList;
    }

    public void destroyItem(ViewGroup container, int position, Object object) {
        try {
            container.removeView(viewArrayList.get(position));// 删除页卡
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getCount() {
        return null == viewArrayList ? 0 : viewArrayList.size();
    }

    // 这个方法用来实例化页卡
    public Object instantiateItem(ViewGroup container, int position) {
        try {
            container.addView(viewArrayList.get(position));// 添加页卡
        } catch (Exception e) {
            e.printStackTrace();
        }
        return viewArrayList.get(position);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tittleArrayList.get(position);
    }

    public List<? extends View> getViewArrayList() {
        if (viewArrayList == null) {
            return new ArrayList<>();
        }
        return viewArrayList;
    }

    public View getItem(int position) {
        if (viewArrayList == null || position >= viewArrayList.size() || position < 0) return null;
        return viewArrayList.get(position);
    }
}
