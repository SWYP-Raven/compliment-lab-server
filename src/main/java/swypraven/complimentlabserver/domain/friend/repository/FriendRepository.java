package swypraven.complimentlabserver.domain.friend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import swypraven.complimentlabserver.domain.friend.entity.Friend;

public interface FriendRepository extends JpaRepository<Friend, Long> {
}
