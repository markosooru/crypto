package com.prax.crypto.exception;

public class PortfolioNotFoundException extends CryptoAppException {
    public PortfolioNotFoundException() {
        super();
    }

    public PortfolioNotFoundException(String message) {
        super(message);
    }

    public PortfolioNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public PortfolioNotFoundException(Throwable cause) {
        super(cause);
    }
}
