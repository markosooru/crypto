package com.prax.crypto.exception;


public abstract class CryptoAppException extends RuntimeException {

    public CryptoAppException() {
        super();
    }

    public CryptoAppException(String message) {
        super(message);
    }

    public CryptoAppException(String message, Throwable cause) {
        super(message, cause);
    }

    public CryptoAppException(Throwable cause) {
        super(cause);
    }
}