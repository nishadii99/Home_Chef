package lk.ijse.backend.service.imple;


import jakarta.servlet.annotation.MultipartConfig;
import lk.ijse.backend.dto.ItemDTO;
import lk.ijse.backend.dto.ItemDataDTO;
import lk.ijse.backend.entity.Categories;
import lk.ijse.backend.entity.Item;
import lk.ijse.backend.entity.User;
import lk.ijse.backend.model.ItemModel;
import lk.ijse.backend.repository.CategoriesRepo;
import lk.ijse.backend.repository.ItemRepo;
import lk.ijse.backend.repository.UserRepo;
import lk.ijse.backend.service.ItemService;
import lk.ijse.backend.util.ImageUploader;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2, // 2MB
        maxFileSize = 1024 * 1024 * 10, // 10MB
        maxRequestSize = 1024 * 1024 * 50 // 50MB
)
public class ItemServiceImple implements ItemService {
    @Autowired
    private UserRepo userRepository;
    @Autowired
    private ItemRepo itemRepository;
    @Autowired
    private CategoriesRepo categoryRepo;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private UserServiceImpl userService;

    @Override
    public ItemDTO saveItem(ItemDataDTO itemDataDTO, String sellerEmail) {
        User seller = userRepository.findByEmailAndRole(sellerEmail, "SELLER");
        if (seller == null) {
            throw new RuntimeException("Seller not found or invalid role");
        }
//
//        if (itemDataDTO.getSourceImage() == null) {
//            userService.saveItemImage(itemDataDTO.getSourceImage());
//        }

        Categories category = categoryRepo.findById(itemDataDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

//        Item item = modelMapper.map(itemDataDTO, Item.class);
//        item.setItemCode(itemDataDTO.getItemCode());
//        item.setItemName(itemDataDTO.getItemName());
//        item.setDescription(itemDataDTO.getDescription());
//        item.setQuantity(itemDataDTO.getQuantity());
//        item.setPrice(itemDataDTO.getPrice());
//        item.setSourceImage(userService.saveItemImage(itemDataDTO.getSourceImage()));
//        item.setLocation(itemDataDTO.getLocation());
//        item.setUser(seller);
//        item.setCategory(category);
        String image = ImageUploader.uploadImage(itemDataDTO.getSourceImage());
        Item item = Item.builder()
                        .itemCode(itemDataDTO.getItemCode())
                        .itemName(itemDataDTO.getItemName())
                        .description(itemDataDTO.getDescription())
                        .quantity(itemDataDTO.getQuantity())
                        .price(itemDataDTO.getPrice())
                        .sourceImage(image)
                        .location(itemDataDTO.getLocation())
                        .user(seller)
                        .category(category)
                        .build();

        itemRepository.save(item);

        ItemDTO itemDTO = new ItemDTO();
        itemDTO.setItemCode(item.getItemCode());
        itemDTO.setItemName(item.getItemName());
        itemDTO.setDescription(item.getDescription());
        itemDTO.setQuantity(item.getQuantity());
        itemDTO.setPrice(item.getPrice());
        itemDTO.setLocation(item.getLocation());
        itemDTO.setSourceImage(item.getSourceImage());
        itemDTO.setCategoryId(item.getCategory().getCategoryId());
        itemDTO.setUserEmail(item.getUser().getEmail());

        return itemDTO;
    }
    @Override
    public List<ItemDTO> getItemsBySeller(String sellerEmail) {
        return itemRepository.findByUserEmail(sellerEmail).stream()
                .map(item -> modelMapper.map(item, ItemDTO.class))
                .collect(Collectors.toList());
    }
    @Override
    public List<ItemDTO> getItemsByCategoryAndSeller(String categoryId, String sellerEmail) {
        return itemRepository.findByCategoryAndSeller(categoryId, sellerEmail).stream()
                .map(item -> modelMapper.map(item, ItemDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemModel> getAllItems() {
        return itemRepository.findAll().stream()
                .map(item -> new ItemModel(
                        item.getItemCode(),
                        item.getItemName(),
                        item.getCategory().getCategoryId(),
                        item.getDescription(),
                        item.getPrice(),
                        item.getSourceImage(),
                        item.getLocation(),
                        item.getQuantity(),
                        item.getUser().getEmail()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public ItemDTO updateItem(ItemDataDTO itemDataDTO, String email) {
        Item item = itemRepository.findByItemCode(itemDataDTO.getItemCode());
        if (item == null) {
            throw new RuntimeException("Item not found");
        }

        item.setItemName(itemDataDTO.getItemName());
        item.setDescription(itemDataDTO.getDescription());
        item.setQuantity(itemDataDTO.getQuantity());
        item.setPrice(itemDataDTO.getPrice());
        item.setLocation(itemDataDTO.getLocation());
        item.setSourceImage(userService.saveItemImage(itemDataDTO.getSourceImage()));

        itemRepository.save(item);

        ItemDTO itemDTO = new ItemDTO();
        itemDTO.setItemCode(item.getItemCode());
        itemDTO.setItemName(item.getItemName());
        itemDTO.setDescription(item.getDescription());
        itemDTO.setQuantity(item.getQuantity());
        itemDTO.setPrice(item.getPrice());
        itemDTO.setLocation(item.getLocation());
        itemDTO.setSourceImage(item.getSourceImage());
        itemDTO.setCategoryId(item.getCategory().getCategoryId());
        itemDTO.setUserEmail(item.getUser().getEmail());

        return itemDTO;

    }

    @Override
    public void deleteItem(int id, String username) {
        Item item = itemRepository.findByItemCode(id);
        if (item == null) {
            throw new RuntimeException("Item not found");
        }
        User user = userRepository.findByEmailAndRole(username, "SELLER");
        if (user == null) {
            throw new RuntimeException("User not found or invalid role");
        }
        itemRepository.delete(item);
    }

    @Override
    public List<ItemDTO> getItembyItemCode(int itemCode, String sellerEmail) {
        return itemRepository.findByItemCodeAndUserEmail(itemCode, sellerEmail).stream()
                .map(item -> modelMapper.map(item, ItemDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public ItemDTO getItemByCode(int itemCode) {
        Item item = itemRepository.findByItemCode(itemCode);
        if (item == null) {
            throw new RuntimeException("Item not found");
        }

        // Map to DTO
        return modelMapper.map(item, ItemDTO.class);
    }

//    @Override
//    public ItemDTO updateItem(ItemDTO itemDTO) {
//        if (!itemRepository.existsById(itemDTO.getItemCode())) {
//            throw new RuntimeException("Item not found");
//        }
//
//        Item existingItem = itemRepository.findById(itemDTO.getItemCode()).get();
//        modelMapper.map(itemDTO, existingItem);
//
//        // Update category if changed
//        if (itemDTO.getCategoryId() != null &&
//                (existingItem.getCategory() == null ||
//                        !existingItem.getCategory().getCategoryId().equals(itemDTO.getCategoryId()))) {
//            Categories category = categoryRepo.findById(itemDTO.getCategoryId())
//                    .orElseThrow(() -> new RuntimeException("Category not found"));
//            existingItem.setCategory(category);
//        }
//
//        // Update user if changed
//        if (itemDTO.getUserEmail() != null &&
//                (existingItem.getUser() == null ||
//                        !existingItem.getUser().getEmail().equals(itemDTO.getUserEmail()))) {
//            User user = userRepository.findById(itemDTO.getUserEmail())
//                    .orElseThrow(() -> new RuntimeException("User not found"));
//            existingItem.setUser(user);
//        }
//
//        itemRepository.save(existingItem);
//        return modelMapper.map(existingItem, ItemDTO.class);
//    }

//    @Override
//    public void deleteItem(String itemCode) {
//        if (!itemRepository.existsById(itemCode)) {
//            throw new RuntimeException("Item not found");
//        }
//        itemRepository.deleteById(itemCode);
//    }
//
//    @Override
//    public ItemDTO findItem(String itemCode) {
//        Item item = itemRepository.findById(itemCode)
//                .orElseThrow(() -> new RuntimeException("Item not found"));
//        return modelMapper.map(item, ItemDTO.class);
//    }
//
//    @Override
//    public List<ItemDTO> getAllItems() {
//        return itemRepository.findAll().stream()
//                .map(item -> modelMapper.map(item, ItemDTO.class))
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public List<ItemDTO> getItemsByCategory(String categoryId) {
//        Categories category = categoryRepo.findById(categoryId)
//                .orElseThrow(() -> new RuntimeException("Category not found"));
//
//        return itemRepository.findByCategory(category).stream()
//                .map(item -> modelMapper.map(item, ItemDTO.class))
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public List<ItemDTO> getItemsByUser(String userEmail) {
//        User user = userRepository.findById(userEmail)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        return itemRepository.findByUser(user).stream()
//                .map(item -> modelMapper.map(item, ItemDTO.class))
//                .collect(Collectors.toList());
//    }
}
