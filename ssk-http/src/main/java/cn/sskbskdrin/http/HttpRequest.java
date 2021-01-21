package cn.sskbskdrin.http;

import android.util.Log;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by sskbskdrin on 2020/8/20.
 *
 * @author sskbskdrin
 */
class HttpRequest<V> implements IRequest<V>, IRequestBody {
    private static final String TAG = "HttpRequest";

    private final HashMap<String, String> mHeader = new HashMap<>();
    private final HashMap<String, Object> mParams = new HashMap<>();

    private String mContentType;

    private IMap<String, String> iHeader;
    private IMap<String, Object> iParams;

    private ICallback<IRequest<V>> mPreRequest;
    private IParseResponse<V> mParseResponse;
    private ICallback<Float> mProgress;
    private ICallback2<V, IParseResult<V>> mSuccess;
    private ICallback2<V, IParseResult<V>> mSuccessIO;
    private ICallback3<String, String, Throwable> mError;
    private ICallback<IRequest<V>> mComplete;

    private String mTag;
    private final String mUrl;
    private long readTimeout = -1;
    private long connectedTimeout = -1;

    private final AtomicBoolean isCancel = new AtomicBoolean(false);
    private Type mType;

    private IRealRequest realRequest;
    private static int mId = 0;

    HttpRequest(String url, Type type) {
        this(url, type, CONTENT_TYPE_GET);
    }

    HttpRequest(String url, IParseResponse<V> iParseResponse) {
        this(url, iParseResponse, CONTENT_TYPE_GET);
    }

    HttpRequest(String url, IParseResponse<V> iParse, String contentType) {
        mUrl = Config.fixUrl(url);
        mParseResponse = iParse;
        mContentType = contentType;
        if (iParse != null) {
            mType = ((ParameterizedType) iParse.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
        }
    }

    HttpRequest(String url, Type type, String contentType) {
        mUrl = Config.fixUrl(url);
        mType = type;
        mContentType = contentType;
    }

    @Override
    public IRequest<V> addHeader(String key, String value) {
        mHeader.put(key, value);
        return this;
    }

    @Override
    public IRequest<V> headers(IMap<String, String> iMap) {
        iHeader = iMap;
        return this;
    }

    @Override
    public IRequest<V> addParams(String key, Object value) {
        mParams.put(key, value);
        return this;
    }

    @Override
    public IRequest<V> params(IMap<String, Object> iMap) {
        iParams = iMap;
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
    public IRequest<V> tag(String tag) {
        mTag = tag;
        return this;
    }

    @Override
    public IRequest<V> pre(ICallback<IRequest<V>> request) {
        mPreRequest = request;
        return this;
    }

    @Override
    public IRequest<V> parseResponse(IParseResponse<V> parse) {
        mParseResponse = parse;
        return this;
    }

    @Override
    public IRequest<V> progress(ICallback<Float> progress) {
        mProgress = progress;
        return this;
    }

    @Override
    public IRequest<V> success(ICallback2<V, IParseResult<V>> success) {
        mSuccess = success;
        return this;
    }

    @Override
    public IRequest<V> successIO(ICallback2<V, IParseResult<V>> success) {
        mSuccessIO = success;
        return this;
    }

    @Override
    public IRequest<V> error(ICallback3<String, String, Throwable> error) {
        mError = error;
        return this;
    }

    @Override
    public IRequest<V> complete(ICallback<IRequest<V>> complete) {
        mComplete = complete;
        return this;
    }

    @Override
    public IResponse requestSync() throws Exception {
        return request(getConfig().getRealRequest());
    }

    @Override
    public IRequest<V> request() {
        if (mTag == null) {
            mTag = "tag-" + mId++;
        }
        realRequest = getConfig().getRealRequest();
        if (realRequest == null) {
            error(ERROR_REAL_REQUEST, "没找到请求实现类，请先设置请求工厂",
                new IllegalAccessException("IRealRequestFactory " + "not " + "impl or IRealRequest is null"));
            return this;
        }

        getConfig().execute(new Runnable() {
            @Override
            public void run() {
                if (isCancel.get()) {
                    return;
                }
                preRequest();
                IResponse res;
                try {
                    res = request(realRequest);
                } catch (Exception e) {
                    error(ERROR_CONNECT, e.getMessage(), e);
                    return;
                }
                if (res == null) {
                    error(ERROR_CONNECT, "response is null", null);
                    return;
                }
                if (!res.isSuccess()) {
                    error(res.code() + "", res.message(), res.throwable());
                } else {
                    IParseResult<V> parseResult = null;
                    if (mParseResponse != null) {
                        try {
                            log("parse: ");
                            parseResult = mParseResponse.parse(mTag, res, mType, HttpRequest.this);
                        } catch (Throwable throwable) {
                            error(ERROR_PARSE, "解析错误", throwable);
                        }
                    }
                    if (parseResult == null) {
                        error(ERROR_PARSE, "解析结果为空", null);
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
                Platform.safeClose(res);
            }
        });
        return this;
    }

    private void preRequest() {
        if (isCancel.get()) return;
        log("pre: ");
        if (mPreRequest != null) {
            Platform.get().callback(new Runnable() {
                @Override
                public void run() {
                    if (isCancel.get()) return;
                    if (mPreRequest != null) {
                        mPreRequest.onCallback(mTag, HttpRequest.this);
                    }
                }
            });
        }
    }

    private void error(final String code, final String msg, final Throwable throwable) {
        if (isCancel.get()) return;
        Log.w(TAG, "error: " + code + " " + msg, throwable);
        if (mError != null) {
            Platform.get().callback(new Runnable() {
                @Override
                public void run() {
                    if (isCancel.get()) return;
                    if (mError != null) {
                        mError.onCallback(mTag, code, msg, throwable);
                    }
                }
            });
        }
        complete();
    }

    private void success(final V v, final IParseResult<V> parseResult) {
        if (isCancel.get()) return;
        log("success: ");
        if (mSuccess != null) {
            Platform.get().callback(new Runnable() {
                @Override
                public void run() {
                    if (isCancel.get()) return;
                    if (mSuccess != null) {
                        mSuccess.onCallback(mTag, v, parseResult);
                    }
                }
            });
        }
        complete();
    }

    private void complete() {
        if (isCancel.get()) return;
        log("complete: ");
        if (mComplete != null) {
            Platform.get().callback(new Runnable() {
                @Override
                public void run() {
                    if (isCancel.get()) return;
                    if (mComplete != null) {
                        mComplete.onCallback(mTag, HttpRequest.this);
                    }
                }
            });
        }
    }

    private Config getConfig() {
        return Config.INSTANCE;
    }

    private IResponse request(IRealRequest request) throws Exception {
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
                return request.postFile(this);
        }
        return null;
    }

    @Override
    public void close() {
        if (isCancel.get()) return;
        isCancel.set(true);
        Log.w(TAG, mTag + " cancel: ");

        iHeader = null;
        iParams = null;
        mPreRequest = null;
        mParseResponse = null;
        mProgress = null;
        mSuccess = null;
        mSuccessIO = null;
        mError = null;
        mComplete = null;
        Platform.safeClose(realRequest);
        realRequest = null;
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

    @Override
    public void publishProgress(final float progress) {
        log("publishProgress: " + progress);
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

    private void log(String content) {
        if (getConfig().isOpenLog()) {
            Log.d(TAG, mTag + " " + content);
        }
    }
}
