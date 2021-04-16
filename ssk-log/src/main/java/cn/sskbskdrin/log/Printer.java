package cn.sskbskdrin.log;

/**
 * @author ex-keayuan001
 */
public abstract class Printer implements LogStrategy {

    protected static final String NEW_LINE = System.getProperty("line.separator");

    private Format format;
    private LogStrategy strategy;
    private Filter filter;

    public Printer() {
    }

    public Printer(Format format) {
        this(format, null);
    }

    public Printer(LogStrategy strategy) {
        this(null, strategy);
    }

    public Printer(Format format, LogStrategy strategy) {
        this.format = format;
        this.strategy = strategy;
    }

    public final void setFormat(Format format) {
        this.format = format;
    }

    public final void setStrategy(LogStrategy strategy) {
        this.strategy = strategy;
    }

    public final void setFilter(Filter filter) {
        this.filter = filter;
    }

    /**
     * Used to determine whether log should be printed out or not.
     *
     * @param priority is the log level e.g. DEBUG, WARNING
     * @param tag      is the given tag for the log message
     * @return is used to determine if log should printed.
     * If it is true, it will be printed, otherwise it'll be ignored.
     */
    private boolean isLoggable(int priority, String tag) {
        return filter != null ? filter.filter(priority, tag) : filter(priority, tag);
    }

    /**
     * Each log will use this pipeline
     *
     * @param priority is the log level e.g. DEBUG, WARNING
     * @param tag      is the given tag for the log message.
     * @param message  is the given message for the log message.
     */
    final synchronized void log(int priority, String tag, String message) {
        if (isLoggable(priority, tag)) {
            if (format != null) {
                tag = format.formatTag(priority, tag);
                message = format.format(message);
            } else {
                tag = formatTag(priority, tag);
                message = format(message);
            }
            if (strategy != null) {
                strategy.print(priority, tag, message);
            } else {
                print(priority, tag, message);
            }
        }
    }

    protected String formatTag(int priority, String tag) {
        return tag;
    }

    protected String format(String msg) {
        return msg;
    }

    protected boolean filter(int priority, String tag) {
        return true;
    }

    @Override
    public int hashCode() {
        return getClass().getName().hashCode();
    }
}
