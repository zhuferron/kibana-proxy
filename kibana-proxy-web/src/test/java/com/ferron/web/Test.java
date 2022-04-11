package com.ferron.web;

import com.ferron.web.util.AESUtils;
import org.springframework.boot.test.context.SpringBootTest;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@SpringBootTest
public class Test {


    @org.junit.jupiter.api.Test
    public void test1() throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {

        String a = "elastic"; //原文
        byte[] a1= AESUtils.encrypt(a.getBytes(), "ada64fa45".getBytes()); //密文
        String b = new String(a1);
        byte[] a2 = Base64.getEncoder().encode(a1); //编码后密文
        String c = new String(a2);
        byte[] c1 =  Base64.getDecoder().decode(c.getBytes()); //解码后密文
        byte[] c2 = AESUtils.dncrypt(c1, "ada64fa45".getBytes()); //原文
        String d = new String(c2);

        System.out.println(a);//原文
        System.out.println(b);//密文
        System.out.println(c);//编码后密文
        System.out.println(new String(c1));//解码后密文
        System.out.println(d);


    }


}
