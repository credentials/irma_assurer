package org.irmacard.identity;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * This class handles all the cryptographic operations needed for secure communication with the server.
 * @author Geert Smelt
 */
public class Crypto {
    RSAPrivateKey skT;
    RSAPublicKey pkS;
    RSAPublicKey pkT;
    SecureRandom sr;

    public Crypto() {
        this.sr = new SecureRandom();
    }

    public byte[] encrypt(byte[] data) {
        byte[] encryptedData = new byte[CONSTANTS.BUFFER_SIZE];
        return encryptedData;
    }

    public void generateSessionKey() {
        byte[] key = new byte[16];
        byte[] iv = new byte[16];

        this.sr.nextBytes(key);
        sr.nextBytes(iv);
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key,"AES"), new IvParameterSpec(iv));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    public RSAPublicKey getPublicKey() {
        return pkT;
    }
}
