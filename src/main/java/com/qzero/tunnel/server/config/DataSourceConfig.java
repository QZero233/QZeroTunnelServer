package com.qzero.tunnel.server.config;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    private final MysqlConfig mysqlConfig;

    public DataSourceConfig(MysqlConfig mysqlConfig) {
        this.mysqlConfig = mysqlConfig;
    }

    @Bean
    public DataSource getDataSource() {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();

        String url=mysqlConfig.getUrl();
        if(url==null || url.equals(""))
            url=generateUrl();

        dataSourceBuilder.url(url);
        dataSourceBuilder.username(mysqlConfig.getUsername());
        dataSourceBuilder.password(mysqlConfig.getPassword());
        return dataSourceBuilder.build();
    }

    private String generateUrl(){
        return "jdbc:mysql://"+mysqlConfig.getIp()+":"+mysqlConfig.getPort()+"/"+mysqlConfig.getDbname();
    }


}
