package cn.sskbskdrin.http.okhttp;

import android.util.Log;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.sskbskdrin.http.HTTP;
import cn.sskbskdrin.http.IRealRequest;
import cn.sskbskdrin.http.IRealRequestFactory;
import cn.sskbskdrin.http.IRequest;
import cn.sskbskdrin.http.IRequestBody;
import cn.sskbskdrin.http.IResponse;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

/**
 * Created by keayuan on 2021/1/20.
 *
 * @author keayuan
 */
public class OkHttpRealRequest implements IRealRequest {
    private static final String TAG = "OkHttpRealRequest";

    private static IRealRequestFactory factory = new IRealRequestFactory() {
        @Override
        public IRealRequest generateRealRequest() {
            return new OkHttpRealRequest();
        }
    };

    protected Call call;

    static {
        HTTP.getConfig().setRealRequestFactory(factory);
        setOkHttpClient(new OkHttpClient());
    }

    private static OkHttpClient mOkHttpClient;
    private static Cache mCache;

    public static void setOkHttpClient(OkHttpClient client) {
        mOkHttpClient = client;
        if (mOkHttpClient == null) {
            mOkHttpClient = new OkHttpClient();
        }
    }

    public static void setCache(Cache cache) {
        mCache = cache;
    }

    @Override
    public IResponse get(IRequestBody request) throws Exception {
        return deliverRequest(request, buildRequest(request, "GET", null, null));
    }

    @Override
    public IResponse post(IRequestBody request) throws Exception {
        FormBody.Builder builder = new FormBody.Builder();
        for (Map.Entry<String, Object> entry : request.getParams().entrySet()) {
            builder.add(entry.getKey(), entry.getValue().toString());
        }
        RequestBody requestBody = builder.build();
        return deliverRequest(request, buildRequest(request, "POST", requestBody, null));
    }

    @Override
    public IResponse postJson(IRequestBody request) throws Exception {
        JSONObject jsonObject = new JSONObject(request.getParams());
        RequestBody requestBody = RequestBody.create(MediaType.parse(IRequest.CONTENT_TYPE_JSON + ";charset=utf-8"),
            jsonObject
            .toString());
        return deliverRequest(request, buildRequest(request, "POST", requestBody, null));
    }

    @Override
    public IResponse postFile(IRequestBody request) throws Exception {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        //设置类型
        builder.setType(MultipartBody.FORM);
        //追加参数
        Map<String, Object> params = request.getParams();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            Object object = entry.getValue();
            if (object instanceof List) {
                List list = (List) object;
                if (list.size() > 0) {
                    Object temp = list.get(0);
                    if (temp instanceof File) {
                        for (File file : (List<File>) list) {
                            builder.addFormDataPart(key, file.getName(), RequestBody.create(null, file));
                        }
                    } else {
                        for (Object id : list) {
                            builder.addFormDataPart(key, id.toString());
                        }
                    }
                }
            } else if (object instanceof File) {
                builder.addFormDataPart(key, ((File) object).getName(), RequestBody.create(null, (File) object));
            } else {
                builder.addFormDataPart(key, object.toString());
            }
        }

        //创建RequestBody
        RequestBody body = builder.build();

        //创建Request
        return deliverRequest(request, buildRequest(request, "POST", body, null));
    }

    protected Request buildRequest(IRequestBody iRequest, String method, RequestBody requestBody, String tag) throws Exception {
        Request.Builder builder = new Request.Builder().url(iRequest.getUrl())
            .headers(Headers.of(iRequest.getHeader()));
        if (tag != null) {
            builder.tag(tag);
        }
        if (iRequest.getCacheTimeout() >= 0) {
            builder.cacheControl(new CacheControl.Builder().maxAge((int) iRequest.getCacheTimeout(), TimeUnit.SECONDS)
                .build());
        }
        return builder.method(method, requestBody).build();
    }

    protected synchronized IResponse deliverRequest(IRequestBody iRequest, final Request request) throws Exception {
        if (HTTP.getConfig().isOpenLog()) {
            Log.d(TAG, "request url======>>" + request.url());
            Log.d(TAG, "request header===>>" + request.headers().toString().replaceAll("\n", ""));
            RequestBody body = request.body();
            if (body != null) {
                Buffer buffer = new Buffer();
                try {
                    body.writeTo(buffer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "request body=====>>" + buffer.readUtf8());
            }
        }
        OkHttpClient.Builder builder = mOkHttpClient.newBuilder()
            .retryOnConnectionFailure(true)
            .connectTimeout(iRequest.getConnectedTimeout(), TimeUnit.MILLISECONDS)
            .readTimeout(iRequest.getReadTimeout(), TimeUnit.MILLISECONDS)
            .retryOnConnectionFailure(true);
        if (mCache != null) {
            builder.cache(mCache);
        }
        call = builder.build().newCall(request);
        return generateResponse(call.execute());
    }

    protected IResponse generateResponse(Response response) {
        return new OkHttpResponse(response);
    }

    @Override
    public void close() {
        if (call != null) {
            call.cancel();
        }
        call = null;
    }
}
