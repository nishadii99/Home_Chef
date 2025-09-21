package lk.ijse.backend.repository;

import lk.ijse.backend.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepo extends JpaRepository<Orders, Integer> {
    @Query("SELECT o FROM Orders o ORDER BY o.orderId DESC LIMIT 1")
    Orders findFirstByOrderByOrderIdDesc();


}
