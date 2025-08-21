package swypraven.complimentlabserver.domain.compliment.model.api;

import org.springframework.stereotype.Service;
import swypraven.complimentlabserver.domain.compliment.model.request.RequestMessage;
import swypraven.complimentlabserver.domain.friend.entity.Friend;

import java.util.List;

@Service
public interface ChatApi {
     String reply(Friend friend, RequestMessage message);
}
