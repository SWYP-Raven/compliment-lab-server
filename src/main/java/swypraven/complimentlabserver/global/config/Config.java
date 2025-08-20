package swypraven.complimentlabserver.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import swypraven.complimentlabserver.domain.compliment.model.api.ChatApi;
import swypraven.complimentlabserver.domain.compliment.model.api.naver.NaverChatApi;

@Configuration
public class Config {

    @Bean
    public ChatApi chatApi(WebClient webClient) {
        return new NaverChatApi(webClient);
    }

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder
                .baseUrl("https://clovastudio.stream.ntruss.com/v1/chat-completions/HCX-003")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("X-NCP-CLOVASTUDIO-REQUEST-ID", "3c1a46d1f14d4e04a99c90ad31639479")
                .defaultHeader("Authorization", "Bearer nv-6e2ca13e7ece4feea56edadaf7664c44O8Ud")
                .build();
    }
}
