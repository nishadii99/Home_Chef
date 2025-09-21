package lk.ijse.backend.dto;

import lk.ijse.backend.entity.Categories;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemDataDTO {
    private int itemCode;
    private String itemName;
    private String description;
    private int quantity;
    private double price;
    private String location;
    private MultipartFile sourceImage;
    private int categoryId;
    private String userEmail;
}
