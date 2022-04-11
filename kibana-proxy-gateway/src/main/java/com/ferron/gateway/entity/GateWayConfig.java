package com.ferron.gateway.entity;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Data
@Component
@ConfigurationProperties( prefix = "gatewayconfig")
public class GateWayConfig {

    private String cookieKey;
    private String proxyKibana;
    private List<Account> accountList = new ArrayList<>();

    @Data
    public static class Account {
        private String username;
        private String password;
    }



}
