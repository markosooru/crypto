package com.prax.crypto.exception;

public class NotAuthenticatedException extends CryptoAppException {
    public NotAuthenticatedException() {
        super();
    }

    public NotAuthenticatedException(String message) {
        super(message);
    }

    public NotAuthenticatedException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotAuthenticatedException(Throwable cause) {
        super(cause);
    }
}
