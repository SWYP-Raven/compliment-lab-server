package swypraven.complimentlabserver.domain.friend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import swypraven.complimentlabserver.domain.compliment.entity.TypeCompliment;
import swypraven.complimentlabserver.domain.friend.entity.Friend;
import swypraven.complimentlabserver.domain.user.entity.User;

import java.util.List;
import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Long> {

    Boolean existsByUserAndType(User user, TypeCompliment type);

    List<Friend> findAllByUser(User user);

    Optional<Friend> findByIdAndUserId(Long friendId, Long userId);
}