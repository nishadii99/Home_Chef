package lk.ijse.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemDTO {
    private int itemCode;
    private String itemName;
    private String description;
    private int quantity;
    private double price;
    private String location;
    private String sourceImage;
    private int categoryId;
    private String userEmail;

}
