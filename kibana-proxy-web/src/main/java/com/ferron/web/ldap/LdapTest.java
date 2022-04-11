package com.ferron.web.ldap;

import com.ferron.web.entity.User;
import com.ferron.web.entity.WebConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
@Slf4j
public class LdapTest {

    @Autowired
    WebConfig webConfig;

    @Autowired
    private User user;

    public User authenticate ( final String username, final String password ) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {

        if ("admin".equals(username) && "123456".equals(password)) {
            user.setUsername("sanfashi");
            user.setLoginFlag("Y");
            user.setGroup("admin");

            for (WebConfig.mapEntry mapEntry : webConfig.getMappingList()) {
                if (mapEntry.getGroup().equals(user.getGroup())) {
                    user.setAccountUsername(mapEntry.getAccountUsername());
                    break;
                }
            }
        } else {
            user.setLoginFlag("N");
        }

        return user;
    }

}
