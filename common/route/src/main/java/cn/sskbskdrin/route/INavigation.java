package cn.sskbskdrin.route;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.launcher.ARouter;

/**
 * Created by keayuan on 2021/8/12.
 *
 * @author keayuan
 */
public interface INavigation {
    default Postcard na(String path) {
        return ARouter.getInstance().build(path);
    }
}
