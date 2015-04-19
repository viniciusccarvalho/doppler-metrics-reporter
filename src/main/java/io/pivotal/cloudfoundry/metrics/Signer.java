package io.pivotal.cloudfoundry.metrics;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Created by vcarvalho on 4/18/15.
 */
public class Signer {

    private final SecretKeySpec keySpec;

    public Signer(String secret){
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(secret.getBytes());
            this.keySpec = new SecretKeySpec(Arrays.copyOf(hash,16),"AES");
        } catch (NoSuchAlgorithmException e) {
           throw new IllegalStateException("Could not create digest instance");
        }

    }
    public byte[] decrypt(byte[] input){
        byte[] decrypted = null;
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE,keySpec);
            decrypted = cipher.doFinal(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return decrypted;
    }

    public byte[] encrypt(byte[] input){
        byte[] encrypted = null;
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE,keySpec);
            encrypted = cipher.doFinal(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encrypted;
    }
}
