package lk.ijse.backend.service.imple;

import lk.ijse.backend.dto.PaymentDTO;
import lk.ijse.backend.dto.PaymentResponse;
import lk.ijse.backend.entity.Cart;
import lk.ijse.backend.entity.Orders;
import lk.ijse.backend.entity.User;
import lk.ijse.backend.repository.CartRepo;
import lk.ijse.backend.repository.OrderDetailRepo;
import lk.ijse.backend.repository.OrderRepo;
import lk.ijse.backend.repository.UserRepo;
import lk.ijse.backend.service.PaymentService;
import lk.ijse.backend.util.Payhere;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final Payhere payhere;
    private final OrderRepo orderRepo;
    private final CartRepo cartRepo;
    private final UserRepo userRepo;

    @Override
    public void savePayment(PaymentDTO paymentDTO) {

    }

    @Override
    public List<PaymentDTO> getPayment() {
        return null;
    }

    @Override
    public PaymentResponse createPayment(PaymentDTO paymentDTO) {
        List<Cart> cartItems = cartRepo.findByUserEmail(paymentDTO.getEmail());
        User user = userRepo.findByEmail(paymentDTO.getEmail());
        Orders order = Orders.builder()
                .userId(user)
                .orderDate(String.valueOf(LocalDate.now()))
                .totalAmount(paymentDTO.getAmount())
                .status("PENDING")
                .build();
        Orders order1 = orderRepo.save(order);
        String signature = payhere.generateSignature(
                String.valueOf(order1.getOrderId()),
                paymentDTO.getAmount(),
                paymentDTO.getCurrency()
        );
        System.out.println(signature);
        return new PaymentResponse(order1.getOrderId(),signature);
    }
}
