package swypraven.complimentlabserver.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import swypraven.complimentlabserver.domain.compliment.api.ChatApi;
import swypraven.complimentlabserver.domain.compliment.api.naver.NaverChatApi;

@Configuration
public class Config {

    @Value("${naver-clova.token}")
    private String token;

    @Value("${naver-clova.id}")
    private String id;

    @Bean
    public ChatApi chatApi(WebClient webClient) {
        return new NaverChatApi(webClient);
    }

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder
                .baseUrl("https://clovastudio.stream.ntruss.com/v3/chat-completions/HCX-005")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("X-NCP-CLOVASTUDIO-REQUEST-ID", id)
                .defaultHeader("Authorization", "Bearer " + token)
                .build();
    }
}
