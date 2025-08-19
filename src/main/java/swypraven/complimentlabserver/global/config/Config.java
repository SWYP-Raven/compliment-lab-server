package swypraven.complimentlabserver.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import swypraven.complimentlabserver.domain.compliment.model.api.ChatApi;
import swypraven.complimentlabserver.domain.compliment.model.api.naver.NaverChatApi;

@Configuration
public class Config {

    @Bean
    public ChatApi chatApi() {
        return new NaverChatApi();
    }
}
