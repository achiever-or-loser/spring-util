package com.csc.spring.logback.appender;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.util.OptionHelper;
import com.csc.spring.logback.LogbackProperties;
import com.csc.spring.logback.encoder.LogbackEncoder;
import com.csc.spring.logback.entity.LogbackAppender;
import com.csc.spring.logback.entity.LogbackUrl;
import com.csc.spring.logback.enumeration.LogbackType;
import com.csc.spring.logback.filter.LogbackFilter;
import com.csc.spring.logback.policy.LogbackRollingPolicy;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.text.MessageFormat;

/**
 * @Description: 通过名字和级别设置Appender
 *
 * @create: 2022/11/18
*/
public class LogbackRollingFileAppenderImpl extends AbstractAppender {

    private LogbackAppender appender;

    public LogbackRollingFileAppenderImpl(LoggerContext loggerContext, LogbackProperties properties, LogbackAppender appender) {
        super(loggerContext, properties);
        this.appender = appender;
    }

    /**
     * 获取按照时间归档文件附加器对象
     *
     * @return
     */
    @Override
    protected Appender<ILoggingEvent> getAppender(Level level) {
        //这里是可以用来设置appender的，在xml配置文件里面，是这种形式：
        RollingFileAppender<ILoggingEvent> rollingFileAppender = new RollingFileAppender<>();
        //日志文件路径
        String loggerPath = this.getFilePath(level);
        //设置文件名
        rollingFileAppender.setFile(OptionHelper.substVars(MessageFormat.format("{0}{1}", loggerPath, ".log"), this.getLoggerContext()));
        //设置日志文件归档策略
        rollingFileAppender.setRollingPolicy(LogbackRollingPolicy.getInstance(this.getLoggerContext(), this.getProperties(), rollingFileAppender, loggerPath));
        //设置上下文，每个logger都关联到logger上下文，默认上下文名称为default。
        // 但可以使用<contextName>设置成其他名字，用于区分不同应用程序的记录。一旦设置，不能修改。
        rollingFileAppender.setContext(this.getLoggerContext());
        //appender的name属性
        rollingFileAppender.setName(this.getAppenderName(level));
        //如果是 true，日志被追加到文件结尾，如果是 false，清空现存文件，默认是true
        rollingFileAppender.setAppend(this.getProperties().getAppender().isAppend());
        //如果是 true，日志会被安全的写入文件，即使其他的FileAppender也在向此文件做写入操作，效率低，默认是 false|Support multiple-JVM writing to the same log file
        rollingFileAppender.setPrudent(this.getProperties().getAppender().isPrudent());
        //设置过滤器
        rollingFileAppender.addFilter(LogbackFilter.getLevelFilter(level));
        //设置附加器编码
        rollingFileAppender.setEncoder(LogbackEncoder.getPatternLayoutEncoder(this.getLoggerContext(), this.getFilePattern()));
        //设置是否将输出流刷新，确保日志信息不丢失，默认：true
        rollingFileAppender.setImmediateFlush(this.getProperties().getAppender().isImmediateFlush());
        rollingFileAppender.start();

        return rollingFileAppender;
    }

    /**
     * 获取文件路径
     *
     * @param level 日志级别
     * @return
     */
    @Override
    protected String getFilePath(Level level) {
        //基础相对路径
        String basePath = this.getProperties().getAppender().getPath();
        //文件路径
        String filePath = LogbackUrl.normalizePath(appender.getFilePath());
        //日志级别
        String levelStr = level.levelStr.toLowerCase();
        //基础日志
        if (LogbackType.ROOT.equals(appender.getLogbackType())) {
            return StringUtils.join(basePath, filePath, File.separator, levelStr, File.separator, levelStr);
        }
        //分模块日志
        if (LogbackType.MODULE.equals(appender.getLogbackType())) {
            return StringUtils.join(basePath, filePath, File.separator, appender.getFileName());
        }
        //分组日志
        if (StringUtils.isEmpty(appender.getFileName())) {
            return StringUtils.join(basePath, filePath, File.separator, levelStr, File.separator, levelStr);
        }
        return StringUtils.join(basePath, filePath, File.separator, levelStr, File.separator, appender.getFileName());
    }

    /**
     * 获取日志输出格式
     *
     * @return 日志格式
     */
    @Override
    protected String getFilePattern() {
        //基础日志
        if (LogbackType.ROOT.equals(appender.getLogbackType())) {
            return this.getProperties().getRoot().getPattern();
        }
        //分组
        if (LogbackType.GROUP.equals(appender.getLogbackType())) {
            return this.getProperties().getGroup().getPattern();
        }
        //分模块
        return this.getProperties().getModule().getPattern();
    }

    /**
     * 日志级别
     *
     * @param level 日志级别
     * @return
     */
    @Override
    protected String getAppenderName(Level level) {
        return MessageFormat.format("{0}_{1}", appender.getAppenderName(), level.levelStr.toLowerCase());
    }
}
