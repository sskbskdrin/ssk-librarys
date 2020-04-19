package cn.sskbskdrin.http;

import java.io.File;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by keayuan on 2019-11-29.
 *
 * @author keayuan
 */
final class HttpRequest<V> implements IRequest<V>, IRequestCallback, IRequestBody {

    private final HashMap<String, String> mHeader = new HashMap<>();
    private final HashMap<String, Object> mParams = new HashMap<>();

    private String mContentType;

    private IMap<String, String> iHeader;
    private IMap<String, Object> iParams;

    private IPreRequest mPreRequest;
    private IParseResponse<V> mParseResponse;
    private IProgress mProgress;
    private ISuccess<V> mSuccess;
    private ISuccess<V> mSuccessIO;
    private IError mError;
    private IComplete mComplete;

    private String mFilePath;

    private String mTag;
    private String mUrl;
    private long readTimeout = -1;
    private long connectedTimeout = -1;

    private AtomicBoolean isContinue = new AtomicBoolean(true);
    private Type mType;

    HttpRequest(String url, Type type) {
        mUrl = Config.fixUrl(url);
        mType = type;
    }

    @Override
    public void get() {
        mContentType = CONTENT_TYPE_GET;
        request();
    }

    @Override
    public void post() {
        mContentType = CONTENT_TYPE_FORM;
        request();
    }

    @Override
    public void postJson() {
        mContentType = CONTENT_TYPE_JSON;
        request();
    }

    @Override
    public void postFile() {
        mContentType = CONTENT_TYPE_MULTIPART;
        request();
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
    public IRequest<V> successIO(ISuccess<V> success) {
        mSuccessIO = success;
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

    private void request() {
        IRealRequest realRequest = getConfig().getRealRequest();
        if (realRequest == null) {
            onError(ERROR_REAL_REQUEST, "没找到请求实现类，请先设置请求工厂", new IllegalAccessException("IRealRequestFactory not " +
                "impl or IRealRequest is null"));
            return;
        }
        final IRealRequest request = realRequest;
        Platform.get().callback(new Runnable() {
            @Override
            public void run() {
                if (mPreRequest != null) {
                    mPreRequest.onPreRequest(mTag);
                }
                getConfig().execute(new Runnable() {
                    @Override
                    public void run() {
                        request(request);
                    }
                });
            }
        });
    }

    private Config getConfig() {
        return Config.INSTANCE;
    }

    private void request(IRealRequest request) {
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
                request.download(this, mFilePath, this);
                break;
        }
    }

    @Override
    public void cancel() {
        isContinue.set(false);
    }

    @Override
    public void onResponseData(byte[] data) {
        if (isNotCancel()) {
            IResponse<V> response = new Response<>(data);
            IParseResult<V> parseResult;
            try {
                if (mParseResponse != null) {
                    parseResult = mParseResponse.parse(mTag, response, mType);
                } else {
                    parseResult = getConfig().parse(mTag, response, mType);
                }
                if (parseResult == null) {
                    onError(ERROR_NO_PARSE, response.string(), null);
                    return;
                } else if (parseResult.isCancel()) {
                    complete();
                    return;
                }
            } catch (Exception e) {
                onError(ERROR_PARSE, "解析错误", e);
                return;
            }
            success(parseResult, response);
        }
    }

    @Override
    public void onResponseFile(File file) {
        if (isNotCancel()) {
            success(new Result<>(true, (V) file), null);
        }
    }

    @Override
    public void onProgress(final float progress) {
        if (isNotCancel() && mProgress != null) {
            Platform.get().callback(new Runnable() {
                @Override
                public void run() {
                    if (mProgress != null) {
                        mProgress.progress(progress);
                    }
                }
            });
        } else {
            mProgress = null;
        }
    }

    private void success(final IParseResult<V> result, final IResponse<V> response) {
        if (isNotCancel()) {
            if (result.isSuccess()) {
                if (mSuccessIO != null) {
                    mSuccessIO.success(mTag, result.getT(), response);
                }
                if (isNotCancel()) {
                    Platform.get().callback(new Runnable() {
                        @Override
                        public void run() {
                            if (isNotCancel() && mSuccess != null) {
                                mSuccess.success(mTag, result.getT(), response);
                            }
                            complete();
                        }
                    });
                }
            } else {
                onError(result.getCode(), result.getMessage(), result.getException());
            }
        }
    }

    @Override
    public void onError(final String code, final String desc, final Exception e) {
        if (isNotCancel()) {
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
    }

    private boolean isNotCancel() {
        if (!isContinue.get()) {
            complete();
        }
        return isContinue.get();
    }

    private void complete() {
        if (Platform.get().isCallbackThread()) {
            if (mComplete != null) {
                mComplete.complete(mTag);
            }
        } else {
            Platform.get().callback(new Runnable() {
                @Override
                public void run() {
                    complete();
                }
            });
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
