package lk.ijse.backend.repository;

import lk.ijse.backend.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartRepo extends JpaRepository<Cart, Integer> {
    List<Cart> findByUserEmail(String userEmail);
    void deleteByUserEmail(String userEmail);

}
