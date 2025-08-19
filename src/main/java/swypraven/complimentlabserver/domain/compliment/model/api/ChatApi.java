package swypraven.complimentlabserver.domain.compliment.model.api;

import org.springframework.stereotype.Service;

@Service
public interface ChatApi {
    String sendMessage(String message);
}
