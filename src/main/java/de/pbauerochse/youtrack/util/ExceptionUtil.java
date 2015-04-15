package de.pbauerochse.youtrack.util;

/**
 * @author Patrick Bauerochse
 * @since 15.04.15
 */
public class ExceptionUtil {

    public static RuntimeException getRuntimeException(String messageKey, Object... params) {
        return getRuntimeException(messageKey, null, params);
    }

    public static RuntimeException getRuntimeException(String messageKey, Throwable t) {
        return new RuntimeException(FormattingUtil.getFormatted(messageKey), t);
    }

    public static RuntimeException getRuntimeException(String messageKey, Throwable t, Object... params) {
        return new RuntimeException(FormattingUtil.getFormatted(messageKey, params), t);
    }

    public static IllegalArgumentException getIllegalArgumentException(String messageKey, Object... params) {
        return getIllegalArgumentException(messageKey, null, params);
    }

    public static IllegalArgumentException getIllegalArgumentException(String messageKey, Throwable t, Object... params) {
        return new IllegalArgumentException(FormattingUtil.getFormatted(messageKey, params), t);
    }

    public static IllegalStateException getIllegalStateException(String messageKey, Object... params) {
        return getIllegalStateException(messageKey, null, params);
    }

    public static IllegalStateException getIllegalStateException(String messageKey, Throwable t, Object... params) {
        return new IllegalStateException(FormattingUtil.getFormatted(messageKey, params), t);
    }

}
