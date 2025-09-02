package swypraven.complimentlabserver.domain.friend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swypraven.complimentlabserver.domain.compliment.entity.TypeCompliment;
import swypraven.complimentlabserver.domain.friend.entity.UserFriendType;
import swypraven.complimentlabserver.domain.user.entity.User;

@Repository
public interface UserFriendTypeRepository extends JpaRepository<UserFriendType, Long> {
    boolean existsByUserAndTypeCompliment(User user, TypeCompliment typeCompliment);
}
