package cn.sskbskdrin.log.logcat;

import cn.sskbskdrin.log.Format;
import cn.sskbskdrin.log.L;

/**
 * Draws borders around the given log message along with additional information such as :
 * <ul>
 * <li>Thread information</li>
 * <li>Method stack trace</li>
 * </ul>
 * <pre>
 *  ┌────────Thread information──────────────────
 *  │ Method stack history
 *  ├--------------------------------------------
 *  │ Log message
 *  └────────────────────────────────────────────
 * </pre>
 * <h3>Customize</h3>
 */
public class PrettyFormat implements Format {

    /**
     * Android's max limit for a log entry is ~4076 bytes,
     * so 4000 bytes is used as chunk size since default charset
     * is UTF-8
     */
    private static final int CHUNK_SIZE = 4000;

    /**
     * The minimum stack trace index, starts at this class after two native calls.
     */
    private static final int MIN_STACK_OFFSET = 8;

    /**
     * Drawing toolbox
     */
    private static final char HORIZONTAL_LINE = '│';
    private static final String MIDDLE_BORDER = "================================";
    private static final String SINGLE_DIVIDER =
        "│------------------------------------------------------------------------------------------------";
    private static final String THREAD_INFO = "┌" + MIDDLE_BORDER + "    Thread:%s    ID:%s    " + MIDDLE_BORDER;
    private static final String BOTTOM_BORDER = "└" + MIDDLE_BORDER + MIDDLE_BORDER + MIDDLE_BORDER;
    private static final String METHOD_INFO = "%s%s.%s (%s:%s)";

    private int methodCount = 10;
    private int methodOffset = 0;
    private boolean logMethod = true;

    private final StringBuilder mBuilder;

    public PrettyFormat() {
        this(8, 0);
    }

    public PrettyFormat(int count, int offset) {
        mBuilder = new StringBuilder(CHUNK_SIZE);
        methodCount = count;
        if (methodCount < 0) {
            methodCount = 0;
        }
        methodOffset = offset;
        if (methodOffset < 0) {
            methodOffset = 0;
        }
    }

    private void logHeaderContent(StringBuilder builder) {
        mBuilder.append(' ');
        mBuilder.append(NEW_LINE);
        Thread thread = Thread.currentThread();
        builder.append(String.format(THREAD_INFO, thread.getName(), android.os.Process.myTid()));
    }

    private void logMethod(StringBuilder builder) {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        int stackOffset = getStackOffset(trace) + methodOffset;

        StringBuilder level = new StringBuilder(" ");
        for (int i = stackOffset + methodCount; i >= stackOffset; i--) {
            if (i >= trace.length || i < 0) {
                continue;
            }
            builder.append(NEW_LINE);
            builder.append(HORIZONTAL_LINE);
            builder.append(String.format(METHOD_INFO, level.toString(), trace[i].getClassName(), trace[i]
                .getMethodName(), trace[i].getFileName(), trace[i].getLineNumber()));
            level.append("  ");
        }
        builder.append(NEW_LINE);
        builder.append(SINGLE_DIVIDER);
    }

    private void logBottomBorder(StringBuilder builder) {
        builder.append(NEW_LINE);
        builder.append(BOTTOM_BORDER);
    }

    private void logContent(StringBuilder builder, String msg) {
        builder.append(NEW_LINE);
        builder.append(HORIZONTAL_LINE);
        builder.append(msg);
    }

    /**
     * Determines the starting index of the stack trace, after method calls made by this class.
     *
     * @param trace the stack trace
     * @return the stack offset
     */
    private int getStackOffset(StackTraceElement[] trace) {
        for (int i = MIN_STACK_OFFSET; i < trace.length; i++) {
            StackTraceElement e = trace[i];
            String name = e.getClassName();
            if (!name.equals(L.class.getName())) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public String formatTag(int priority, String tag) {
        logMethod = priority >= L.WARN;
        return tag;
    }

    @Override
    public String format(String msg) {
        mBuilder.setLength(0);
        logHeaderContent(mBuilder);
        if (logMethod) {
            logMethod(mBuilder);
        }
        String[] temp = msg.split(NEW_LINE);
        for (String content : temp) {
            if (content.length() <= CHUNK_SIZE) {
                logContent(mBuilder, content);
            } else {
                for (int i = 0; i < content.length(); i += CHUNK_SIZE) {
                    int count = Math.min(content.length() - i, CHUNK_SIZE);
                    logContent(mBuilder, content.substring(i, i + count));
                }
            }
        }
        logBottomBorder(mBuilder);
        return mBuilder.toString();
    }
}
