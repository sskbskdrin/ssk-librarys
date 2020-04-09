package cn.sskbskdrin.http;

import java.io.File;
import java.util.HashMap;

public interface IRequestBody {

    String getUrl();

    HashMap<String, String> getParams();

    HashMap<String, File> getFileParams();

    HashMap<String, String> getHeader();

    long getConnectedTimeout();

    long getReadTimeout();
}
