package cn.sskbskdrin.lib.demo;

import android.content.Context;
import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import cn.sskbskdrin.base.IA;
import cn.sskbskdrin.base.IFragment;

@Route(path = "/main/common")
public class CommonFragmentActivity extends AppCompatActivity implements IA {
    private List<IFragment> mFragments;

    @Autowired
     int aa;

    @Autowired
    String ab;

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public boolean isFinish() {
        return isFinishing();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        ARouter.getInstance().inject(this);
        mFragments = new ArrayList<>();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String name = bundle.getString("fragment");
            try {
                IFragment fragment = (IFragment) Class.forName(name).newInstance();
                replaceFragment(fragment);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        postDelayed(() -> new Work().en(), 1000);
    }

    public void replaceFragment(IFragment fragment) {
        mFragments.add(fragment);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
        transaction.replace(R.id.activity_fragment_content, fragment).commit();
    }

    private void forwardFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
        transaction.replace(R.id.activity_fragment_content, getCurrentFragment()).commit();
    }

    private IFragment getCurrentFragment() {
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
