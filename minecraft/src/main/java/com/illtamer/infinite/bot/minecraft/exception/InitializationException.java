package com.illtamer.infinite.bot.minecraft.exception;

public class InitializationException extends RuntimeException {

    public InitializationException() {
    }

    public InitializationException(String message) {
        super(message);
    }

    public InitializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public InitializationException(Throwable cause) {
        super(cause);
    }

}
