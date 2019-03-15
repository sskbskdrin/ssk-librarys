package cn.sskbskdrin.lib.demo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ex-keayuan001 on 2019/3/15.
 *
 * @author ex-keayuan001
 */
public class Utils {
    public static List<String> getSimpleList(int size) {
        List<String> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            list.add("simple " + i);
        }
        return list;
    }
}
