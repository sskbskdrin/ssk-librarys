package cn.sskbskdrin.http;

import java.io.File;
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
class HRequest<V> implements IRequest<V>, IRequestBody {

    private final HashMap<String, String> mHeader = new HashMap<>();
    private final HashMap<String, Object> mParams = new HashMap<>();

    private String mContentType;

    private IMap<String, String> iHeader;
    private IMap<String, Object> iParams;

    private IPreRequest mPreRequest;
    private IParseResponse<?> mParseResponse;
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
                            mProgress.progress(progress);
                        }
                    }
                });
            } else {
                mProgress = null;
            }
        }
    };

    HRequest(String url, Type type) {
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
            new FlowProcess().main(mError == null ? null : new IProcess<Object>() {
                @Override
                public Object process(IFlow flow, Object... params) {
                    mError.error(mTag, ERROR_REAL_REQUEST, "没找到请求实现类，请先设置请求工厂", new IllegalAccessException(
                        "IRealRequestFactory " + "not " + "impl or IRealRequest is null"));
                    return null;
                }
            }).start();
            return;
        }
        final IRRequest request = null;//TODO 未实现

        FlowProcess process = new FlowProcess(request);
        process.main(mPreRequest == null ? null : new IProcess<Object>() {
            @Override
            public Object process(IFlow flow, Object... params) {
                mPreRequest.onPreRequest(mTag);
                return params[0];
            }
        }).io(new IProcess<Response>() {
            @Override
            public Response process(IFlow flow, Object... params) {
                if (isCancel(flow)) return null;
                Response res = request((IRRequest) params[0]);
                if (res == null || !res.isSuccess()) {
                    flow.remove("parse");
                    flow.remove("successIO");
                    flow.remove("success");
                } else {
                    if (res.isFile()) flow.remove("parse");
                }
                return res;
            }
        }).io("parse", new IProcess<Response>() {
            @Override
            public Response process(IFlow flow, Object... params) {
                if (isCancel(flow)) return null;
                Response res = (Response) params[0];
                try {
                    if (mParseResponse != null) {
                        res.result = mParseResponse.parse(mTag, (IResponse) params[0], mType);
                    } else {
                        res.result = getConfig().parse(mTag, (IResponse) params[0], mType);
                    }
                    if (res.result == null) {
                        res = Response.get(ERROR_NO_PARSE, res.string(), null);
                    }
                } catch (Exception e) {
                    res = Response.get(ERROR_PARSE, res.string(), e);
                }
                return res;
            }
        }).io("successIO", mSuccessIO == null ? null : new IProcess<Response>() {
            @Override
            public Response process(IFlow flow, Object... params) {
                if (isCancel(flow)) return null;
                Response res = (Response) params[0];
                if (mSuccessIO != null && res.isSuccess()) {
                    if (res.isFile()) {
                        mSuccessIO.success(mTag, (V) new File(res.string()), res);
                        return res;
                    }
                    IParseResult<V> ret = res.result;
                    if (ret != null && ret.isSuccess()) {
                        mSuccessIO.success(mTag, ret.getT(), res);
                    }
                }
                flow.remove("error");
                return res;
            }
        }).main("success", mSuccess == null ? null : new IProcess<Response>() {
            @Override
            public Response process(IFlow flow, Object... params) {
                if (isCancel(flow)) return null;
                Response res = (Response) params[0];
                if (mSuccess != null && res.isSuccess()) {
                    if (res.isFile()) {
                        mSuccess.success(mTag, (V) new File(res.string()), res);
                        return res;
                    }
                    IParseResult<V> ret = res.result;
                    if (ret != null && ret.isSuccess()) {
                        mSuccess.success(mTag, ret.getT(), res);
                    }
                }
                flow.remove("error");
                return res;
            }
        }).main("error", mError == null ? null : new IProcess<Response>() {
            @Override
            public Response process(IFlow flow, Object... params) {
                if (isCancel(flow)) return null;
                Response res = (Response) params[0];
                if (mError != null) {
                    IParseResult<V> ret = res.result;
                    if (!res.isSuccess()) {
                        mError.error(mTag, res.code(), res.desc(), res.exception());
                    } else if (ret == null) {
                        mError.error(mTag, ERROR_UNKNOWN, "未知错误", null);
                    } else if (!ret.isSuccess()) {
                        mError.error(mTag, ret.getCode(), ret.getMessage(), ret.getException());
                    }
                }
                return res;
            }
        }).main(mComplete == null ? null : new IProcess<Object>() {
            @Override
            public Object process(IFlow flow, Object... params) {
                if (mComplete != null) mComplete.complete(mTag);
                return null;
            }
        }).start();
    }

    private Config getConfig() {
        return Config.INSTANCE;
    }

    private Response request(IRRequest request) {
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
            flow.remove("successIO");
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
