package io.pivotal.cloudfoundry.metrics;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Created by vcarvalho on 4/18/15.
 */
public class Signer {

    private SecretKeySpec keySpec;
    private Mac hmac;

    public Signer(String secret){
        try {
            this.hmac = Mac.getInstance("HmacSHA256");
            this.keySpec = new SecretKeySpec(secret.getBytes("UTF-8"),"HmacSHA256");
            this.hmac.init(keySpec);
        } catch (NoSuchAlgorithmException e) {
           throw new IllegalStateException("Could not create digest instance");
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }
    public byte[] decrypt(byte[] input){
        byte[] decrypted = null;
        try {
            byte[] message = new byte[input.length-32];
            System.arraycopy(input,32,message,0,message.length);
            byte[] expected = hmac.doFinal(message);
            byte[] current = new byte[32];
            System.arraycopy(input,0,current,0,32);
            if(!Arrays.equals(expected,current)){
                throw new RuntimeException("Invalid HMAC signature");
            }
            decrypted = new byte[input.length-32];
            System.arraycopy(input,32,decrypted,0,input.length-32);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return decrypted;
    }

    public byte[] encrypt(byte[] input){
        byte[] encrypted = null;
        try {
            byte[] signature = hmac.doFinal(input);
            encrypted = new byte[input.length+signature.length];
            System.arraycopy(signature,0,encrypted,0,signature.length);
            System.arraycopy(input,0,encrypted,signature.length,input.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encrypted;
    }
}
