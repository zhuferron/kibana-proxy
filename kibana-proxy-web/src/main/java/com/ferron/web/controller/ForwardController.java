package com.ferron.web.controller;

import com.ferron.web.entity.Error;
import com.ferron.web.entity.User;
import com.ferron.web.entity.WebConfig;
import com.ferron.web.ldap.LdapAuthenticationService;
import com.ferron.web.ldap.LdapTest;
import com.ferron.web.util.AESUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Controller
@Slf4j
public class ForwardController {

    @Autowired
    LdapAuthenticationService ldapAuthenticationService;

    @Autowired
    LdapTest ldapTest;

    @Autowired
    WebConfig webConfig;

    @PostMapping("/authenticate")
    public String authenticate(@RequestParam("username") String username, @RequestParam("password") String password, Model model, HttpServletResponse httpServletResponse) throws InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException {

        User user = ldapTest.authenticate(username, password);
        System.out.println(user.toString());

        if( "Y".equals(user.getLoginFlag()) && user.getGroup() != null ) {
            //successfully login in
            log.info("Successfully login in , username is {}", username);

            //proxy host
            model.addAttribute("string","0.1;url=http://"+webConfig.getProxyHost()+":9001/login");

            //cookie config
            byte[] cipherBytes = AESUtils.encrypt(user.getAccountUsername().getBytes(), webConfig.getCookieConfig().getEncryptKey().getBytes());
            String cookieString = new String(Base64.getEncoder().encode(cipherBytes));
            Cookie cookie = new Cookie("AccountUsername", cookieString);
            cookie.setSecure(webConfig.getCookieConfig().isSecure());
            cookie.setMaxAge(60 * 60 * webConfig.getCookieConfig().getMaxAge());
            httpServletResponse.addCookie(cookie);
            return "success";
        } else if ( user.getGroup() == null && user.getUsername() != null ) {
            log.info("Fail to login in, username is {}", username);
            model.addAttribute("error", Error.INVAILD_GROUP);
            return "login";
        } else {
            log.info("Fail to login in, username is {}", username);
            model.addAttribute("error", Error.INVAILD_USERNAME_OR_PASSWORD);
            return "login";
        }

    }

}
