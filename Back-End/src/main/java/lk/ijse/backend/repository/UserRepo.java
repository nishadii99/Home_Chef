package lk.ijse.backend.repository;


import lk.ijse.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserRepo extends JpaRepository<User, String> {
    User findByEmail(String email);
    User findByEmailAndRole(String email, String role);
    boolean existsByEmail(String email);
    User getUserByEmail(String email);

    int deleteByEmail(String userName);

    Optional<Object> findByUsername(String username);

}
