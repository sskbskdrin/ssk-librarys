package cn.sskbskdrin.http;

public interface IProgress {
    /**
     * 下载文件时，进度回调
     *
     * @param progress 下载进度[0,1]
     */
    void progress(float progress);
}
