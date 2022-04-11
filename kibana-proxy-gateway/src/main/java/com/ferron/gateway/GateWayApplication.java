package com.ferron.gateway;

import com.ferron.gateway.entity.GateWayConfig;
import com.ferron.gateway.filter.KibanaProxyFilter;
import com.ferron.gateway.util.SpringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

@SpringBootApplication(scanBasePackages = ("com.ferron.gateway"))
public class GateWayApplication {

    public static void main(String[] args) {

        SpringApplication.run(GateWayApplication.class, args);

    }

    @Bean
    public RouteLocator kibanaRouteLocator(RouteLocatorBuilder builder) {


        GateWayConfig accountList = ( GateWayConfig ) SpringUtils.getBean("gateWayConfig");

        return builder.routes()
                .route("kibana-proxy",r -> r.alwaysTrue()
                        .filters(f -> f.filter(new KibanaProxyFilter()))
                        .uri(accountList.getProxyKibana()))
                .build();

    }




}
