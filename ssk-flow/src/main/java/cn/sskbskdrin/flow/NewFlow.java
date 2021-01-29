package cn.sskbskdrin.flow;

/**
 * Created by keayuan on 2021/1/29.
 *
 * @author keayuan
 */
class NewFlow {
    public static void main(String[] args) {
        new Flow<String>().main(new IProcess<Long, String>() {

            @Override
            public Long process(IFlow<Long> flowProcess, String last, Object... params) {
                return null;
            }
        }).io(new IProcess<Integer, Long>() {
            @Override
            public Integer process(IFlow<Integer> flowProcess, Long last, Object... params) {
                return null;
            }
        }).main(new IProcess<Object, Integer>() {
            @Override
            public Object process(IFlow<Object> flowProcess, Integer last, Object... params) {
                return null;
            }
        }).start();
    }
}
