package com.skax.aiplatform.repository.home;

import com.skax.aiplatform.entity.user.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GpoUsersRepository extends JpaRepository<User, String> {

    Optional<User> findByJkwNm(String username);

    Optional<User> findByMemberId(String memberId);

    Optional<User> findByUuid(String uuid);

    void deleteByMemberId(String memberId);
}
