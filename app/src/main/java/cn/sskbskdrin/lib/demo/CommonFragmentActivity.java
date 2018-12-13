package cn.sskbskdrin.lib.demo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;

import cn.sskbskdrin.base.BaseActivity;

public class CommonFragmentActivity extends BaseActivity {
    private List<Fragment> mFragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        mFragments = new ArrayList<>();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String name = bundle.getString("fragment");
            try {
                Fragment fragment = (Fragment) Class.forName(name).newInstance();
                replaceFragment(fragment);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void replaceFragment(Fragment fragment) {
        mFragments.add(fragment);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
        transaction.replace(R.id.activity_fragment_content, fragment).commit();
        // 根据当前fragment页面改变菜单按钮功能
    }

    private void forwardFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
        transaction.replace(R.id.activity_fragment_content, getCurrentFragment()).commit();
        // 根据当前fragment页面改变菜单按钮功能
    }

    private Fragment getCurrentFragment() {
        return mFragments.get(mFragments.size() - 1);
    }

    @Override
    public void onBackPressed() {
        if (mFragments.size() > 1) {
            mFragments.remove(mFragments.size() - 1);
            forwardFragment();
            return;
        }
        super.onBackPressed();
    }

}
