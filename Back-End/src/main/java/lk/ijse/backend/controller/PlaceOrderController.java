package lk.ijse.backend.controller;

import lk.ijse.backend.dto.OrderDTO;
import lk.ijse.backend.dto.ResponseDTO;
import lk.ijse.backend.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/placeOrder")
@CrossOrigin
public class PlaceOrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping(path = "save")
    @PreAuthorize("hasAnyAuthority('SELLER','BUYER')")
    public ResponseEntity<ResponseDTO> placeOrder(@RequestBody OrderDTO orderDTO) {
        orderService.placeOrder(orderDTO);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseDTO(HttpStatus.OK.value(), "Order Added SuccessFully!", orderDTO));
    }

//    @GetMapping(path = "get")
//    @PreAuthorize("hasAnyAuthority('SELLER','BUYER')")
//    public ResponseEntity<List<OrderDTO>> getOrders(){
//        List<OrderDTO> orders = orderService.getAllOrders();
//        return ResponseEntity.ok(orders);
//    }

}
