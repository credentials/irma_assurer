package org.irmacard.identity;

/**
 * Exception that is intended for failure when verifying integrity of an ID document, such as a passport.
 */
public class IDVerificationException extends Throwable {
    public IDVerificationException(String s) {
        System.out.println(s);
    }
}
