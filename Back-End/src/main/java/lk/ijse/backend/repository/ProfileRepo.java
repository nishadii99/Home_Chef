package lk.ijse.backend.repository;

import lk.ijse.backend.entity.Profile;
import lk.ijse.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepo extends JpaRepository<Profile,Integer> {
    Profile findByUserEmail(String email);
    Profile findByUser(User user);
}
