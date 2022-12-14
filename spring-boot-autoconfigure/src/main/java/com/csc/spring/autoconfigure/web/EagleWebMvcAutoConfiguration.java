package com.csc.spring.autoconfigure.web;

import com.csc.common.constant.CharacterInfo;
import com.csc.spring.autoconfigure.InitializingAutoConfig;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Objects;

/**
 * @author: csc
 * @description: webmvc自动化配置
 * @create: 2022/12/13
 */
@AutoConfiguration
@EnableConfigurationProperties(WebProperties.class)
public class EagleWebMvcAutoConfiguration implements WebMvcConfigurer, InitializingAutoConfig {

    private WebProperties webProperties;
    /**
     * 忽略URL前缀的控制器类
     */
    private static String[] ignoreUrlPrefixController = new String[]{
            "springfox.documentation.swagger.web.ApiResourceController",
            "org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController"};

    public EagleWebMvcAutoConfiguration(WebProperties webProperties) {
        this.webProperties = webProperties;
    }

    /**
     * 配置路由规则
     *
     * @param configurer
     */
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        WebProperties.Path webPropertiesPath = webProperties.getPath();

        AntPathMatcher matcher = getAntPathMatcher(webPropertiesPath);
        //设置路由匹配规则
        configurer.setPathMatcher(matcher);
        //设置URL末尾是否支持斜杠，默认true,如/a/b/有效，/a/b也有效
        configurer.setUseTrailingSlashMatch(webPropertiesPath.isUseTrailingSlashMatch());
        //忽略URL前缀的控制器类
        ignoreUrlPrefixController = ArrayUtils.addAll(ignoreUrlPrefixController, webPropertiesPath.getExcludes().toArray(new String[]{}));
        //给所有的接口统一添加前缀
        configurer.addPathPrefix(webPropertiesPath.getPrefix(),
                c -> !ArrayUtils.contains(ignoreUrlPrefixController, c.getName()) && (BooleanUtils.isTrue(webPropertiesPath.isEnableAllPrefix())));
    }

    private AntPathMatcher getAntPathMatcher(WebProperties.Path webPropertiesPath) {
        AntPathMatcher matcher = new AntPathMatcher();
        //区分大小写,默认true
        matcher.setCaseSensitive(webPropertiesPath.isCaseSensitive());
        //是否去除前后空格,默认false
        matcher.setTrimTokens(webPropertiesPath.isTrimTokens());
        //分隔符
        matcher.setPathSeparator(CharacterInfo.PATH_SEPARATOR);
        //是否缓存匹配规则,默认null等于true
        matcher.setCachePatterns(webPropertiesPath.isCachePatterns());
        return matcher;
    }

    /**
     * 跨域设置
     * 在浏览器console控制台测试ajax示例
     * $.ajax({
     * url:"http://172.30.67.122:9000/api/void/test1",//发送的路径
     * type:"POST",//发送的方式
     * async:false,
     * data:JSON.stringify({'name':'test','age':23}),//发送的数据
     * contentType: "application/json", //提交数据类型
     * dataType:"json",//服务器返回的数据类型
     * success: function(data) {
     * console.log(data)
     * },
     * error: function (data){
     * alert("提交失败");
     * }
     * });
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        if (BooleanUtils.isFalse(webProperties.getCors().isEnable())) {
            return;
        }
        //启用跨域匹配的路径，默认所有请求，示例：/admin或/admin/**
        CorsRegistration registration = registry.addMapping("/**");
        //允许来自所有域名请求
        if (!webProperties.getCors().getAllowedOrigins().isEmpty()) {
            registration.allowedOrigins(webProperties.getCors().getAllowedOrigins().toArray(new String[]{}));
        } else {
            registration.allowedOriginPatterns("*");
        }
        //设置所允许的HTTP请求方法，*号代表允许所有方法
        if (!webProperties.getCors().getAllowedMethods().isEmpty()) {
            registration.allowedMethods(webProperties.getCors().getAllowedMethods().toArray(new String[]{}));
        } else {
            registration.allowedMethods("OPTIONS", "GET", "PUT", "POST");
        }
        //服务器支持的所有头信息字段，多个字段用逗号分隔；默认支持所有，*号代表所有
        if (!webProperties.getCors().getAllowedHeaders().isEmpty()) {
            registration.allowedHeaders(webProperties.getCors().getAllowedHeaders().toArray(new String[]{}));
        } else {
            registration.allowedHeaders("*");
        }
        //浏览器是否应该发送凭据，如是否允许发送Cookie，true为允许
        if (BooleanUtils.isFalse(webProperties.getCors().isAllowCredentials())) {
            registration.allowCredentials(false);
        } else {
            registration.allowCredentials(true);
        }
        //设置响应HEAD,默认无任何设置，不可以使用*号
        if (!webProperties.getCors().getExposedHeaders().isEmpty()) {
            registration.exposedHeaders(webProperties.getCors().getExposedHeaders().toArray(new String[]{}));
        }
        //设置多长时间内不需要发送预检验请求，可以缓存该结果，默认1800秒
        if (Objects.nonNull(webProperties.getCors().getMaxAge())) {
            registration.maxAge(webProperties.getCors().getMaxAge());
        }
    }
}
