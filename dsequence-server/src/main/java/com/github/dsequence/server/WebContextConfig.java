package com.github.dsequence.server;

import cz.jirutka.spring.exhandler.RestHandlerExceptionResolver;
import cz.jirutka.spring.exhandler.support.HttpMessageConverterUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import java.util.List;

/**
 * WebContextConfig 配置
 *
 * @author ChenWoZe(YAN.WANG)
 * @version 1.0.0 createTime: 2016/4/15
 * @see WebMvcAutoConfiguration.WebMvcAutoConfigurationAdapter
 */
@Slf4j
@EnableWebMvc
@Configuration
@ImportResource({"classpath:spring/spring-extra.xml"})
public class WebContextConfig extends WebMvcAutoConfiguration.WebMvcAutoConfigurationAdapter {

    @Bean
    public BeanPostProcessor dispatcherServletConfig() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
                if (bean instanceof DispatcherServlet) {
                    ((DispatcherServlet) bean).setThrowExceptionIfNoHandlerFound(true);
                    log.info("set dispatcherServlet.throwExceptionIfNoHandlerFound=true");
                }
                return bean;
            }

            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                return bean;
            }
        };
    }

    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {

        resolvers.add(exceptionHandlerExceptionResolver());
        resolvers.add(restExceptionResolver());
    }

    @Bean
    public RestHandlerExceptionResolver restExceptionResolver() {
        return RestHandlerExceptionResolver.builder()
                .messageSource(httpErrorMessageSource())
                .defaultContentType(MediaType.APPLICATION_JSON)
                        //.addErrorMessageHandler(EmptyResultDataAccessException.class, HttpStatus.NOT_FOUND)
                        //.addHandler(ServerException.class, new ServerExceptionHandler())
                .withDefaultHandlers(true)
                .withDefaultMessageSource(true)
                .build();
    }

    @Bean
    public MessageSource httpErrorMessageSource() {
        ReloadableResourceBundleMessageSource m = new ReloadableResourceBundleMessageSource();
        m.setBasenames("classpath:i18n/messages");
        m.setDefaultEncoding("UTF-8");
        return m;
    }

    @Bean
    public ExceptionHandlerExceptionResolver exceptionHandlerExceptionResolver() {
        ExceptionHandlerExceptionResolver resolver = new ExceptionHandlerExceptionResolver();
        resolver.setMessageConverters(HttpMessageConverterUtils.getDefaultHttpMessageConverters());
        return resolver;
    }

}