package swypraven.complimentlabserver.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import swypraven.complimentlabserver.domain.compliment.model.api.ChatApi;
import swypraven.complimentlabserver.domain.compliment.model.api.naver.NaverChatApi;

@Configuration
public class Config {

    @Value("${naver-clova.token}")
    private String token;

    @Bean
    public ChatApi chatApi(WebClient webClient) {
        return new NaverChatApi(webClient);
    }

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        System.out.println(token);
        return builder
                .baseUrl("https://clovastudio.stream.ntruss.com/v1/chat-completions/HCX-005")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("Authorization", "Bearer " + token)
                .build();
    }
}
