package cn.sskbskdrin.http;

import java.io.File;

public interface IRequestCallback {

    /**
     * 网络请求结果回调
     *
     * @param data 返回数据
     */
    void onResponseData(byte[] data);

    /**
     * 下载文件时，下载成功回调
     *
     * @param file 下载保存的文件
     */
    void onResponseFile(File file);

    /**
     * 下载时回调的进度
     *
     * @param progress 下载进度
     */
    void onProgress(float progress);

    /**
     * 请求发生错误时回调
     *
     * @param code 错误码
     * @param desc 错误说明
     * @param e    发生的异常
     */
    void onError(final String code, final String desc, final Exception e);
}
