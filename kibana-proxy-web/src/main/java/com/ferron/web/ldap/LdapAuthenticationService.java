package com.ferron.web.ldap;

import com.ferron.web.entity.User;
import com.ferron.web.entity.WebConfig;
import com.ferron.web.util.AESUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Properties;

@Service
@Slf4j
public class LdapAuthenticationService {

    @Autowired
    WebConfig webConfig;

    @Autowired
    private User user;

    public User authenticate ( final String username, final String password ) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {

        LdapContext ctx = null;
        Control[] connCtls = null;
        String version = "";


        try {
            Properties props = new Properties();
            props.put(Context.INITIAL_CONTEXT_FACTORY,webConfig.getLdap().getInitialContextFactory());
            props.put(Context.PROVIDER_URL, webConfig.getLdap().getUrl());
            props.put(Context.SECURITY_AUTHENTICATION, webConfig.getLdap().getAuthentication());
            props.put(Context.SECURITY_PROTOCOL,webConfig.getLdap().getProtocal());
            props.put(Context.SECURITY_PRINCIPAL, webConfig.getLdap().getUser());
            byte[] passwordBytes = AESUtils.dncrypt(Base64.getDecoder().decode(webConfig.getLdap().getPassword()),webConfig.getLdap().getKey().getBytes());
            props.put(Context.SECURITY_CREDENTIALS, new String(passwordBytes));
            props.put("java.naming.ldap.version", version);
            ctx = new InitialLdapContext(props, connCtls);
        } catch (NamingException e) {
            log.error("Error: {}", e.toString());
        }

        try {
            ctx.addToEnvironment(Context.SECURITY_PRINCIPAL, username);

            if(password == null || "".equals(password)) {
                log.error("The password does not allow blank for : {}", username);
                user.setLoginFlag("N");
                return user;
            }

            ctx.addToEnvironment(Context.SECURITY_CREDENTIALS, password);
            ctx.reconnect(null);
            findUser(username, ctx);
            ctx.close();
            return user;

        } catch (NullPointerException e) {
            log.error("Error listing attribute.");
            user.setLoginFlag("N");
            return user;
        } catch (AuthenticationException e) {
            log.error("AuthenticationException : {} is not authenticated.", username);
            log.error("Error :" + e);
            user.setLoginFlag("N");
            return user;
        } catch (NamingException e) {
            log.error("NamingException : {} is not authenticated.", username);
            log.error("Error :" + e.toString());
            user.setLoginFlag("N");
            return user;
        }

    }

    public void findUser(String username, LdapContext ctx) throws NamingException {

        LdapContext dirContext = ctx;
        try {
            SearchControls controls = new SearchControls();
            controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            String filter = String.format("xxxxxxxx",username);
            NamingEnumeration<SearchResult> answer = ctx.search(webConfig.getLdap().getSearchBase(), filter, controls);
            log.info("Get user : {} informatio........", username);
            while (answer.hasMoreElements()) {
                SearchResult sr = answer.nextElement();
                Attributes Attrs = sr.getAttributes();

                String group = Attrs.get("memberOf") + "";
                if (group.contains(":")) {
                    String[] groups = group.substring( group.indexOf(":") + 1 ).trim().split(",");
                    for (String s : groups) {
                        for (WebConfig.mapEntry mapEntry : webConfig.getMappingList()) {
                            if ( mapEntry.getGroup().equals(s) ) {
                                user.setGroup(s);
                                user.setAccountUsername(mapEntry.getAccountUsername());
                                break;
                            }
                        }
                    }
                }

                String ad = Attrs.get("name") + "";
                if (ad.contains(":")) {
                    user.setUsername(ad.substring(ad.indexOf(":")).trim());
                }
                user.setLoginFlag("Y");

            }
        } catch (NamingException e) {
            log.error("Error : " + e);
        } finally {
            if (dirContext != null) {
                try {
                    dirContext.close();
                } catch (NamingException e) {
                    e.printStackTrace();
                }
            }
        }


    }



































}
