package cn.sskbskdrin.http;

import java.io.File;

public interface IResponseCallback<V> {

    void onResponseData(byte[] data);

    void onResponseFile(File file);

    void onProgress(float progress);

    void onError(final String code, final String desc, final Exception e);
}
