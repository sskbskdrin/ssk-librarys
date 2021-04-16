package cn.sskbskdrin.log.widget;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author sskbskdrin
 * @date 2019-07-07
 */
final class Log {
    private static final String[] LEVEL = {"V", "D", "I", "W", "E", "A"};
    private static final int[] COLOR = {0xFF6F7365, 0XFF3578D4, 0XFF11BB2F, 0XFFFFB157, 0XFFCA3A33, 0XFFFF0000};
    private static final int filterColor = 0xa000b5ff;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS", Locale.US);

    private static final Date date = new Date();
    private static Pattern pattern;

    final int priority;

    final String tag;

    private CharSequence content;
    final int size;

    boolean isRemove = false;

    Log(int level, String tag, String message) {
        if (level < 2) {
            priority = 0;
        } else if (level > 7) {
            priority = 5;
        } else {
            priority = level - 2;
        }
        this.tag = tag;
        date.setTime(System.currentTimeMillis());
        content = dateFormat.format(date) + " " + LEVEL[priority] + "/" + tag + ": " + message;
        size = content.toString().getBytes().length;
    }

    static void filter(String reg) {
        pattern = TextUtils.isEmpty(reg) ? null : Pattern.compile(reg, Pattern.CASE_INSENSITIVE);
    }

    boolean checkSpan() {
        if (pattern != null) {
            Matcher matcher = pattern.matcher(content);
            boolean has = false;
            SpannableString ss = new SpannableString(content.toString());
            while (matcher.find()) {
                has = true;
                ss.setSpan(new BackgroundColorSpan(filterColor), matcher.start(), matcher.end(),
                    Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            }
            if (has) {
                content = ss;
            } else {
                content = content.toString();
                return false;
            }
        } else {
            content = content.toString();
        }
        return true;
    }

    CharSequence getContent() {
        return content;
    }

    int color() {
        return COLOR[priority];
    }

    void markRemove() {
        isRemove = true;
    }

    @Override
    public String toString() {
        return content.toString();
    }
}
