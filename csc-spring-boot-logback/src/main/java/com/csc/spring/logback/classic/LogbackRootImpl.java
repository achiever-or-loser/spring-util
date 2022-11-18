package com.csc.spring.logback.classic;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.core.util.StatusPrinter;
import com.csc.spring.logback.LogbackProperties;
import com.csc.spring.logback.appender.AbstractAppender;
import com.csc.spring.logback.appender.LogbackAsyncAppender;
import com.csc.spring.logback.appender.LogbackConsoleAppenderImpl;
import com.csc.spring.logback.appender.LogbackRollingFileAppenderImpl;
import com.csc.spring.logback.entity.LogbackAppender;
import com.csc.spring.logback.enumeration.LogbackType;

/**
 * @Description: 日志组件抽象类
 *
 * @create: 2022/11/18
*/
public class LogbackRootImpl extends AbstractLogback {

    public LogbackRootImpl(LogbackProperties properties) {
        super(properties);
    }

    /**
     * 构建RootLogger对象，需在配置类中主动调用进行初始化
     * 日志级别以及优先级排序: OFF > ERROR > WARN > INFO > DEBUG > TRACE >ALL
     */
    @Override
    public Logger getLogger() {
        // 获取logger对象
        Logger logger = this.getLoggerContext().getLogger(Logger.ROOT_LOGGER_NAME);
        //设置是否向上级打印信息
        logger.setAdditive(false);
        // 设置日志级别
        logger.setLevel(Level.toLevel(this.getProperties().getRoot().getLevel().levelStr));
        // 获取帮助类对象
        LogbackAppender appender = new LogbackAppender(Logger.ROOT_LOGGER_NAME, this.getProperties().getRoot().getFilePath(), LogbackType.ROOT);
        // appender对象
        AbstractAppender rollingFileAppender = new LogbackRollingFileAppenderImpl(this.getLoggerContext(), this.getProperties(), appender);
        // 是否开启异步日志
        if (this.getProperties().getAppender().getAsync().isEnabled()) {
            //异步appender
            LogbackAsyncAppender asyncAppender = new LogbackAsyncAppender(this.getLoggerContext(), this.getProperties());
            if (logger.getLevel().levelInt <= Level.ERROR_INT) {
                logger.addAppender(asyncAppender.getAppender(rollingFileAppender.getInstance(Level.ERROR)));
            }
            if (logger.getLevel().levelInt <= Level.WARN_INT) {
                logger.addAppender(asyncAppender.getAppender(rollingFileAppender.getInstance(Level.WARN)));
            }
            if (logger.getLevel().levelInt <= Level.INFO_INT) {
                logger.addAppender(asyncAppender.getAppender(rollingFileAppender.getInstance(Level.INFO)));
            }
            if (logger.getLevel().levelInt <= Level.DEBUG_INT) {
                logger.addAppender(asyncAppender.getAppender(rollingFileAppender.getInstance(Level.DEBUG)));
            }
            if (logger.getLevel().levelInt <= Level.TRACE_INT) {
                logger.addAppender(asyncAppender.getAppender(rollingFileAppender.getInstance(Level.TRACE)));
            }
        } else {
            if (logger.getLevel().levelInt <= Level.ERROR_INT) {
                logger.addAppender(rollingFileAppender.getInstance(Level.ERROR));
            }
            if (logger.getLevel().levelInt <= Level.WARN_INT) {
                logger.addAppender(rollingFileAppender.getInstance(Level.WARN));
            }
            if (logger.getLevel().levelInt <= Level.INFO_INT) {
                logger.addAppender(rollingFileAppender.getInstance(Level.INFO));
            }
            if (logger.getLevel().levelInt <= Level.DEBUG_INT) {
                logger.addAppender(rollingFileAppender.getInstance(Level.DEBUG));
            }
            if (logger.getLevel().levelInt <= Level.TRACE_INT) {
                logger.addAppender(rollingFileAppender.getInstance(Level.TRACE));
            }
        }
        if (!this.getProperties().getRoot().isConsole()) {
            //移除console控制台appender
            logger.detachAppender(LogbackConsoleAppenderImpl.CONSOLE_NAME);
        }
        //是否报告logback内部状态信息
        if (this.getProperties().getAppender().isReportState()) {
            StatusPrinter.print(this.getLoggerContext());
        }
        return logger;
    }
}
