package cn.sskbskdrin.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ex-keayuan001 on 2018/5/24.
 *
 * @author ex-keayuan001
 */
public class TabHostWidget extends LinearLayout {
    private static final String TAG = "TabHostWidget";

    private Pager mViewPager;
    private PagerAdapter mPagerAdapter;
    private TabWidget mTabWidget;

    public TabHostWidget(Context context) {
        this(context, null);
    }

    public TabHostWidget(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabHostWidget(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TabHostWidget(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        mViewPager = new Pager(getContext());
        mViewPager.setId(ViewCompat.generateViewId());
        mViewPager.setOffscreenPageLimit(5);
        addView(mViewPager, new LayoutParams(LayoutParams.MATCH_PARENT, 0, 1));
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                int current = mViewPager.getCurrentItem();
                if (position >= current) {
                    current += position - current;
                } else if (position < current) {
                    current += position - current + 1;
                }
                mTabWidget.offsetTab(current, position + positionOffset - current);
            }

            @Override
            public void onPageSelected(int position) {
            }
        });
        mTabWidget = new TabWidget(getContext());
        addView(mTabWidget, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        mTabWidget.addOnTabChangeListener(new TabWidget.OnTabChangeListener() {
            @Override
            public void onTabChange(int old, int newPosition) {
                if (mViewPager.getCurrentItem() != newPosition) {
                    mViewPager.setCurrentItem(newPosition, false);
                }
            }
        });
    }

    public void addOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        mViewPager.addOnPageChangeListener(listener);
    }

    public void setTabWidgetPosition(boolean isTop) {
        if (isTop) {
            if (getChildAt(0) != mTabWidget) {
                removeView(mTabWidget);
                addView(mTabWidget, 0);
            }
        } else {
            if (getChildAt(0) == mTabWidget) {
                removeView(mTabWidget);
                addView(mTabWidget);
            }
        }
    }

    public TabWidget getTabWidget() {
        return mTabWidget;
    }

    public void setPager(FragmentManager fm, List<Class<? extends Fragment>> list) {
        mViewPager.removeAllViews();
        mPagerAdapter = new Adapter(fm, list);
        mViewPager.setAdapter(mPagerAdapter);
    }

    public void enablePagerScroll(boolean enable) {
        mViewPager.enableScroll(enable);
    }

    public void setPagerAdapter(PagerAdapter adapter) {
        if (adapter != null) {
            mPagerAdapter = adapter;
        }
        mViewPager.setAdapter(mPagerAdapter);
    }

    private static class Pager extends ViewPager {

        private boolean mEnableScroll = true;

        public Pager(@NonNull Context context) {
            super(context);
        }

        private void enableScroll(boolean enable) {
            mEnableScroll = enable;
        }

        private boolean getEnableScroll() {
            return mEnableScroll;
        }

        @Override
        public boolean onTouchEvent(MotionEvent ev) {
            return mEnableScroll && super.onTouchEvent(ev);
        }
    }

    private static class Adapter extends FragmentPagerAdapter {
        private List<Class<? extends Fragment>> mList;
        private SparseArray<Fragment> mFragments;

        public Adapter(FragmentManager fm, List<Class<? extends Fragment>> list) {
            super(fm);
            mList = list;
            if (mList == null) {
                mList = new ArrayList<>();
            }
            mFragments = new SparseArray<>(list.size());
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = mFragments.get(position);
            if (fragment == null) {
                try {
                    fragment = mList.get(position).newInstance();
                    mFragments.append(position, fragment);
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return mList.size();
        }
    }
}
