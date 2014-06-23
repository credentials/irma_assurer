package org.irmacard.identity.assurer;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.irmacard.identity.common.CONSTANTS;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
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
        return new byte[CONSTANTS.BUFFER_SIZE];
    }

    public void generateSessionKey() {
        try {
            System.out.println("Starting AES session key generation.");

            Security.addProvider(new BouncyCastleProvider());
            KeyGenerator keygen = KeyGenerator.getInstance("AES", "BC");
            keygen.init(256, sr);
            session_key = keygen.generateKey();

            System.out.println("Session key generated successfully.");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("The specified algorithm does not exist.");
            e.printStackTrace();
        } catch (InvalidParameterException e) {
            System.out.println("You have specified an incorrect parameter.");
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            System.out.println("The Bouncy Castle security provider is not installed. Aborting.");
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
