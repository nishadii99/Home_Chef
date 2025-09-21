package lk.ijse.backend.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lk.ijse.backend.dto.CategoriesDTO;
import lk.ijse.backend.dto.CategoryUpdateDTO;
import lk.ijse.backend.dto.ResponseDTO;
import lk.ijse.backend.service.CategoriesService;
import lk.ijse.backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@CrossOrigin
public class CategoriesController {
    @Autowired
    private final CategoriesService categoriesService;
    @Autowired
    private final JwtUtil jwtUtil;

    public CategoriesController(CategoriesService categoriesService, JwtUtil jwtUtil) {
        this.categoriesService = categoriesService;
        this.jwtUtil = jwtUtil;
    }


    @PostMapping(path = "/save", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('SELLER')")
    public ResponseEntity<CategoriesDTO> createCategory(@RequestBody CategoriesDTO categoryDTO) {
        CategoriesDTO savedCategory = categoriesService.saveCategory(categoryDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCategory);
    }


    @GetMapping(path = "/get")
//    @PreAuthorize("hasAnyAuthority('SELLER','BUYER')")
    public ResponseEntity<ResponseDTO> getAllCategories(@RequestHeader("Authorization") String token) {
        String email = jwtUtil.getUsernameFromToken(token.substring(7));
        List<CategoriesDTO> categories = categoriesService.getAllCategories();
        return ResponseEntity.ok()
                .body(new ResponseDTO(HttpStatus.OK.value(), "Success", categories));
    }

    @PutMapping(path = "update",params = "id",consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('SELLER')")
    public ResponseEntity<ResponseDTO> updateCategory(
            @RequestParam ("id") int id,
            @Valid @RequestBody CategoryUpdateDTO updateDTO,
            @RequestHeader("Authorization") String authHeader) {

        try {
            String token = authHeader.replace("Bearer ", "");
            String username = jwtUtil.getUsernameFromToken(token);

            CategoriesDTO updatedCategory = categoriesService.updateCategory(id, updateDTO, username);

            return ResponseEntity.ok()
                    .body(new ResponseDTO(HttpStatus.OK.value(), "Category updated successfully", updatedCategory));

        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO(HttpStatus.NOT_FOUND.value(), ex.getMessage(), null));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ResponseDTO(HttpStatus.FORBIDDEN.value(), ex.getMessage(), null));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage(), null));
        }
    }

    @DeleteMapping(path = "delete",params = "id")
    @PreAuthorize("hasAnyAuthority('SELLER')")
    public ResponseEntity<ResponseDTO> deleteCategory(
            @RequestParam ("id") int id,
            @RequestHeader("Authorization") String authHeader) {

        try {

            String token = authHeader.replace("Bearer ", "");
            String username = jwtUtil.getUsernameFromToken(token);

            categoriesService.deleteCategory(id, username);

            return ResponseEntity.ok()
                    .body(new ResponseDTO(HttpStatus.OK.value(), "Category deleted successfully", null));

        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO(HttpStatus.NOT_FOUND.value(), ex.getMessage(), null));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ResponseDTO(HttpStatus.FORBIDDEN.value(), ex.getMessage(), null));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error deleting category", null));
        }
    }

}
