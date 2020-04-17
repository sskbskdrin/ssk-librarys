package cn.sskbskdrin.http;

import java.io.File;

/**
 * Created by keayuan on 2020/4/17.
 *
 * @author keayuan
 */
public class ParseResult implements IParseResult<File> {

    private File file;

    ParseResult(File file) {
        this.file = file;
    }

    @Override
    public boolean isSuccess() {
        return file != null && file.exists();
    }

    @Override
    public boolean isCancel() {
        return false;
    }

    @Override
    public String getCode() {
        return null;
    }

    @Override
    public String getMessage() {
        return null;
    }

    @Override
    public Exception getException() {
        return null;
    }

    @Override
    public File getT() {
        return file;
    }
}
