package cn.sskbskdrin.http;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by keayuan on 2020/4/17.
 *
 * @author keayuan
 */
public class Main {
    public static void main(String[] args) {
        JsonObject object = new JsonObject();
        object.addProperty("name", "B-name");
        JsonObject o = new JsonObject();
        o.addProperty("a", 15);
        object.add("t", o);
        B<AA> b = new B<>();
        b.a = true;
        b.bb = "nnn";
        b.t = new AA();
        b.t.aa = 15;
        final String str = new Gson().toJson(b);
        System.out.println(str);
        B<AA> list = parse(str, new TypeToken<B<AA>>() {});
        final B parse = parse(str, B.class);
        System.out.println(parse);
        List<AA> arr = parse("[{\"aa\":12},{\"aa\":13},{\"aa\":14}]", new TypeToken<List<AA>>() {});
        //        System.out.println(arr);
        //        System.out.println(list.toString());

        HTTP.url("https://www.baidu.com", new TypeToken<B<AA>>() {}).parseResponse(new IParseResponse<B<AA>>() {
            @Override
            public IParseResult<B<AA>> parse(String tag, IResponse response, Type clazz) {
                Res<B<AA>> res = new Res<>();
                B<AA> b = new Gson().fromJson(str, clazz);
                res.setBean(b);
                res.isSuccess = true;
                return res;
            }
        }).success(new ISuccess<B<AA>>() {
            @Override
            public void success(String tag, B result, IResponse response) {
                System.out.println(result);
            }
        }).error(new IError() {
            @Override
            public void error(String tag, String code, String desc, Exception e) {
                System.out.println("code=" + code + " desc=" + desc);
                if (e != null) {
                    e.printStackTrace();
                }
            }
        }).complete(new IComplete() {
            @Override
            public void complete(String tag) {
                System.out.println("complete");
            }
        }).get();
    }

    static <T> T parse(String text, Class<T> tClass) {
        return new Gson().fromJson(text, tClass);
    }

    static <T> T parse(String text, TypeToken<T> token) {
        return new Gson().fromJson(text, token.getType());
    }

    static class B<T> extends A<T> {
        String bb;

        @Override
        public String toString() {
            return String.format("name=%s %s", bb, super.toString());
        }
    }

    static class A<T> {
        T t;
        boolean a;

        @Override
        public String toString() {
            return String.format("b=%b t=%s", a, t);
        }
    }

    static class AA {
        int aa;

        @Override
        public String toString() {
            return String.valueOf(aa);
        }
    }
}
