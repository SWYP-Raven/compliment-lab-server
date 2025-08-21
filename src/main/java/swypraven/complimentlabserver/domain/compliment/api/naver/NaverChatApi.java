package swypraven.complimentlabserver.domain.compliment.api.naver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import swypraven.complimentlabserver.domain.compliment.api.ChatApi;
import swypraven.complimentlabserver.domain.compliment.model.request.RequestMessage;
import swypraven.complimentlabserver.domain.friend.entity.Chat;
import swypraven.complimentlabserver.domain.friend.entity.Friend;

import java.util.List;


@Slf4j
@RequiredArgsConstructor
public class NaverChatApi implements ChatApi {

    private final WebClient webClient;

    @Value("${naver-clova.process}")
    private String process;

    @Value("${naver-clova.action}")
    private String action;

    @Value("${naver-clova.information}")
    private String information;

    @Value("${naver-clova.characteristic}")
    private String characteristic;

    @Value("${naver-clova.result}")
    private String result;

    @Override
    public String reply(Friend friend, List<Chat> history, RequestMessage message) {
        String id = process;

        String prompt = friend.getType().getDescription();

        RequestNaverClovaChat chat = new RequestNaverClovaChat(prompt, history, message);



        String response = webClient.post()
                .uri("/")
                .header("X-NCP-CLOVASTUDIO-REQUEST-ID", id)
                .bodyValue(chat)
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(resp -> log.info("Clova API Response: {}", resp))
                .block();
        return response;
    }
}
