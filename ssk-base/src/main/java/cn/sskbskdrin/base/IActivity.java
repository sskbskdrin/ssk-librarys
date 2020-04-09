package cn.sskbskdrin.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.View;

/**
 * @author keayuan
 */
@SuppressLint("Registered")
public class IActivity extends Activity implements IA {

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
