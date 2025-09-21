package lk.ijse.backend.service;


import lk.ijse.backend.dto.ItemDTO;
import lk.ijse.backend.dto.ItemDataDTO;
import lk.ijse.backend.model.ItemModel;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ItemService {
    ItemDTO saveItem(ItemDataDTO itemDataDTO, String sellerEmail);
    List<ItemDTO> getItemsBySeller(String sellerEmail);
    List<ItemDTO> getItemsByCategoryAndSeller(String categoryId, String sellerEmail);

    List<ItemModel> getAllItems();

    ItemDTO updateItem(ItemDataDTO itemDataDTO, String email);

    void deleteItem(int id, String username);

    List<ItemDTO> getItembyItemCode(int itemCode, String sellerEmail);

    ItemDTO getItemByCode(int itemCode);
}
