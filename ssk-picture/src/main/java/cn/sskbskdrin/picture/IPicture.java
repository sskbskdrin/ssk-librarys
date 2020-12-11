package cn.sskbskdrin.picture;

/**
 * Created by keayuan on 2020/12/11.
 *
 * @author keayuan
 */
interface IPicture {
    IPicture take();

    IPicture takeAndCrop();

    IPicture pick();

    IPicture pickAndCrop();
}
