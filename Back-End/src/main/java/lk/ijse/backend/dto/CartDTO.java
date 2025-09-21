package lk.ijse.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartDTO {
    private String userEmail;
    private int itemCode;
    private int quantity;
    private String image;
    private String name;
    private double price;

}
