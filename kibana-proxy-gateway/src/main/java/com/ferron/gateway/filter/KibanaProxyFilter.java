package com.ferron.gateway.filter;

import com.ferron.gateway.entity.GateWayConfig;
import com.ferron.gateway.util.AESUtils;
import com.ferron.gateway.util.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


import java.util.Base64;
@Slf4j
public class KibanaProxyFilter implements GatewayFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        GateWayConfig accountList = ( GateWayConfig ) SpringUtils.getBean("gateWayConfig");

        HttpCookie cookie = exchange.getRequest().getCookies().getFirst("AccountUsername");
        byte[] decodeCipherBytes;
        try {
            decodeCipherBytes = Base64.getDecoder().decode(cookie.getValue());
        } catch (NullPointerException e) {
            decodeCipherBytes = null;
            log.error("There is a NullPointerException");
        }
        String accountUsername = "";
        try {
            byte[] cipherBytes = AESUtils.dncrypt(decodeCipherBytes, accountList.getCookieKey().getBytes());
            accountUsername = new String(cipherBytes);
        } catch (Exception e) {
            log.info("Fail to cookie parse");
        }


        if(!"".equals(accountUsername)) {
            for (GateWayConfig.Account account : accountList.getAccountList()) {
                if(accountUsername.equals(account.getUsername())) {
                    String auth = String.format("Basic %s", account.getPassword());

                    ServerWebExchange modifiedExchange = exchange.mutate()
                            .request(originalRequest -> originalRequest.header("Authorization", auth))
                            .build();

                    return chain.filter(modifiedExchange);
                }
            }
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
