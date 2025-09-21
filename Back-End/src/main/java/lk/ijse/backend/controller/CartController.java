package lk.ijse.backend.controller;

import jakarta.validation.Valid;
import lk.ijse.backend.dto.CartDTO;
import lk.ijse.backend.dto.ResponseDTO;
import lk.ijse.backend.dto.UserDTO;
import lk.ijse.backend.entity.Cart;
import lk.ijse.backend.service.CartService;
import lk.ijse.backend.service.UserService;
import lk.ijse.backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cart")
@CrossOrigin
public class CartController {
    @Autowired
    private final CartService cartService;
    @Autowired
    private final UserService userService;
    @Autowired
    private final JwtUtil jwtUtil;

    public CartController(CartService cartService, UserService userService, JwtUtil jwtUtil) {
        this.cartService = cartService;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/save")
    @PreAuthorize("hasAnyAuthority('BUYER')") // Typically only buyers can add to cart
    public ResponseEntity<ResponseDTO> saveCart(@RequestBody CartDTO cartDTO,
                                                @RequestHeader("Authorization") String token) {
        String username = jwtUtil.getUsernameFromToken(token.substring(7));

        // Either:
        // 1. Set email directly if username is email
        cartDTO.setUserEmail(username);

        // Or 2. Fetch full user details
        UserDTO userDTO = userService.getUserByEmail(username);
        cartDTO.setUserEmail(userDTO.getEmail());

        cartService.addtoCart(cartDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseDTO(HttpStatus.CREATED.value(), "Success", cartDTO));


    }

    @GetMapping(path = "/get")
    @PreAuthorize("hasAnyAuthority('SELLER','BUYER')")
    public ResponseEntity<ResponseDTO> getCart(@RequestHeader("Authorization") String token) {
        try {
            String email = jwtUtil.getUsernameFromToken(token.substring(7));
            System.out.println("Fetching cart for email: " + email); // Debug log

            List<Cart> cartItems = cartService.getCartItemsByUser(email);
            System.out.println("Found " + cartItems.size() + " cart items"); // Debug log

            return ResponseEntity.ok()
                    .body(new ResponseDTO(HttpStatus.OK.value(), "Success", cartItems));
        } catch (Exception e) {
            System.err.println("Error fetching cart: " + e.getMessage()); // Debug log
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null));
        }
    }

    @DeleteMapping(path = "/delete/{id}")
    @PreAuthorize("hasAnyAuthority('SELLER','BUYER')")
    public ResponseEntity<ResponseDTO> deleteCart(@PathVariable String id,
                                                  @RequestHeader("Authorization") String token) {
        cartService.removeFromCart(Integer.parseInt(id));
        return ResponseEntity.ok()
                .body(new ResponseDTO(HttpStatus.OK.value(), "Cart deleted successfully", null));
    }

    @PutMapping(path = "/update/{id}")
    @PreAuthorize("hasAnyAuthority('SELLER','BUYER')")
    public ResponseEntity<ResponseDTO> updateCart(@PathVariable int id,
                                                  @Valid @RequestBody CartDTO cartDTO,
                                                  @RequestHeader("Authorization") String token) {
        cartService.updateCartQuantity(id, cartDTO.getQuantity());
        return ResponseEntity.ok().body(new ResponseDTO(HttpStatus.OK.value(), "Cart updated successfully", cartDTO));
    }


}

