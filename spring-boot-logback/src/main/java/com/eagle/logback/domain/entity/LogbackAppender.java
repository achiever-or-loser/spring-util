package com.eagle.logback.domain.entity;


import com.eagle.logback.domain.enumeration.LogbackType;

/**
 * @Description: 日志滚动帮助类
 * @create: 2022/11/18
*/
public class LogbackAppender {
    /**
     * appender名称
     */
    private String appenderName;
    /**
     * 日志路径
     */
    private String filePath;
    /**
     * 日志文件名
     */
    private String fileName;
    /**
     * 日志类型
     */
    private LogbackType logbackType;

    public LogbackAppender(String appenderName, String filePath, LogbackType logbackType) {
        this(appenderName, filePath, null, logbackType);
    }

    public LogbackAppender(String appenderName, String filePath, String fileName, LogbackType logbackType) {
        this.appenderName = appenderName;
        this.filePath = filePath;
        this.fileName = fileName;
        this.logbackType = logbackType;
    }

    public String getAppenderName() {
        return appenderName;
    }

    public void setAppenderName(String appenderName) {
        this.appenderName = appenderName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }


    public LogbackType getLogbackType() {
        return logbackType;
    }

    public void setLogbackType(LogbackType logbackType) {
        this.logbackType = logbackType;
    }


}
