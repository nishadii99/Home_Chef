package lk.ijse.backend.service.imple;

import jakarta.transaction.Transactional;
import lk.ijse.backend.dto.OrderDTO;
import lk.ijse.backend.entity.Cart;
import lk.ijse.backend.entity.Orders;
import lk.ijse.backend.entity.User;
import lk.ijse.backend.repository.*;
import lk.ijse.backend.service.OrderService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class OrderServiceImple implements OrderService {
    private final CartRepo cartRepo;
    private final OrderRepo orderRepo;
    private final OrderDetailRepo orderDetailsRepo;
    private final ItemRepo itemRepo;
    private final ModelMapper modelMapper;
    private final UserRepo userRepo;

    public OrderServiceImple(CartRepo cartRepository, OrderRepo orderRepository, OrderDetailRepo orderDetailsRepository, ItemRepo itemRepository, ModelMapper modelMapper, CartRepo cartRepo, OrderRepo orderRepo, OrderDetailRepo orderDetailsRepo, ItemRepo itemRepo, ModelMapper modelMapper1, UserRepo userRepo) {

        this.cartRepo = cartRepo;
        this.orderRepo = orderRepo;
        this.orderDetailsRepo = orderDetailsRepo;
        this.itemRepo = itemRepo;
        this.modelMapper = modelMapper1;
        this.userRepo = userRepo;
    }


    @Override
    public void placeOrder(OrderDTO orderDTO) {
        orderRepo.save(modelMapper.map(orderDTO, Orders.class));
    }

    @Override
    public void delete(int orderId) {
        orderRepo.deleteById(orderId);
    }

    @Override
    public List<OrderDTO> getallOrders() {
        return modelMapper.map(orderRepo.findAll(), new TypeToken<List<OrderDTO>>() {}.getType());
    }

    @Override
    public OrderDTO getLastOrder() {
        Orders lastOrder = orderRepo.findFirstByOrderByOrderIdDesc();

        if (lastOrder == null) {
            return null; // No orders available
        }
        return modelMapper.map(lastOrder, OrderDTO.class);
    }

    @Override
    public String updateOrderStatus(int orderId, String status) {
        Orders order = orderRepo.findById(orderId).get();
        System.out.println(status);
        if(!status.equals("PAID")){
            System.out.println("Order status :" + status);
            System.out.println("delete.: " + order);
            orderRepo.delete(order);
            return status;
        }
        order.setStatus(status);
        Orders newOrder = orderRepo.save(order);
        System.out.println(newOrder);
        List<Cart> cart = cartRepo.findByUserEmail(newOrder.getUserId().getEmail());
        for (Cart c : cart) {
            System.out.println("delete.: " + c);
            cartRepo.delete(c);
        }
        return status;
    }
}
