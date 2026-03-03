package org.example.features.quote.exception;

/**
 * Custom exception for Quote operations
 */
public class QuoteException extends RuntimeException {

    public QuoteException(String message) {
        super(message);
    }

    public QuoteException(String message, Throwable cause) {
        super(message, cause);
    }

    public static class QuoteNotFoundException extends QuoteException {
        public QuoteNotFoundException(Long orderId) {
            super("Quote not found for order: " + orderId);
        }
    }

    public static class QuoteAlreadyExistsException extends QuoteException {
        public QuoteAlreadyExistsException(Long orderId) {
            super("Quote already exists for order: " + orderId);
        }
    }

    public static class QuoteExpiredException extends QuoteException {
        public QuoteExpiredException() {
            super("Quote has expired");
        }
    }

    public static class InvalidQuoteStatusException extends QuoteException {
        public InvalidQuoteStatusException(String message) {
            super(message);
        }
    }
}
