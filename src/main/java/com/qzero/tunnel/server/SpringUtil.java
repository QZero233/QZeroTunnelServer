package com.qzero.tunnel.server;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContextStatic;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        applicationContextStatic=applicationContext;
    }

    public static <T> T getBean(Class<T> type) {
        if (applicationContextStatic == null) {
            throw new RuntimeException("applicationContext has not been injected");
        }
        return applicationContextStatic.getBean(type);
    }
}
