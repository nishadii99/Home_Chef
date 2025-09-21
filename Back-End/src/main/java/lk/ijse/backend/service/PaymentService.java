package lk.ijse.backend.service;

import lk.ijse.backend.dto.PaymentDTO;
import lk.ijse.backend.dto.PaymentResponse;

import java.util.List;

public interface PaymentService {
    void savePayment(PaymentDTO paymentDTO);
    List<PaymentDTO> getPayment();

    PaymentResponse createPayment(PaymentDTO paymentDTO);
}
