package com.illtamer.infinite.bot.starter;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "bot")
public class BotProperties {

    @NotNull
    private String httpUri = "http://localhost:5700";

    @NotNull
    private String wsUri = "http://localhost:8080";

    @NotNull
    private String authorization = "";

    @NotNull
    public String getHttpUri() {
        return httpUri;
    }

    public void setHttpUri(@NotNull String httpUri) {
        this.httpUri = httpUri;
    }

    @NotNull
    public String getWsUri() {
        return wsUri;
    }

    public void setWsUri(@NotNull String wsUri) {
        this.wsUri = wsUri;
    }

    @NotNull
    public String getAuthorization() {
        return authorization;
    }

    public void setAuthorization(@NotNull String authorization) {
        this.authorization = authorization;
    }

}
