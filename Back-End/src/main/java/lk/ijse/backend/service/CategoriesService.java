package lk.ijse.backend.service;

import lk.ijse.backend.dto.CategoriesDTO;
import lk.ijse.backend.dto.CategoryUpdateDTO;

import java.util.List;

public interface CategoriesService {
    CategoriesDTO saveCategory(CategoriesDTO categoriesDTO);
//    CategoriesDTO getCategoryById(String id);
    List<CategoriesDTO> getAllCategories();
    CategoriesDTO updateCategory(int id, CategoryUpdateDTO updateDTO, String username);

    void deleteCategory(int id, String username);

}
