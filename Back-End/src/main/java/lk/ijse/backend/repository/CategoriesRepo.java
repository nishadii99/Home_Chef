package lk.ijse.backend.repository;


import lk.ijse.backend.dto.CategoriesDTO;
import lk.ijse.backend.entity.Categories;
import lk.ijse.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriesRepo extends JpaRepository<Categories,Integer> {


    boolean existsByName(String name);

    @Query("SELECT COUNT(c) > 0 FROM Categories c WHERE c.name = :name AND c.categoryId <> :id")
    boolean existsByNameAndIdNot(@Param("name") String name, @Param("id") int id);
}
