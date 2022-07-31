package com.illtamer.infinite.bot.api.exception;

public class ExclusiveMessageException extends RuntimeException {

    public ExclusiveMessageException(String type) {
        super("This message can only be created by the single type: " + type);
    }

}
