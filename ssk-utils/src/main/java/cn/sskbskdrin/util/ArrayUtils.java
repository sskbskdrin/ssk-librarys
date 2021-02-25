package cn.sskbskdrin.util;

/**
 * Array Utils
 */
public class ArrayUtils {

    private ArrayUtils() {
        throw new AssertionError();
    }

    /**
     * is null or its length is 0
     *
     * @param <V>
     * @param sourceArray
     * @return
     */
    public static <V> boolean isEmpty(V[] sourceArray) {
        return (sourceArray == null || sourceArray.length == 0);
    }

    public static <V> int size(V[] array) {
        return array == null ? 0 : array.length;
    }

}
