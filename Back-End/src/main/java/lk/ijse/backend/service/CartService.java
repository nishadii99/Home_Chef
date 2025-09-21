package lk.ijse.backend.service;

import lk.ijse.backend.dto.CartDTO;
import lk.ijse.backend.entity.Cart;

import java.util.List;

public interface CartService {
    void addtoCart(CartDTO cartDTO);
    void removeFromCart(int id);
    List<Cart> getCartItemsByUser(String userEmail);
    Cart updateCartQuantity(int id, int quantity);
}
