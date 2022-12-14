package com.csc.spring.httpclient;

import com.csc.spring.httpclient.annotation.TargetHttpTimeout;
import com.csc.spring.httpclient.factory.HttpContextFactory;
import com.csc.spring.httpclient.handler.CustomResponseErrorHandler;
import com.csc.spring.httpclient.interceptor.client.DefaultHttpClientInterceptor;
import com.csc.spring.httpclient.interceptor.client.HttpClientCustomizer;
import com.csc.spring.httpclient.interceptor.timeout.DefaultHttpTimeoutMethodInterceptor;
import com.csc.spring.httpclient.interceptor.timeout.HttpTimeoutCustomizer;
import com.csc.spring.logback.LoggerFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContexts;
import org.slf4j.Logger;
import org.springframework.aop.Advisor;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Role;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;

/**
 * @author: csc
 * @Description: ???RestTemplate????????????
 */
@AutoConfiguration
@ConditionalOnClass(RestTemplate.class)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@EnableConfigurationProperties(HttpClientProperties.class)
@ConditionalOnProperty(prefix = HttpClientProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class HttpClientAutoConfiguration implements InitializingBean, DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(HttpClientAutoConfiguration.class);

    /**
     * ???RestTemplate???????????????????????????????????????????????????????????????????????????
     */
    @Primary
    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public RestTemplate restTemplate(ObjectProvider<HttpClientCustomizer> httpClientCustomizers, ClientHttpRequestFactory clientHttpRequestFactory, HttpClientProperties httpClientProperties) {
        RestTemplate restTemplate = new RestTemplate();
        //??????BufferingClientHttpRequestFactory???????????????????????????????????????????????????????????????;??????io?????????????????????
        restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(clientHttpRequestFactory));
        //???????????????????????????
        restTemplate.setErrorHandler(new CustomResponseErrorHandler());
        if (httpClientProperties.isInterceptor()) {
            //???????????????
            restTemplate.setInterceptors(Collections.singletonList(httpClientCustomizers.orderedStream().findFirst().get()));
        }

        return restTemplate;
    }

    /**
     * ??????HTTP??????????????????,??????????????????
     */
    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public ClientHttpRequestFactory clientHttpRequestFactory(HttpClientProperties properties) throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        //SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        //????????????5???,??????????????????,???????????????
        factory.setReadTimeout(properties.getReadTimeOut());
        //????????????10???????????????????????????????????????
        factory.setConnectTimeout(properties.getConnectTimeOut());
        //??????HTTP???????????????????????????
        factory.setHttpContextFactory(new HttpContextFactory());
        //??????HTTPS????????????
        if (properties.isSsl()) {
            TrustStrategy acceptingTrustStrategy = (x509Certificates, authType) -> true;
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(keyStore, acceptingTrustStrategy).build();
            SSLConnectionSocketFactory connectionSocketFactory = new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());
            factory.setHttpClient(HttpClientBuilder.create().setSSLSocketFactory(connectionSocketFactory).build());
        }
        return factory;
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @ConditionalOnMissingBean
    public DefaultHttpClientInterceptor httpClientInterceptor() {
        return new DefaultHttpClientInterceptor();
    }

    /**
     * RestTemplate????????????????????????????????????
     */
    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public Advisor httpTimeoutPointCutAdvice(ObjectProvider<HttpTimeoutCustomizer> httpTimeoutCustomizers) {
        //???????????????????????????
        Pointcut pointcut = AnnotationMatchingPointcut.forMethodAnnotation(TargetHttpTimeout.class);
//        Pointcut mpc = new AnnotationMatchingPointcut(null, TargetHttpTimeout.class, false);
        //????????????(??????)?????????????????????????????????????????????????????????
//        pointcut = new ComposablePointcut(pointcut);
        //???????????????
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor();
        advisor.setPointcut(pointcut);
        advisor.setAdvice(httpTimeoutCustomizers.orderedStream().findFirst().get());
//        AnnotationPointcutAdvisor advisor = new AnnotationPointcutAdvisor(customizers.orderedStream().findFirst().get(), pointcut);
        //?????????????????????
        advisor.setOrder(AopOrderInfo.HTTP_CLIENT);
        return advisor;
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @ConditionalOnMissingBean
    public HttpTimeoutCustomizer httpTimeoutCustomizer() {
        return new DefaultHttpTimeoutMethodInterceptor();
    }

    @Override
    public void destroy() {
        logger.info("<== ?????????--??????????????????----RestTemplate(HttpClient)?????????HttpClientAutoConfiguration???");
    }

    @Override
    public void afterPropertiesSet() {
        logger.info("==> ????????????--??????????????????----RestTemplate(HttpClient)?????????HttpClientAutoConfiguration???");
    }
}
