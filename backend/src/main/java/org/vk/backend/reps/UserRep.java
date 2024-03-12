package org.vk.backend.reps;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.vk.backend.entity.user.User;

import java.util.Optional;

@Repository
public interface UserRep extends JpaRepository<User, Long> {
    Optional<User> findUserByUsername(String username);
    Optional<User> findUserById(Long id);

}
