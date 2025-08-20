package swypraven.complimentlabserver.domain.compliment.model.api.naver;

import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;
import swypraven.complimentlabserver.domain.compliment.model.api.ChatApi;
import swypraven.complimentlabserver.domain.compliment.model.request.RequestMessage;
import swypraven.complimentlabserver.domain.friend.entity.Friend;


@RequiredArgsConstructor
public class NaverChatApi implements ChatApi {

    private final WebClient webClient;

    @Override
    public String sendMessage(Friend friend, RequestMessage message) {
        RequestNaverClovaChat request = new RequestNaverClovaChat();
        webClient.post().body(request, RequestNaverClovaChat.class);
        return "";
    }
}
