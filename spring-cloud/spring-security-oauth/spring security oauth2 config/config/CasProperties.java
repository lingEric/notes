package com.hand.oauth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;


@PropertySource(value = "classpath:config/cas.properties")
@ConfigurationProperties(prefix = "cas")
@Configuration
public class CasProperties {
    private String server;

    private String server_login;

    private String server_logout;

    private String service;

    private String service_login;

    private String service_logout;

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getServer_login() {
        return server_login;
    }

    public void setServer_login(String server_login) {
        this.server_login = server_login;
    }

    public String getServer_logout() {
        return server_logout;
    }

    public void setServer_logout(String server_logout) {
        this.server_logout = server_logout;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getService_login() {
        return service_login;
    }

    public void setService_login(String service_login) {
        this.service_login = service_login;
    }

    public String getService_logout() {
        return service_logout;
    }

    public void setService_logout(String service_logout) {
        this.service_logout = service_logout;
    }
}
