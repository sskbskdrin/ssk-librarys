package cn.sskbskdrin.http;

import java.io.File;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import cn.sskbskdrin.flow.FlowProcess;
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
            onError(ERROR_REAL_REQUEST, "没找到请求实现类，请先设置请求工厂", new IllegalAccessException("IRealRequestFactory not " +
                "impl or IRealRequest is null"));
            return;
        }
        final IRRequest request = null;
        
        final FlowProcess process = new FlowProcess(request);
        process.main(mPreRequest == null ? null : new IProcess<Object>() {
            @Override
            public Object process(int[] jump, Object... params) {
                mPreRequest.onPreRequest(mTag);
                return params[0];
            }
        }).io(new IProcess<Response>() {
            @Override
            public Response process(int[] jump, Object... params) {
                if (checkCancel(jump)) return null;
                Response res = request((IRRequest) params[0]);
                if (res == null || !res.isSuccess()) {
                    jump[0] = 1;
                }
                return res;
            }
        }).io(mParseResponse == null ? null : new IProcess<Response>() {
            @Override
            public Response process(int[] jump, Object... params) {
                if (checkCancel(jump)) return null;
                Response res = (Response) params[0];
                try {
                    res.result = mParseResponse.parse(mTag, (IResponse) params[0], mType);
                    if (res.result == null) {
                        res = Response.get(ERROR_NO_PARSE, res.string(), null);
                    }
                } catch (Exception e) {
                    res = Response.get(ERROR_PARSE, res.string(), e);
                }
                return res;
            }
        }).io(mParseResponse != null ? null : new IProcess<Response>() {
            @Override
            public Response process(int[] jump, Object... params) {
                if (checkCancel(jump)) return null;
                Response res = (Response) params[0];
                try {
                    res.result = getConfig().parse(mTag, (IResponse) params[0], mType);
                    if (res.result == null) {
                        res = Response.get(ERROR_NO_PARSE, res.string(), null);
                    }
                } catch (Exception e) {
                    res = Response.get(ERROR_PARSE, res.string(), e);
                }
                return res;
            }
        }).io(mSuccessIO == null ? null : new IProcess<Response>() {
            @Override
            public Response process(int[] jump, Object... params) {
                if (checkCancel(jump)) return null;
                Response res = (Response) params[0];
                if (isNotCancel() && mSuccessIO != null && res.isSuccess()) {
                    IParseResult<V> ret = res.result;
                    if (ret != null && ret.isSuccess()) {
                        mSuccessIO.success(mTag, (V) ret.getT(), res);
                    }
                }
                return res;
            }
        }).main(mSuccess == null ? null : new IProcess<Response>() {
            @Override
            public Response process(int[] jump, Object... params) {
                if (checkCancel(jump)) return null;
                Response res = (Response) params[0];
                if (isNotCancel() && mSuccess != null && res.isSuccess()) {
                    IParseResult<V> ret = res.result;
                    if (ret != null && ret.isSuccess()) {
                        mSuccess.success(mTag, (V) ret.getT(), res);
                    }
                }
                return res;
            }
        }).main(mError == null ? null : new IProcess<Response>() {
            @Override
            public Response process(int[] jump, Object... params) {
                if (checkCancel(jump)) return null;
                Response res = (Response) params[0];
                if (isNotCancel() && mError != null) {
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
            public Object process(int[] jump, Object... params) {
                if (checkCancel(jump)) return null;
                if (mComplete != null) {
                    mComplete.complete(mTag);
                }
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
        isContinue.set(false);
    }

    private boolean checkCancel(int[] jump) {
        boolean cancel = isCancel.get();
        if (cancel) jump[0] = -1;
        return cancel;
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

    private boolean isCancel() {
        return isCancel.get();
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
