package lk.ijse.backend.controller;
import lk.ijse.backend.dto.ItemDTO;
import lk.ijse.backend.dto.ItemDataDTO;
import lk.ijse.backend.dto.ResponseDTO;
import lk.ijse.backend.model.ItemModel;
import lk.ijse.backend.service.ItemService;
import lk.ijse.backend.service.UserService;
import lk.ijse.backend.service.imple.UserServiceImpl;
import lk.ijse.backend.util.JwtUtil;
import lk.ijse.backend.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/addsItem")
@CrossOrigin
public class ItemController {
    @Autowired
    private final ItemService itemService;
    @Autowired
    private final UserService userService;
    @Autowired
    private final JwtUtil jwtUtil;
    @Autowired
    private final UserServiceImpl userServiceImpl;

    public ItemController(ItemService itemService, UserService userService, JwtUtil jwtUtil, UserServiceImpl userServiceImpl) {
        this.itemService = itemService;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.userServiceImpl = userServiceImpl;
    }


    @PostMapping(path = "/save",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('SELLER')")
    //@PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ResponseDTO> saveItemImage(@ModelAttribute ItemDataDTO itemDataDTO,
                                                     @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String username = jwtUtil.getUsernameFromToken(token);

            userServiceImpl.loadUserByUsername(username);

            String savedItem = userService.saveItemImage(itemDataDTO.getSourceImage());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ResponseDTO(HttpStatus.CREATED.value(), "Success", savedItem));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ResponseDTO(HttpStatus.FORBIDDEN.value(), "Only sellers can upload items", null));
        }
    }
    @PostMapping(path = "/saveItem", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('SELLER')")
    public ResponseEntity<ResponseDTO> saveItem(@RequestHeader("Authorization") String token,
                                            @ModelAttribute ItemDataDTO itemDataDTO) {
        String email = jwtUtil.getUsernameFromToken(token.substring(7));
        System.out.println("save item calling");
        ItemDTO savedItem = itemService.saveItem(itemDataDTO, email);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseDTO(
                        HttpStatus.CREATED.value(), "Item saved successfully", savedItem
                ));
    }

    @GetMapping(path = "/get")
    @PreAuthorize("hasAnyAuthority('SELLER','BUYER')")
    public ResponseEntity<ResponseDTO> getItemsBySeller(@AuthenticationPrincipal UserDetails userDetails) {
        String sellerEmail = userDetails.getUsername();
        List<ItemDTO> items = itemService.getItemsBySeller(sellerEmail);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseDTO(HttpStatus.OK.value(), "Items fetched successfully", items));
    }
    @GetMapping(path = "/getAll")
    @PreAuthorize("hasAnyAuthority('SELLER','BUYER')")
    public ResponseEntity<ResponseDTO> getAllItems(@RequestHeader("Authorization") String token) {
        String username = jwtUtil.getUsernameFromToken(token.substring(7));
        List<ItemModel> items = itemService.getAllItems();
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseDTO(HttpStatus.OK.value(), "Items fetched successfully", items));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ResponseUtil> getItemsByCategoryAndSeller(
            @PathVariable String categoryId,
            @AuthenticationPrincipal UserDetails userDetails) {
        String sellerEmail = userDetails.getUsername();
        List<ItemDTO> items = itemService.getItemsByCategoryAndSeller(categoryId, sellerEmail);
        return ResponseEntity.ok(new ResponseUtil(200, "Items fetched successfully", items));
    }
    @GetMapping(path = "{itemCode}")
    public ResponseEntity<ResponseDTO> getItemsByItemCode(
            @PathVariable int itemCode,
            @AuthenticationPrincipal UserDetails userDetails) {
        String sellerEmail = userDetails.getUsername();
        List<ItemDTO> items = itemService.getItembyItemCode(itemCode, sellerEmail);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseDTO(HttpStatus.OK.value(), "Items fetched successfully", items));
    }

    @PutMapping(path = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ItemDTO> updateItem(@RequestHeader("Authorization") String token,
                                              @ModelAttribute ItemDataDTO itemDataDTO) {
        String email = jwtUtil.getUsernameFromToken(token.substring(7));

        ItemDTO updatedItem = itemService.updateItem(itemDataDTO, email);
        return ResponseEntity.ok(updatedItem);
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseDTO> deleteItem(@PathVariable String id,
                                                   @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String username = jwtUtil.getUsernameFromToken(token);

        itemService.deleteItem(Integer.parseInt(id), username);
        return ResponseEntity.ok()
                .body(new ResponseDTO(HttpStatus.OK.value(), "Item deleted successfully", null));
    }

}
