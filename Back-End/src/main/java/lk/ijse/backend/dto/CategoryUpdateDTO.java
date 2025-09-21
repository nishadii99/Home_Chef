package lk.ijse.backend.dto;

import jakarta.validation.constraints.Size;

public class CategoryUpdateDTO {
    @Size(min = 2, max = 20, message = "Name must be between 2 and 50 characters")
    private String name;

    public CategoryUpdateDTO() {
    }

    public CategoryUpdateDTO(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "CategoryUpdateDTO{" +
                "name='" + name + '\'' +
                '}';
    }
}
