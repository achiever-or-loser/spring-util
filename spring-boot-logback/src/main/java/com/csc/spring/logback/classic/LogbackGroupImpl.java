package com.csc.spring.logback.classic;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.csc.spring.logback.appender.AbstractAppender;
import com.csc.spring.logback.appender.LogbackAsyncAppender;
import com.csc.spring.logback.appender.LogbackConsoleAppenderImpl;
import com.csc.spring.logback.appender.LogbackRollingFileAppenderImpl;
import com.csc.spring.logback.entity.LogbackAppender;
import com.csc.spring.logback.enumeration.LogbackType;

/**
 * @Description: 分组记录日志
 *
 * @create: 2022/11/18
*/
public class LogbackGroupImpl extends AbstractLogback {

    public LogbackGroupImpl(com.csc.spring.logback.LogbackProperties properties) {
        super(properties);
    }

    /**
     * 构建Logger对象
     * 日志级别以及优先级排序: OFF > ERROR > WARN > INFO > DEBUG > TRACE >ALL
     *
     * @param fileName 日志文件名|模块名称
     * @return
     */
    @Override
    public Logger getLogger(String loggerName, String appenderName, String filePath, String fileName) {
        // 获取logger对象
        Logger logger = this.getLoggerContext().getLogger(loggerName);
        // 设置是否向上级打印信息
        logger.setAdditive(false);
        // 设置日志级别
        logger.setLevel(Level.toLevel(this.getProperties().getGroup().getLevel().levelStr));
        // 获取帮助类对象
        LogbackAppender appender = new LogbackAppender(appenderName, filePath, fileName, LogbackType.GROUP);
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
        if (this.getProperties().getGroup().isConsole()) {
            // 添加控制台appender
            logger.addAppender(new LogbackConsoleAppenderImpl(this.getLoggerContext(), this.getProperties()).getInstance(logger.getLevel()));
        }

        return logger;
    }
}
