package swypraven.complimentlabserver.domain.compliment.api.naver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import swypraven.complimentlabserver.domain.compliment.api.ChatApi;
import swypraven.complimentlabserver.domain.compliment.model.dto.naver.RequestNaverClovaChat;
import swypraven.complimentlabserver.domain.compliment.model.request.RequestMessage;
import swypraven.complimentlabserver.domain.friend.entity.Chat;
import swypraven.complimentlabserver.domain.friend.entity.Friend;

import java.util.List;


@Slf4j
@RequiredArgsConstructor
public class NaverChatApi implements ChatApi {

    private final WebClient webClient;

    @Override
    public String reply(Friend friend, List<Chat> history, RequestMessage message) {
        String prompt = friend.getType().getDescription();
        RequestNaverClovaChat chat = new RequestNaverClovaChat(prompt, history, message);

        ObjectMapper mapper = new ObjectMapper();
        try {
            log.info("Request JSON: {}", mapper.writeValueAsString(chat));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }


        String response = webClient.post()
                .uri("/")
                .bodyValue(chat)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(e -> {
                    log.info("ERROR: " + e.getLocalizedMessage());
                    return Mono.error(new Exception(e));
                })
                .doOnNext(resp -> log.info("Clova API Response: {}", resp))
                .block();
        return response;
    }
}
