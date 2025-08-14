package swypraven.complimentlabserver.domain.friend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import swypraven.complimentlabserver.domain.compliment.entity.TypeCompliment;
import swypraven.complimentlabserver.domain.compliment.service.ComplimentTypeService;
import swypraven.complimentlabserver.domain.friend.entity.Friend;
import swypraven.complimentlabserver.domain.friend.model.request.RequestCreateFriend;
import swypraven.complimentlabserver.domain.friend.model.response.ResponseCreateFriend;
import swypraven.complimentlabserver.domain.friend.repository.FriendRepository;
import swypraven.complimentlabserver.domain.user.entity.User;
import swypraven.complimentlabserver.domain.user.repository.UserRepository;
import swypraven.complimentlabserver.global.exception.user.UserErrorCode;
import swypraven.complimentlabserver.global.exception.user.UserException;

@Slf4j
@Service
@RequiredArgsConstructor
public class FriendService {
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;
    private final ComplimentTypeService complimentTypeService;



    public ResponseCreateFriend create(RequestCreateFriend request) {
        // TODO: 유저 로직
        User user = userRepository.findById(3L).orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        TypeCompliment type = complimentTypeService.getType(request.getFriendType());
        // TODO: 같은 타입의 친구 생성 불가

        Friend friend = Friend.builder().name(request.getName()).user(user).type(type).build();
        Friend savedFriend = friendRepository.save(friend);

        return new ResponseCreateFriend(savedFriend);
    }

}
