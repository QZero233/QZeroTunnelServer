package com.qzero.tunnel.server.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.Ssl;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SSLConfig {

    @Autowired
    private CustomizedSSLConfig sslConfig;

    @Bean
    public WebServerFactoryCustomizer webServerFactoryCustomizer() {
        return new WebServerFactoryCustomizer<ConfigurableServletWebServerFactory>() {
            @Override
            public void customize(ConfigurableServletWebServerFactory factory) {
                Ssl ssl=new Ssl();
                ssl.setKeyStore(sslConfig.getKeyStorePath());
                ssl.setKeyStoreType(sslConfig.getKeyStoreType());
                ssl.setKeyStorePassword(sslConfig.getKeyStorePassword());
                ssl.setKeyAlias(sslConfig.getKeyAlias());
                ssl.setEnabled(sslConfig.isEnabled());

                factory.setSsl(ssl);
            }
        };
    }


}
