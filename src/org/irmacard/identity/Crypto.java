package org.irmacard.identity;

import org.bouncycastle.jcajce.provider.digest.SHA1;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.DHGenParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
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
    SecretKey session_key;
    final byte[] serverPublicKey = {};

    public Crypto() {
        this.sr = new SecureRandom();
    }

    public byte[] encrypt(byte[] data) {
        byte[] encryptedData = new byte[CONSTANTS.BUFFER_SIZE];
        return encryptedData;
    }

    public void generateSessionKey() {
        System.out.println("Starting AES session key generation.");
        System.out.printf("List of available security providers: %s\n", (Object[]) Security.getProviders());

        try {
            KeyGenerator keygen = KeyGenerator.getInstance("AES");
            keygen.init(256, sr);
            session_key = keygen.generateKey();
            System.out.printf("Session key generated successfully: %s\n", session_key.toString());
        } catch (NoSuchAlgorithmException e) {
            System.out.println("The specified algorithm does not exist.");
            e.printStackTrace();
        } catch (InvalidParameterException e) {
            System.out.println("You have specified an incorrect parameter.");
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public RSAPublicKey getPublicKey() {
        return pkT;
    }

    protected void reset() {
        session_key = null;
    }
}
