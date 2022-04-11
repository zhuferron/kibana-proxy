package com.ferron.gateway.util;

import org.springframework.stereotype.Service;

import javax.crypto.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

@Service
public class AESUtils {

    private static final int KEY_SIZE = 128;
    private static final String ALGORITHM = "AES";
    private static final String RNG_ALGORITHM = "SHA1PRNG";

    private static SecretKey generateKey ( byte[] key ) throws NoSuchAlgorithmException {

        SecureRandom random = SecureRandom.getInstance(RNG_ALGORITHM);
        random.setSeed(key);

        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
        keyGenerator.init(KEY_SIZE, random);

        return keyGenerator.generateKey();

    }

    public static byte[] encrypt( byte[] plainBytes, byte[] key ) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {

        SecretKey secretKey = generateKey(key);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE,secretKey);
        byte[] cipherBytes = cipher.doFinal(plainBytes);
        return cipherBytes;
    }

    public static byte[] dncrypt( byte[] cipherBytes, byte[] key ) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {

        SecretKey secretKey = generateKey(key);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE,secretKey);
        byte[] plainBytes = cipher.doFinal(cipherBytes);
        return plainBytes;
    }





}
