package com.illtamer.infinite.bot.api.exception;

public class TypeParseException extends RuntimeException {

    public TypeParseException() {
    }

    public TypeParseException(String message) {
        super(message);
    }

    public TypeParseException(String message, Throwable cause) {
        super(message, cause);
    }

}
