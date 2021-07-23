package cn.sskbskdrin.util;

import java.util.List;
import java.util.Objects;

import cn.sskbskdrin.util.function.Consumer;

/**
 * Created by keayuan on 2021/2/4.
 *
 * @author keayuan
 */
public class CollectionUtils {

    private CollectionUtils() {
    }

    //================== List ==================
    public static <T> boolean isEmpty(List<T> collection) {
        return collection == null || collection.isEmpty();
    }

    public static <T> boolean isNotEmpty(List<T> collection) {
        return !isEmpty(collection);
    }

    public static <T> T getLast(List<T> collection) {
        if (isEmpty(collection)) return null;
        int size = collection.size();
        return collection.get(size - 1);
    }

    public static <T> T getFirst(List<T> collection) {
        if (isEmpty(collection)) return null;
        return collection.get(0);
    }

    public static int getSize(List<?> list) {
        return list == null ? 0 : list.size();
    }

    public static <T> T getQuietly(List<T> list, int position) {
        if (position < 0 || position >= getSize(list)) return null;
        return list.get(position);
    }

    public static <E> void forEach(List<E> list, Consumer<E> consumer) {
        Objects.requireNonNull(consumer);
        if (isEmpty(list)) {
            return;
        }
        for (E e : list) {
            consumer.accept(e);
        }
    }

    //================== Array ==================
    public static <T> boolean isEmpty(T[] arr) {
        return arr == null || arr.length == 0;
    }

    public static <T> boolean isNotEmpty(T[] arr) {
        return !isEmpty(arr);
    }

}
