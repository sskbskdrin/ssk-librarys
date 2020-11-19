package cn.sskbskdrin.lib.demo.tool;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import cn.sskbskdrin.base.IFragment;
import cn.sskbskdrin.http.HTTP;
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
    protected void onViewCreated(View rootView, Bundle arguments, Bundle savedInstanceState) {
        resultView = getView(R.id.http_result);
        resultView.setMovementMethod(ScrollingMovementMethod.getInstance());
        urlView = getView(R.id.http_url);
        getView(R.id.http_request).setOnClickListener(v -> request());
        protocolView = getView(R.id.http_url_protocol);
        protocolView.setAdapter(new SimpleAdapter<>(new String[]{"https://", "http://"}, dp2px(36), true));
        methodView = getView(R.id.http_method_sp);
        methodView.setAdapter(new SimpleAdapter<>(new String[]{"GET", "POST", "JSON", "POSTFILE", "DOWNLOAD"},
            dp2px(36), true));
    }

    private void request() {
        HTTP.globalConfig().setOpenLog(true);
        String url = urlView.getText().toString().replaceAll("\n", "");
        if (!url.startsWith("http")) {
            url = protocolView.getSelectedItem() + url;
        }
        IRequest request = HTTP.url(url)
            .success((tag, result, response) -> resultView.setText(String.valueOf(response.string())))
            .error((tag, code, desc, e) -> resultView.setText(code + "\n" + desc + "\n" + (e != null ?
                e.getMessage() : "")));
        switch (String.valueOf(methodView.getSelectedItem())) {
            case "GET":
                request.get();
                break;
            case "POST":
                request.post();
                break;
            case "JSON":
                request.postJson();
                break;
            case "POSTFILE":
                request.postFile();
                break;
            case "DOWNLOAD":
                request.download("");
                break;
        }
    }
}
