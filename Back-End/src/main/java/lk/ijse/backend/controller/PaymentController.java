package lk.ijse.backend.controller;

import lk.ijse.backend.dto.PaymentDTO;
import lk.ijse.backend.dto.PaymentResponse;
import lk.ijse.backend.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/payment")
@CrossOrigin
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @PostMapping("save")
    @PreAuthorize("hasAnyAuthority('SELLER','BUYER')")
    public ResponseEntity<PaymentDTO>savePayment(PaymentDTO paymentDTO){
        paymentService.savePayment(paymentDTO);
        return ResponseEntity.ok(paymentDTO);
    }

    @GetMapping("get")
    @PreAuthorize("hasAnyAuthority('SELLER','BUYER')")
    public ResponseEntity<List<PaymentDTO>>getPayment(){
        List<PaymentDTO> paymentDTO = paymentService.getPayment();
        return ResponseEntity.ok(paymentDTO);
    }

    @PostMapping("create")
    @PreAuthorize("hasAnyAuthority('SELLER','BUYER')")
    public ResponseEntity<PaymentResponse> createPayment(@RequestBody PaymentDTO paymentDTO){
        PaymentResponse payment = paymentService.createPayment(paymentDTO);
        return ResponseEntity.ok(payment);
    }
}
