package cn.sskbskdrin.http;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import cn.sskbskdrin.http.impl.UrlRequest;

/**
 * Created by keayuan on 2019-11-29.
 *
 * @author keayuan
 */
class HttpRequest<V> implements IRequest<V>, IResponseCallback<V>, IRequestBody {

    static Executor executor = new ThreadPoolExecutor(2, 10, 10, TimeUnit.SECONDS,
        new LinkedBlockingQueue<Runnable>(), new DefaultThreadFactory());

    private static final HashMap<String, String> globalHeader = new HashMap<>();
    private static IMap<String, String> iMapGlobalHeader;

    private static IRealRequestFactory mRealRequestFactory;
    private Class<?> responseClazz;

    private final HashMap<String, String> mHeader = new HashMap<>();
    private final HashMap<String, String> mParams = new HashMap<>();
    private final HashMap<String, File> mFileParams = new HashMap<>();

    private String mContentType;

    private IMap<String, String> header;
    private IMap<String, String> params;

    private IPreRequest mPreRequest;
    private IResponse<V> mResponse;
    private IParseResponse<V> mParseResponse;
    private IProgress mProgress;
    private ISuccess<V> mSuccess;
    private IError mError;
    private IComplete mComplete;

    private String mFilePath;

    private String mTag;
    private String mUrl;
    private long readTimeout = 15000;
    private long connectedTimeout = 15000;

    private AtomicBoolean isCancel = new AtomicBoolean(false);

    public HttpRequest(String url) {
        mUrl = url;
    }

    @Override
    public IRequest<V> get() {
        mContentType = CONTENT_TYPE_GET;
        return this;
    }

    @Override
    public IRequest<V> post() {
        mContentType = CONTENT_TYPE_FORM;
        return this;
    }

    @Override
    public IRequest<V> postJson() {
        mContentType = CONTENT_TYPE_JSON;
        return this;
    }

    @Override
    public IRequest<V> postFile() {
        mContentType = CONTENT_TYPE_MULTIPART;
        return this;
    }

    @Override
    public IRequest<V> download(String filePath) {
        mContentType = CONTENT_TYPE_DOWN;
        mFilePath = filePath;
        if (mFilePath == null) {
            throw new IllegalArgumentException("filepath is null");
        }
        return this;
    }

    @Override
    public IRequest<V> addHeader(String key, String value) {
        mHeader.put(key, value);
        return this;
    }

    @Override
    public IRequest<V> headers(IMap<String, String> iMap) {
        header = iMap;
        return this;
    }

    @Override
    public IRequest<V> addParams(String key, String value) {
        mParams.put(key, value);
        return this;
    }

    @Override
    public IRequest<V> addParams(String key, File value) {
        mFileParams.put(key, value);
        return this;
    }

    @Override
    public IRequest<V> params(IMap<String, String> iMap) {
        params = iMap;
        return this;
    }

    @Override
    public IRequest<V> connectedTimeout(long ms) {
        connectedTimeout = ms;
        return this;
    }

    @Override
    public IRequest<V> readTimeout(long ms) {
        readTimeout = ms;
        return this;
    }

    @Override
    public IRequest<V> response(IResponse<V> response) {
        mResponse = response;
        return this;
    }

    @Override
    public IRequest<V> tag(String tag) {
        mTag = tag;
        return this;
    }

    @Override
    public IRequest<V> pre(IPreRequest request) {
        mPreRequest = request;
        return this;
    }

    @Override
    public IRequest<V> parseResponse(IParseResponse<V> parse) {
        mParseResponse = parse;
        return this;
    }

    @Override
    public IRequest<V> parseResponse(Class<?> clazz) {
        responseClazz = clazz;
        return this;
    }

    @Override
    public IRequest<V> progress(IProgress progress) {
        mProgress = progress;
        return this;
    }

    @Override
    public IRequest<V> success(ISuccess<V> success) {
        mSuccess = success;
        return this;
    }

    @Override
    public IRequest<V> error(IError error) {
        mError = error;
        return this;
    }

    @Override
    public IRequest<V> complete(IComplete complete) {
        mComplete = complete;
        return this;
    }

