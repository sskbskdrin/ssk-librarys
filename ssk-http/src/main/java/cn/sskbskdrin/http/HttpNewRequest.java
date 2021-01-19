package cn.sskbskdrin.http;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by sskbskdrin on 2020/8/20.
 *
 * @author sskbskdrin
 */
class HttpNewRequest<V> implements INewRequest<V>, IRequestBody {
    private static final String TAG = "HttpRequest";

    private final HashMap<String, String> mHeader = new HashMap<>();
    private final HashMap<String, Object> mParams = new HashMap<>();

    private String mContentType;

    private IMap<String, String> iHeader;
    private IMap<String, Object> iParams;

    private ICallback<String> mPreRequest;
    private IParseResponse<V> mParseResponse;
    private ICallback<Float> mProgress;
    private ICallback2<V, IParseResult<V>> mSuccess;
    private ICallback2<V, IParseResult<V>> mSuccessIO;
    private ICallback<Error> mError;
    private ICallback<String> mComplete;

    private String mFilePath;

    private String mTag;
    private final String mUrl;
    private long readTimeout = -1;
    private long connectedTimeout = -1;

    private final AtomicBoolean isCancel = new AtomicBoolean(false);
    private Type mType;

    private final IProgress inProgress = new IProgress() {
        @Override
        public void progress(final float progress) {
            if (mProgress != null && !isCancel.get()) {
                Platform.get().callback(new Runnable() {
                    @Override
                    public void run() {
                        if (mProgress != null) {
                            mProgress.onCallback(mTag, ((int) (progress * 100)) / 100f);
                        }
                    }
                });
            } else {
                mProgress = null;
            }
        }
    };

    HttpNewRequest(String url, Type type) {
        mUrl = Config.fixUrl(url);
        mType = type;
    }

    HttpNewRequest(String url, IParseResponse<V> iParseResponse) {
        mUrl = Config.fixUrl(url);
        parseResponse(iParseResponse);
    }

    @Override
    public void get() {
        mContentType = CONTENT_TYPE_GET;
        request();
    }

    @Override
    public IResponse getSync() {
        mContentType = CONTENT_TYPE_GET;
        return request(getConfig().getRealRequest());
    }

    @Override
    public void post() {
        mContentType = CONTENT_TYPE_FORM;
        request();
    }

    @Override
    public IResponse postSync() {
        mContentType = CONTENT_TYPE_FORM;
        return request(getConfig().getRealRequest());
    }

    @Override
    public void postJson() {
        mContentType = CONTENT_TYPE_JSON;
        request();
    }

    @Override
    public IResponse postJsonSync() {
        mContentType = CONTENT_TYPE_JSON;
        return request(getConfig().getRealRequest());
    }

    @Override
    public void postFile() {
        mContentType = CONTENT_TYPE_MULTIPART;
        request();
    }

    @Override
    public IResponse postFileSync() {
        mContentType = CONTENT_TYPE_MULTIPART;
        return request(getConfig().getRealRequest());
    }

    @Override
    public void download(String filePath) {
        mContentType = CONTENT_TYPE_DOWN;
        mFilePath = filePath;
        if (mFilePath == null) {
            throw new IllegalArgumentException("filepath is null");
        }
        request();
    }

    @Override
    public INewRequest<V> addHeader(String key, String value) {
        mHeader.put(key, value);
        return this;
    }

    @Override
    public INewRequest<V> headers(IMap<String, String> iMap) {
        iHeader = iMap;
        return this;
    }

    @Override
    public INewRequest<V> addParams(String key, Object value) {
        mParams.put(key, value);
        return this;
    }

    @Override
    public INewRequest<V> params(IMap<String, Object> iMap) {
        iParams = iMap;
        return this;
    }

    @Override
    public INewRequest<V> connectedTimeout(long ms) {
        connectedTimeout = ms;
        return this;
    }

    @Override
    public INewRequest<V> readTimeout(long ms) {
        readTimeout = ms;
        return this;
    }

    @Override
    public INewRequest<V> tag(String tag) {
        mTag = tag;
        return this;
    }

    @Override
    public INewRequest<V> pre(ICallback<String> request) {
        mPreRequest = request;
        return this;
    }

    @Override
    public INewRequest<V> parseResponse(IParseResponse<V> parse) {
        mParseResponse = parse;
        return this;
    }

    @Override
    public INewRequest<V> progress(ICallback<Float> progress) {
        mProgress = progress;
        return this;
    }

    @Override
    public INewRequest<V> success(ICallback2<V, IParseResult<V>> success) {
        mSuccess = success;
        return this;
    }

