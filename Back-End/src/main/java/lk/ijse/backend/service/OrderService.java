package lk.ijse.backend.service;

import lk.ijse.backend.dto.OrderDTO;

import java.util.List;


public interface OrderService {
    void placeOrder(OrderDTO orderDTO);
    void delete(int orderId);
    List<OrderDTO> getallOrders();
    OrderDTO getLastOrder();

    String updateOrderStatus(int orderId, String status);
}
