package com.illtamer.infinite.bot.minecraft.expansion;

import com.illtamer.infinite.bot.minecraft.api.IExpansion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

public class ExpansionLogger implements Logger {
    private final Logger logger;
    private final String expansionName;
    private final String prefix;
    private boolean debug = false;

    public ExpansionLogger(IExpansion expansion) {
        String name = expansion.getExpansionName();
        this.expansionName = (name != null && !name.isEmpty()) ? name : expansion.getClass().getSimpleName();
        this.prefix = "[" + expansionName + ']';
        this.logger = LoggerFactory.getLogger(prefix);
    }

    @Override
    public String getName() {
        return expansionName;
    }

    @Override
    public boolean isTraceEnabled() {
        return logger.isTraceEnabled();
    }

    @Override
    public void trace(String s) {
        logger.trace(prefix + s);
    }

    @Override
    public void trace(String s, Object o) {
        logger.trace(prefix + s, o);
    }

    @Override
    public void trace(String s, Object o, Object o1) {
        logger.trace(prefix + s, o, o1);
    }

    @Override
    public void trace(String s, Object... objects) {
        logger.trace(prefix + s, objects);
    }

    @Override
    public void trace(String s, Throwable throwable) {
        logger.trace(prefix + s, throwable);
    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
        return false;
    }

    @Override
    public void trace(Marker marker, String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void trace(Marker marker, String s, Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void trace(Marker marker, String s, Object o, Object o1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void trace(Marker marker, String s, Object... objects) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void trace(Marker marker, String s, Throwable throwable) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isDebugEnabled() {
        return debug;
    }

    @Override
    public void debug(String s) {
        if (debug) {
            logger.debug(prefix + s);
        }
    }

    @Override
    public void debug(String s, Object o) {
        if (debug) {
            logger.debug(prefix + s, o);
        }
    }

    @Override
    public void debug(String s, Object o, Object o1) {
        if (debug) {
            logger.debug(prefix + s, o, o1);
        }
    }

    @Override
    public void debug(String s, Object... objects) {
        if (debug) {
            logger.debug(prefix + s, objects);
        }
    }

    @Override
    public void debug(String s, Throwable throwable) {
        if (debug) {
            logger.debug(prefix + s, throwable);
        }
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        return false;
    }

    @Override
    public void debug(Marker marker, String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void debug(Marker marker, String s, Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void debug(Marker marker, String s, Object o, Object o1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void debug(Marker marker, String s, Object... objects) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void debug(Marker marker, String s, Throwable throwable) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isInfoEnabled() {
        return true;
    }

    public void info(String msg) {
        logger.info(prefix + msg);
    }

    @Override
    public void info(String s, Object o) {
        logger.info(prefix + s, o);
    }

    @Override
    public void info(String s, Object o, Object o1) {
        logger.info(prefix + s, o, o1);
    }

    @Override
    public void info(String s, Object... objects) {
        logger.info(prefix + s, objects);
    }

    @Override
    public void info(String s, Throwable throwable) {
        logger.info(prefix + s, throwable);
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        return false;
    }

    @Override
    public void info(Marker marker, String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void info(Marker marker, String s, Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void info(Marker marker, String s, Object o, Object o1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void info(Marker marker, String s, Object... objects) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void info(Marker marker, String s, Throwable throwable) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isWarnEnabled() {
        return true;
    }

    @Override
    public void warn(String s) {
        logger.warn(prefix + s);
    }

    @Override
    public void warn(String s, Object o) {
        logger.warn(prefix + s, o);
    }

    @Override
    public void warn(String s, Object... objects) {
        logger.warn(prefix + s, objects);
    }

    @Override
    public void warn(String s, Object o, Object o1) {
        logger.warn(prefix + s, o, o1);
    }

    @Override
    public void warn(String s, Throwable throwable) {
        logger.warn(prefix + s, throwable);
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
        return false;
    }

    @Override
    public void warn(Marker marker, String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void warn(Marker marker, String s, Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void warn(Marker marker, String s, Object o, Object o1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void warn(Marker marker, String s, Object... objects) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void warn(Marker marker, String s, Throwable throwable) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isErrorEnabled() {
        return true;
    }

    public void error(String msg) {
        logger.error(prefix + msg);
    }

    @Override
    public void error(String s, Object o) {
        logger.error(prefix + s, o);
    }

    @Override
    public void error(String s, Object o, Object o1) {
        logger.error(prefix + s, o, o1);
    }

    @Override
    public void error(String s, Object... objects) {
        logger.error(prefix + s, objects);
    }

    @Override
    public void error(String s, Throwable throwable) {
        logger.error(prefix + s, throwable);
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        return false;
    }

    @Override
    public void error(Marker marker, String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void error(Marker marker, String s, Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void error(Marker marker, String s, Object o, Object o1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void error(Marker marker, String s, Object... objects) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void error(Marker marker, String s, Throwable throwable) {
        throw new UnsupportedOperationException();
    }

    public ExpansionLogger setDebug(boolean debug) {
        this.debug = debug;
        return this;
    }
}
