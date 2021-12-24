package com.qzero.tunnel.server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:ssl.properties")
@ConfigurationProperties(prefix = "ssl")
public class CustomizedSSLConfig {

    private String keyStorePath;
    private String keyStoreType="JKS";
    private String keyStorePassword;
    private String keyAlias;

    private boolean enabled=false;

    public CustomizedSSLConfig() {
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getKeyStorePath() {
        return keyStorePath;
    }

    public void setKeyStorePath(String keyStorePath) {
        this.keyStorePath = keyStorePath;
    }

    public String getKeyStoreType() {
        return keyStoreType;
    }

    public void setKeyStoreType(String keyStoreType) {
        this.keyStoreType = keyStoreType;
    }

    public String getKeyStorePassword() {
        return keyStorePassword;
    }

    public void setKeyStorePassword(String keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
    }

    public String getKeyAlias() {
        return keyAlias;
    }

    public void setKeyAlias(String keyAlias) {
        this.keyAlias = keyAlias;
    }

    @Override
    public String toString() {
        return "CustomizedSSLConfig{" +
                "keyStorePath='" + keyStorePath + '\'' +
                ", keyStoreType='" + keyStoreType + '\'' +
                ", keyStorePassword='" + keyStorePassword + '\'' +
                ", keyAlias='" + keyAlias + '\'' +
                '}';
    }
}
