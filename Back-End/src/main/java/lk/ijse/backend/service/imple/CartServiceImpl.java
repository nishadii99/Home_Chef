package lk.ijse.backend.service.imple;

import lk.ijse.backend.dto.CartDTO;
import lk.ijse.backend.entity.Cart;
import lk.ijse.backend.repository.CartRepo;
import lk.ijse.backend.service.CartService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    private CartRepo cartRepo;

    @Override
    public void addtoCart(CartDTO cartDTO) {
        if (cartDTO == null) {
            throw new IllegalArgumentException("CartDTO cannot be null");
        }

        try {
            Cart cart = modelMapper.map(cartDTO, Cart.class);
            cartRepo.save(cart);
        } catch (Exception e) {
            e.printStackTrace(); // this will show you the actual error in console
            throw new RuntimeException("Failed to add item to cart", e);
        }

    }

    @Override
    public void removeFromCart(int id) {
        cartRepo.deleteById(id);
    }

    @Override
    public List<Cart> getCartItemsByUser(String email) {
        List<Cart> cartItems = cartRepo.findByUserEmail(email);

        if (cartItems == null || cartItems.isEmpty()) {
            return Collections.emptyList();
        }
        for (Cart cart : cartItems) {
            System.out.println("Cart Item: " + cart); // Debug log
        }
        return cartItems;
    }

    @Override
    public Cart updateCartQuantity(int id, int quantity) {
        Cart cart = cartRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        cart.setQuantity(quantity);
        return cartRepo.save(cart);
    }
}
