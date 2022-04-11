package com.ferron.web.entity;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class User {

    private String username;
    private String loginFlag;
    private String group;
    private String accountUsername;

}
