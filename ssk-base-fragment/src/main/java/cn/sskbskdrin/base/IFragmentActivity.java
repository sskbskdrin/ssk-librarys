package cn.sskbskdrin.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;

import androidx.fragment.app.FragmentActivity;

/**
 * @author keayuan
 *  2020/3/27
 */
@SuppressLint("Registered")
public class IFragmentActivity extends FragmentActivity implements IA, IWindow, IView, IPermission {

    @Override
    public <T extends View> T getView(int id) {
        return findViewById(id);
    }

    @Override
    public Context context() {
        return this;
    }

    @Override
    public boolean isFinish() {
        return isFinishing();
    }
}
