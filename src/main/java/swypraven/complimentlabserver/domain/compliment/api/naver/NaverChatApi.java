package swypraven.complimentlabserver.domain.compliment.api.naver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import swypraven.complimentlabserver.domain.compliment.api.ChatApi;
import swypraven.complimentlabserver.domain.compliment.model.dto.naver.request.RequestNaverClovaChat;
import swypraven.complimentlabserver.domain.compliment.model.dto.naver.response.ResponseNavarClovaChat;
import swypraven.complimentlabserver.domain.compliment.model.request.RequestMessage;
import swypraven.complimentlabserver.domain.friend.entity.Chat;
import swypraven.complimentlabserver.domain.friend.entity.Friend;

import java.util.List;


@Slf4j
@RequiredArgsConstructor
public class NaverChatApi implements ChatApi {

    private final WebClient webClient;

    @Override
    public ResponseNavarClovaChat reply(Friend friend, List<Chat> history, RequestMessage message) {
        String prompt = friend.getType().getDescription();
        RequestNaverClovaChat chat = new RequestNaverClovaChat(prompt, history, message);


        return webClient.post()
                .uri("/HCX-005")
                .bodyValue(chat)
                .retrieve()
                .bodyToMono(ResponseNavarClovaChat.class)
                .block();
    }
}
