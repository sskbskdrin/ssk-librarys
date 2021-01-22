package cn.sskbskdrin.lib.demo.tool;

import android.Manifest;
import android.os.Bundle;
import android.os.Environment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import cn.sskbskdrin.base.IFragment;
import cn.sskbskdrin.http.HTTP;
import cn.sskbskdrin.http.HttpUtils;
import cn.sskbskdrin.http.IRequest;
import cn.sskbskdrin.lib.demo.R;
import cn.sskbskdrin.lib.demo.simple.SimpleAdapter;

/**
 * Created by keayuan on 2020/8/10.
 *
 * @author keayuan
 */
public class HttpFragment extends IFragment {
    private TextView resultView;
    private TextView urlView;
    private Spinner protocolView;
    private Spinner methodView;

    @Override
    protected int getLayoutId() {
        return R.layout.f_http_layout;
    }

    @Override
    protected void onInitView(View rootView,  Bundle savedInstanceState) {
        resultView = getView(R.id.http_result);
        resultView.setMovementMethod(ScrollingMovementMethod.getInstance());
        urlView = getView(R.id.http_url);
        getView(R.id.http_request).setOnClickListener(v -> request());
        protocolView = getView(R.id.http_url_protocol);
        protocolView.setAdapter(new SimpleAdapter<>(new String[]{"https://", "http://"}, dp2px(36), true));
        methodView = getView(R.id.http_method_sp);
        methodView.setAdapter(new SimpleAdapter<>(new String[]{"GET", "POST", "JSON", "POSTFILE", "DOWNLOAD"},
            dp2px(36), true));

        getView(R.id.http_upload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //                HTTP.url("https://static.iyuan.site/public.zip").progress(new IProgress() {
                //                    @Override
                //                    public void progress(float progress) {
                //                        Log.i(TAG, "progress: " + progress);
                //                    }
                //                }).download(Environment.getExternalStorageDirectory().getAbsolutePath() + "/public
                //                .zip");

                HttpUtils.create("https://static.iyuan.site/public.zip")
                    .onProgress((res, e) -> Log.i(TAG, "progress: " + res))
                    .onError((res, e) -> Log.w(TAG, "error: " + res, e))
                    .downLoad(Environment.getExternalStorageDirectory()
                        .getAbsolutePath() + "/public.zip", (res, e) -> Log.d(TAG,
                        "success: " + res.getAbsolutePath()));
            }
        });
        checkPermission(1001, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    private Set<Closeable> closeable = new HashSet<>();

    private void request() {
        HTTP.getConfig().setOpenLog(true);
        //        HTTP.getConfig().setRealRequestFactory(null);
        String url = urlView.getText().toString().replaceAll("\n", "");
        if (!url.startsWith("http")) {
            url = protocolView.getSelectedItem() + url;
        }

        IRequest<String> request = null;
        switch (String.valueOf(methodView.getSelectedItem())) {
            case "GET":
                request = HTTP.get(url, HTTP.STRING_PARSE_RESPONSE);
                break;
            case "POST":
                request = HTTP.post(url, String.class);
                break;
            case "JSON":
                request = HTTP.postJson(url, String.class);
                break;
            case "POSTFILE":
                request = HTTP.postFile(url, String.class);
                break;
            case "DOWNLOAD":
                Closeable close = HTTP.download("https://static.iyuan.site/public.zip", getContext().getCacheDir()
                    .getAbsolutePath() + "/public.zip")
                    .pre((tag, closeable) -> this.closeable.add(closeable))
                    .success((tag, file, fileIParseResult) -> resultView.setText(file.getAbsolutePath()))
                    .request();
                closeable.add(close);
                break;
        }
        if (request != null) {
            request.pre((tag, closeable) -> Log.d(TAG, "request: "))
                .successIO((tag, s, stringIParseResult) -> Log.d(TAG, "successIO"))
                .success((tag, result, response) -> resultView.setText(response.getT()))
                .error((tag, code, msg, throwable) -> {
                    resultView.setText(code + "\n" + msg + "\n" + (throwable != null ? throwable.getMessage() : ""));
                })
                .complete((tag, s) -> Log.d(TAG, s.toString()))
                .request();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        resultView = null;
        if (closeable != null) {
            try {
                for (Closeable closeable1 : closeable) {
                    closeable1.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
