package de.marcschuler.onyxserver.error.webclient;

public class NotFoundException extends Throwable{
    public NotFoundException(String message) {
        super(message);
    }
}
