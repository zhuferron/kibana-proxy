package com.ferron.web.entity;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Data
@ConfigurationProperties("webconfig")
@Component
public class WebConfig {

    private String proxyHost;
    private Ldap ldap = new Ldap();
    private CookieConfig cookieConfig = new CookieConfig();
    private List<mapEntry> mappingList = new ArrayList<>();

    @Data
    public static class Ldap {
        private String url;
        private String password;
        private String key;
        private String authentication;
        private String protocal;
        private String user;
        private String initialContextFactory;
        private String searchBase;
    }

    @Data
    public static class CookieConfig {
        private Integer maxAge;
        private boolean isSecure;
        private String encryptKey;
    }

    @Data
    public static class mapEntry {
        private String accountUsername;
        private String group;
    }






}
