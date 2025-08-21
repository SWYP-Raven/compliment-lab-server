package swypraven.complimentlabserver.domain.compliment.model.api.naver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;
import swypraven.complimentlabserver.domain.compliment.model.api.ChatApi;
import swypraven.complimentlabserver.domain.compliment.model.request.RequestMessage;
import swypraven.complimentlabserver.domain.friend.entity.Friend;


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
    public String reply(Friend friend, RequestMessage message) {
        String id = process;

        String prompt = friend.getType().getDescription();
        String role = "system";

        RequestNaverClovaChat chat = new RequestNaverClovaChat(role, prompt, message);

        log.info("NaverChatApi reply: {}", chat);


        String response = webClient.post().uri("/")
                .bodyValue(chat)
                .header("X-NCP-CLOVASTUDIO-REQUEST-ID", id)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        return response;
    }
}
