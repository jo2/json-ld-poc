package io.blogtrack.poc;

public class ClientException extends RuntimeException {
    public ClientException(String message) {
        super(message);
    }
}
