/**
 * Copyright (c) 2016, www.cubbery.com. All rights reserved.
 */
package com.cubbery.event.utils.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;

public class DesCrypter {
    private final static String DES_KEY = "zX/95tXv+4PTBD4xkm3992jpnqcynZ#@";
    private final static Logger LOG = LoggerFactory.getLogger(DesCrypter.class);
    //cipher 非线程安全
    private static ThreadLocal<Encrypter> desEncrypter = new ThreadLocal<Encrypter>(){
        @Override
        protected Encrypter initialValue() {
            try {
                return new Encrypter(DES_KEY);
            } catch (Exception e) {
                LOG.info("Encrypter Init Error!",e);
                return null;
            }
        }
    };
    private static ThreadLocal<Decrypter> desDecrypter = new ThreadLocal<Decrypter>(){
        @Override
        protected Decrypter initialValue() {
            try {
                return new Decrypter(DES_KEY);
            } catch (Exception e) {
                LOG.info("Decrypt Init Error!",e);
                return null;
            }
        }
    };

    public static String encrypt(String txt) {
        try {
            return desEncrypter.get().encrypt(txt);
        } catch (Exception e) {
            LOG.info("Encrypt Error ");
            throw new RuntimeException(e);
        }
    }

    public static String decrypt(String txt) {
        try {
            return desDecrypter.get().decrypt(txt);
        } catch (Exception e) {
            LOG.info("Decrypt Error ");
            throw new RuntimeException(e);
        }
    }

    static class Encrypter extends Des {

        public Encrypter(String passPhrase) throws Exception {
            // Create the key
            KeySpec keySpec = new PBEKeySpec(passPhrase.toCharArray(), salt, iterationCount);
            SecretKey key = SecretKeyFactory.getInstance(FACTORY_KEY).generateSecret(keySpec);
            cipher = Cipher.getInstance(key.getAlgorithm());
            // Prepare the parameter to the ciphers
            AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, iterationCount);
            // Create the ciphers
            cipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
        }

        public String encrypt(String str) throws Exception {
            // Encode the string into bytes using utf-8
            byte[] utf8 = str.getBytes("UTF-8");
            // Encrypt
            byte[] enc = encrypt(utf8);
            return Base32.encode(enc);
        }

        public byte[] encrypt(byte[] utf8) throws IllegalBlockSizeException, BadPaddingException {
            return cipher.doFinal(utf8);
        }
    }

    static class Decrypter extends Des {
        public Decrypter(String passPhrase) throws Exception {
            // Create the key
            KeySpec keySpec = new PBEKeySpec(passPhrase.toCharArray(), salt, iterationCount);
            SecretKey key = SecretKeyFactory.getInstance(FACTORY_KEY).generateSecret(keySpec);
            cipher = Cipher.getInstance(key.getAlgorithm());
            // Prepare the parameter to the ciphers
            AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, iterationCount);
            // Create the ciphers
            cipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
        }

        public String decrypt(String str) throws Exception {
            byte[] dec = Base32.decode(str);
            // Decrypt
            byte[] utf8 = decrypt(dec);
            // Decode using utf-8
            return new String(utf8,"UTF-8");

        }

        public byte[] decrypt(byte[] dec) throws IllegalBlockSizeException, BadPaddingException {
            return cipher.doFinal(dec);
        }
    }
}