    @Override
    public void request() {
        IRealRequest<V> realRequest;
        if (mRealRequestFactory == null) {
            mRealRequestFactory = new IRealRequestFactory() {
                @Override
                public <T> IRealRequest<T> generateRealRequest() {
                    return new UrlRequest<>(true);
                }
            };
        }
        realRequest = mRealRequestFactory.generateRealRequest();
        if (realRequest == null) {
            onError("-1", "没找到请求实现类，请先设置请求工厂", new IllegalAccessException("IRealRequestFactory not impl"));
            return;
        }
        final IRealRequest<V> request = realRequest;
        Platform.get().callback(new Runnable() {
            @Override
            public void run() {
                if (mPreRequest != null) {
                    mPreRequest.onPreRequest(mTag);
                }
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        request(request);
                    }
                });
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void request(IRealRequest<V> request) {
        if (!getHeader().containsKey("Content-Type")) {
            addHeader("Content-Type", mContentType);
        }
        switch (mContentType) {
            case CONTENT_TYPE_GET:
                request.get(this, this);
                break;
            case CONTENT_TYPE_FORM:
                request.post(this, this);
                break;
            case CONTENT_TYPE_JSON:
                request.postJson(this, this);
                break;
            case CONTENT_TYPE_MULTIPART:
                request.postFile(this, this);
                break;
            case CONTENT_TYPE_DOWN:
                request.download(this, mFilePath, (IResponseCallback<File>) this);
                break;
        }
    }

    @Override
    public void request(Callback<V> callback) {
        mSuccess = callback;
        mError = callback;
        mComplete = callback;
        request();
    }

    @Override
    public void cancel() {
        isCancel.set(true);
    }

    public static void putGlobalHeader(String key, String value) {
        globalHeader.put(key, value);
    }

    public static void globalHeader(IMap<String, String> global) {
        iMapGlobalHeader = global;
    }

    public static void setRealRequestFactory(IRealRequestFactory factory) {
        mRealRequestFactory = factory;
    }

    @Override
    public void onResponseData(byte[] data) {
        if (isCancel.get()) return;
        parse(data, null);
    }

    @Override
    public void onResponseFile(File file) {
        if (isCancel.get()) return;
        parse(null, file);
    }

    private void parse(byte[] data, File file) {
        Response<V> response = null;
        if (mResponse != null) {
            response = mResponse.generate();
        }
        if (response == null) {
            response = new Response<>();
        }

        if (file != null) {
            response.setBean((V) file);
            responseClazz = File.class;
            response.isSuccess = true;
        } else {
            try {
                response.bodyData = data;
                if (response.parse(mTag, data, responseClazz)) {
                    if (mParseResponse != null && response.isSuccess()) {
                        mParseResponse.parse(mTag, response, responseClazz);
                    }
                } else {
                    Platform.get().callback(new Runnable() {
                        @Override
                        public void run() {
                            complete();
                        }
                    });
                    return;
                }
            } catch (Exception e) {
                onError("-2", "解析错误", e);
                return;
            }
        }
        success(response);
    }

    @Override
    public void onProgress(final float progress) {
        if (isCancel.get()) return;
        if (mProgress != null) {
            Platform.get().callback(new Runnable() {
                @Override
                public void run() {
                    if (mProgress != null) {
                        mProgress.progress(progress);
                    }
                }
            });
        }
    }

    private void success(final Response<V> response) {
        if (isCancel.get()) return;
        if (response.isSuccess()) {
            Platform.get().callback(new Runnable() {
                @Override
                public void run() {
                    if (mSuccess != null) {
                        mSuccess.success(mTag, response.getBean(), response);
                    }
                    complete();
                }
            });
        } else {
            onError(response.code(), response.msg(), response.getException());
        }
    }

    @Override
    public void onError(final String code, final String desc, final Exception e) {
        if (isCancel.get()) return;
        Platform.get().callback(new Runnable() {
            @Override
            public void run() {
                if (mError != null) {
                    mError.error(mTag, code, desc, e);
                }
                complete();
            }
        });
    }

    private void complete() {
        if (mComplete != null) {
            mComplete.complete(mTag);
        }
    }

    @Override
    public String getUrl() {
        if (CONTENT_TYPE_GET.equalsIgnoreCase(mContentType)) {
            return mUrl + parseParams();
        }
        return mUrl;
    }

    @Override
    public HashMap<String, String> getParams() {
        if (params != null) {
            params.apply(mParams);
        }
        return mParams;
    }

    @Override
    public HashMap<String, File> getFileParams() {
        return mFileParams;
    }

    @Override
    public HashMap<String, String> getHeader() {
        if (header != null) {
            header.apply(mHeader);
        }
        if (iMapGlobalHeader != null) {
            iMapGlobalHeader.apply(mHeader);
        }
        HashMap<String, String> map = new HashMap<>(mHeader.size() + globalHeader.size());
        map.putAll(mHeader);
        map.putAll(globalHeader);
        return map;
    }

    @Override
    public long getReadTimeout() {
        return readTimeout;
    }

    @Override
    public long getConnectedTimeout() {
        return connectedTimeout;
    }

    private String parseParams() {
        HashMap<String, String> map = getParams();
        if (map == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            sb.append(entry.getKey());
            sb.append('=');
            sb.append(entry.getValue());
            sb.append('&');
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }

    private static class DefaultThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        DefaultThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
            namePrefix = "HttpThreadPool-" + poolNumber.getAndIncrement();
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            if (t.getPriority() != Thread.NORM_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }
            return t;
        }
    }
}
