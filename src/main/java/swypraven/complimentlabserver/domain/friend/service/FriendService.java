package swypraven.complimentlabserver.domain.friend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swypraven.complimentlabserver.domain.compliment.entity.TypeCompliment;
import swypraven.complimentlabserver.domain.compliment.model.dto.ChatResponse;
import swypraven.complimentlabserver.domain.compliment.repository.TypeComplimentRepository;
import swypraven.complimentlabserver.domain.compliment.service.ChatService;
import swypraven.complimentlabserver.domain.compliment.service.ComplimentTypeService;
import swypraven.complimentlabserver.domain.friend.entity.Friend;
import swypraven.complimentlabserver.domain.friend.entity.UserFriendType;
import swypraven.complimentlabserver.domain.friend.model.request.RequestCreateFriend;
import swypraven.complimentlabserver.domain.friend.model.request.RequestUpdateFriend;
import swypraven.complimentlabserver.domain.friend.model.response.ResponseFriend;
import swypraven.complimentlabserver.domain.friend.repository.FriendRepository;
import swypraven.complimentlabserver.domain.friend.repository.UserFriendTypeRepository;
import swypraven.complimentlabserver.domain.user.entity.User;
import swypraven.complimentlabserver.domain.user.repository.UserRepository;
import swypraven.complimentlabserver.global.auth.security.CustomUserDetails;
import swypraven.complimentlabserver.global.exception.friend.FriendErrorCode;
import swypraven.complimentlabserver.global.exception.friend.FriendException;
import swypraven.complimentlabserver.global.exception.user.UserErrorCode;
import swypraven.complimentlabserver.global.exception.user.UserException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FriendService {
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;
    private final UserFriendTypeRepository  userFriendTypeRepository;

    private final ComplimentTypeService complimentTypeService;
    private final ChatService chatService;

    @Transactional
    public ResponseFriend create(CustomUserDetails userDetails, RequestCreateFriend request) {

        User user = userRepository.findById(userDetails.getId()).orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        TypeCompliment type = complimentTypeService.getType(request.getFriendType());

        if(friendRepository.existsByUserAndType(user, type)) {
            throw new FriendException(FriendErrorCode.EXIST_FRIEND);
        }

        Friend friend = Friend.builder().name(request.getName()).user(user).type(type).build();
        Friend savedFriend = friendRepository.save(friend);

        boolean isFirst = !userFriendTypeRepository.existsByUserAndTypeCompliment(user, savedFriend.getType());

        return new ResponseFriend(savedFriend, isFirst);
    }


    @Transactional(readOnly = true)
    public List<ResponseFriend> getFriends(CustomUserDetails userDetails) {
        User user = userRepository.findById(userDetails.getId()).orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
        List<Friend> friends = friendRepository.findAllByUser(user);

        return friends.stream().map(friend -> {
            ChatResponse lastChat = chatService.findLastChats(friend);
            return new ResponseFriend(friend, lastChat.getMessage());
        }).toList();
    }


    @Transactional
    public ResponseFriend updateFriend(Long friendId, RequestUpdateFriend request) {
        Friend friend = friendRepository.findById(friendId)
                .orElseThrow(() -> new FriendException(FriendErrorCode.NOT_FOUND_FRIEND));
        friend.changeName(request.getName());

        return new ResponseFriend(friend);
    }

    @Transactional
    public void delete(Long friendId) {
        Friend friend = friendRepository.findById(friendId).orElseThrow(() -> new FriendException(FriendErrorCode.NOT_FOUND_FRIEND));
        userFriendTypeRepository.save(new UserFriendType(friend.getUser(), friend.getType()));
        friendRepository.deleteById(friendId);
    }

    @Transactional(readOnly = true)
    public Friend getFriend(Long friendId) {
        return friendRepository.findById(friendId).orElseThrow(() -> new FriendException(FriendErrorCode.NOT_FOUND_FRIEND));
    }

}
