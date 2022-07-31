package com.illtamer.infinite.bot.api.handler;

public abstract class AbstractAPIHandler implements APIHandler {

    private transient final String endpoint;

    public AbstractAPIHandler(String endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public String getEndpoint() {
        return endpoint;
    }

}
