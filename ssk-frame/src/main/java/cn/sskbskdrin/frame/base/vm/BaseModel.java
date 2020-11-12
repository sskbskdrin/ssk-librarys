package cn.sskbskdrin.frame.base.vm;

import androidx.lifecycle.LiveData;

/**
 * Created by keayuan on 2020/4/3.
 *
 * @author keayuan
 */
public class BaseModel<T> extends LiveData<T> {

    public BaseModel() {
    }

    public BaseModel(T value) {
        super(value);
    }

    @Override
    public void postValue(T value) {
        super.postValue(value);
    }

}
