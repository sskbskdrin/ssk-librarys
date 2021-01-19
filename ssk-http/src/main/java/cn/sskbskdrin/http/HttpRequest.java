package cn.sskbskdrin.http;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import cn.sskbskdrin.flow.FlowProcess;
import cn.sskbskdrin.flow.IFlow;
import cn.sskbskdrin.flow.IProcess;

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

    private AtomicBoolean isCancel = new AtomicBoolean(false);
    private Type mType;

    private IProgress inProgress = new IProgress() {
        @Override
        public void progress(final float progress) {
            if (mProgress != null && !isCancel.get()) {
                Platform.get().callback(new Runnable() {
                    @Override
                    public void run() {
                        if (mProgress != null) {
                            mProgress.progress(((int) (progress * 100)) / 100f);
                        }
                    }
                });
            } else {
                mProgress = null;
            }
        }
    };

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
    public V getSync() {
        return null;
    }

    @Override
    public void post() {
        mContentType = CONTENT_TYPE_FORM;
        request();
    }

    @Override
    public V postSync() {
        return null;
    }

    @Override
    public void postJson() {
        mContentType = CONTENT_TYPE_JSON;
        request();
    }

    @Override
    public V postJsonSync() {
        return null;
    }

    @Override
    public void postFile() {
        mContentType = CONTENT_TYPE_MULTIPART;
        request();
    }

    @Override
    public V postFileSync() {
        return null;
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
    public V downLoad(String filePath) {
        return null;
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
            new FlowProcess().main(mError == null ? null : new IProcess<Object, Object>() {
                @Override
                public Object process(IFlow flow, Object last, Object... params) {
                    mError.error(mTag, ERROR_REAL_REQUEST, "没找到请求实现类，请先设置请求工厂", new IllegalAccessException(
                        "IRealRequestFactory " + "not " + "impl or IRealRequest is null"));
                    return null;
                }
            }).start();
            return;
        }
        FlowProcess process = new FlowProcess(realRequest);
        process.main(mPreRequest == null ? null : new IProcess<IRealRequest, IRealRequest>() {
            @Override
            public IRealRequest process(IFlow flow, IRealRequest last, Object... params) {
                Platform.get().log(TAG, "pre");
                mPreRequest.onPreRequest(mTag);
                return last;
            }
        }).io(new IProcess<Object, IRealRequest>() {
            @Override
            public Object process(IFlow flow, IRealRequest last, Object... params) {
                Platform.get().log(TAG, "request");
                if (isCancel(flow)) return null;
                IResponse res = request(last);
                if (res == null || !res.isSuccess()) {
                    flow.remove("parse");
                    flow.remove("successIO");
                    flow.remove("success");
                    return new Error("" + res.code(), res.message(), res.exception());
                }
                return res;
            }
        }).io("parse", new IProcess<Object, IResponse>() {
            @Override
            public Object process(IFlow flow, IResponse last, Object... params) {
                Platform.get().log(TAG, "parse");
                if (isCancel(flow)) return null;
                IParseResult result;
                try {
                    IParseResponse response = mParseResponse == null ? getConfig().iParseResponse : mParseResponse;
                    if (response != null) {
                        result = response.parse(mTag, last, mType);
                    } else {
                        result = IParseResult.DEFAULT;
                    }
                } catch (Exception e) {
                    return new Error(ERROR_PARSE, last.message(), e);
                }
                if (result != null && result.isCancel()) {
                    flow.remove("success");
                    flow.remove("error");
                }
                if (last.isSuccess() && (result == null || result.isSuccess())) flow.remove("error");
                else flow.remove("success");
                return result;
            }
        }).io("success", mSuccessIO == null ? null : new IProcess<IParseResult<V>, IParseResult<V>>() {
            @Override
            public IParseResult<V> process(IFlow flow, IParseResult<V> last, Object... params) {
                Platform.get().log(TAG, "successIO");
                if (isCancel(flow)) return null;
                if (mSuccessIO != null && last.isSuccess()) {
                    mSuccessIO.success(mTag, last.getT(), last);
                }
                return last;
            }
        }).main("success", mSuccess == null ? null : new IProcess<IParseResult<V>, IParseResult<V>>() {
            @Override
            public IParseResult<V> process(IFlow flow, IParseResult<V> last, Object... params) {
                Platform.get().log(TAG, "success");
                if (isCancel(flow)) return null;
                if (mSuccess != null && last.isSuccess()) {
                    mSuccessIO.success(mTag, last.getT(), last);
                }
                return last;
            }
        }).main("error", mError == null ? null : new IProcess<Object, Error>() {
            @Override
            public Object process(IFlow flow, Error error, Object... params) {
                Platform.get().log(TAG, "error", error.throwable);
                if (isCancel(flow)) return null;
                if (mError != null) {
                    mError.error(mTag, error.code, error.msg, error.throwable);
                }
                return error;
            }
        }).main(mComplete == null ? null : new IProcess<Object, Object>() {
            @Override
            public Object process(IFlow flow, Object last, Object... params) {
                Platform.get().log(TAG, "complete");
                if (mComplete != null) mComplete.complete(mTag);
                return null;
            }
        }).start();
    }

    private Config getConfig() {
        return Config.INSTANCE;
    }

    private IResponse request(IRealRequest request) {
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

    private boolean isCancel(IFlow flow) {
        boolean cancel = isCancel.get();
        if (cancel) {
            flow.remove("parse");
            flow.remove("success");
            flow.remove("error");
        }
        return cancel;
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
