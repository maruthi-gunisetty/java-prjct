package com.inventory.config;

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.net.InetSocketAddress;
import java.net.Socket;

@Configuration
public class DataSourceConfig {

    private static final Logger log = LoggerFactory.getLogger(DataSourceConfig.class);

    @Value("${spring.datasource.mysql.host:127.0.0.1}")
    private String mysqlHost;

    @Value("${spring.datasource.mysql.port:3306}")
    private int mysqlPort;

    @Value("${spring.datasource.mysql.database:inventory_management}")
    private String mysqlDatabase;

    @Value("${spring.datasource.mysql.username:root}")
    private String mysqlUsername;

    @Value("${spring.datasource.mysql.password:}")
    private String mysqlPassword;

    @Bean
    @Primary
    public DataSource dataSource() {
        if (isPortReachable(mysqlHost, mysqlPort, 1200)) {
            log.info("Connecting to MySQL Database at {}:{}/{}", mysqlHost, mysqlPort, mysqlDatabase);
            HikariDataSource ds = new HikariDataSource();
            ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
            ds.setJdbcUrl("jdbc:mysql://" + mysqlHost + ":" + mysqlPort + "/" + mysqlDatabase 
                    + "?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
            ds.setUsername(mysqlUsername);
            ds.setPassword(mysqlPassword);
            ds.setMaximumPoolSize(10);
            ds.setMinimumIdle(2);
            return ds;
        }

        log.info("MySQL not reachable on port {}. Initializing embedded database for inventory_management.", mysqlPort);
        HikariDataSource h2Ds = new HikariDataSource();
        h2Ds.setDriverClassName("org.h2.Driver");
        h2Ds.setJdbcUrl("jdbc:h2:mem:inventory_management;DB_CLOSE_DELAY=-1;MODE=MySQL;NON_KEYWORDS=KEY,VALUE");
        h2Ds.setUsername("sa");
        h2Ds.setPassword("");
        return h2Ds;
    }

    private boolean isPortReachable(String host, int port, int timeoutMs) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), timeoutMs);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

