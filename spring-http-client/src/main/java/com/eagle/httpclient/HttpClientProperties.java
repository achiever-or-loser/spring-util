package com.eagle.httpclient;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = HttpClientProperties.PREFIX)
public class HttpClientProperties {
    /**
     * 属性配置前缀
     */
    public static final String PREFIX = "eagle.http-client";
    /**
     * 组件开关
     */
    private boolean enabled;
    /**
     * HttpClientService read timeout (in milliseconds),default:5000
     */
    private Integer readTimeOut = 5000;
    /**
     * HttpClientService connect timeout (in milliseconds),default:10000
     */
    private Integer connectTimeOut = 10000;
    /**
     * 开启调用接口拦截器
     */
    private boolean interceptor = true;
    /**
     * 是否开启SSL，默认：true
     */
    private boolean ssl = true;

    public Integer getReadTimeOut() {
        return readTimeOut;
    }

    public void setReadTimeOut(Integer readTimeOut) {
        this.readTimeOut = readTimeOut;
    }

    public Integer getConnectTimeOut() {
        return connectTimeOut;
    }

    public void setConnectTimeOut(Integer connectTimeOut) {
        this.connectTimeOut = connectTimeOut;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isInterceptor() {
        return interceptor;
    }

    public void setInterceptor(boolean interceptor) {
        this.interceptor = interceptor;
    }

    public boolean isSsl() {
        return ssl;
    }

    public void setSsl(boolean ssl) {
        this.ssl = ssl;
    }
}