    @Override
    public INewRequest<V> successIO(ICallback2<V, IParseResult<V>> success) {
        mSuccessIO = success;
        return this;
    }

    @Override
    public INewRequest<V> error(ICallback<Error> error) {
        mError = error;
        return this;
    }

    @Override
    public INewRequest<V> complete(ICallback<String> complete) {
        mComplete = complete;
        return this;
    }

    private void request() {
        final IRealRequest realRequest = getConfig().getRealRequest();
        if (realRequest == null) {
            error(ERROR_REAL_REQUEST, "没找到请求实现类，请先设置请求工厂",
                new IllegalAccessException("IRealRequestFactory " + "not " + "impl or IRealRequest is null"));
            return;
        }
        if (mPreRequest != null) {
            Platform.get().callback(new Runnable() {
                @Override
                public void run() {
                    mPreRequest.onCallback(mTag, "pre");
                }
            });
        }
        getConfig().execute(new Runnable() {
            @Override
            public void run() {
                if (isCancel.get()) {
                    complete();
                    return;
                }
                IResponse res = request(realRequest);
                if (res == null) {
                    error(ERROR_UNKNOWN, "unknown", null);
                } else if (!res.isSuccess()) {
                    error(res.code() + "", res.message(), res.exception());
                } else {
                    IParseResult<V> parseResult = null;
                    if (mParseResponse != null) {
                        parseResult = mParseResponse.parse(mTag, res, mType);
                    }
                    if (parseResult == null) {
                        error(ERROR_PARSE, "解析错误", null);
                    } else if (parseResult.isCancel()) {
                        complete();
                    } else if (parseResult.isSuccess()) {
                        if (mSuccessIO != null) {
                            mSuccessIO.onCallback(mTag, parseResult.getT(), parseResult);
                        }
                        success(parseResult.getT(), parseResult);
                    } else {
                        error(ERROR_PARSE, "解析失败", null);
                    }
                }
            }
        });
    }

    private void complete() {
        if (mComplete != null) {
            Platform.get().callback(new Runnable() {
                @Override
                public void run() {
                    mComplete.onCallback(mTag, "complete");
                }
            });
        }
    }


    private void error(final String code, final String msg, final Throwable throwable) {
        if (mError != null) {
            Platform.get().callback(new Runnable() {
                @Override
                public void run() {
                    if (mError != null) {
                        mError.onCallback(mTag, new Error(code, msg, throwable));
                    }
                    complete();
                }
            });
        }
    }

    private void success(final V v, final IParseResult<V> parseResult) {
        if (mSuccess != null) {
            Platform.get().callback(new Runnable() {
                @Override
                public void run() {
                    if (mSuccess != null) {
                        mSuccess.onCallback(mTag, v, parseResult);
                    }
                    complete();
                }
            });
        }
    }

    private Config getConfig() {
        return Config.INSTANCE;
    }

    private IResponse request(IRealRequest request) {
        if (request == null) {
            error(ERROR_REAL_REQUEST, "没找到请求实现类，请先设置请求工厂",
                new IllegalAccessException("IRealRequestFactory " + "not " + "impl or IRealRequest is null"));
            return null;
        }
        if (!getHeader().containsKey("Content-Type")) {
            addHeader("Content-Type", mContentType);
        }
        switch (mContentType) {
            case CONTENT_TYPE_GET:
                return request.get(this);
            case CONTENT_TYPE_FORM:
                return request.post(this);
            case CONTENT_TYPE_JSON:
                return request.postJson(this);
            case CONTENT_TYPE_MULTIPART:
                return request.postFile(this, inProgress);
            case CONTENT_TYPE_DOWN:
                return request.download(this, mFilePath, inProgress);
        }
        return null;
    }

    @Override
    public void cancel() {
        isCancel.set(true);
    }

    @Override
    public String getUrl() {
        if (CONTENT_TYPE_GET.equalsIgnoreCase(mContentType)) {
            return mUrl + parseParams();
        }
        return mUrl;
    }

    @Override
    public HashMap<String, Object> getParams() {
        if (iParams != null) {
            iParams.apply(mParams);
        }
        return mParams;
    }

    @Override
    public HashMap<String, String> getHeader() {
        getConfig().applyHeader(mHeader);
        if (iHeader != null) {
            iHeader.apply(mHeader);
        }
        return new HashMap<>(mHeader);
    }

    @Override
    public long getReadTimeout() {
        return readTimeout < 0 ? getConfig().readTimeout : readTimeout;
    }

    @Override
    public long getConnectedTimeout() {
        return connectedTimeout < 0 ? getConfig().connectedTimeout : connectedTimeout;
    }

    private String parseParams() {
        HashMap<String, Object> map = getParams();
        if (map == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
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
}
