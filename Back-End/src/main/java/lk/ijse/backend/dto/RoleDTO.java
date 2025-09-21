package lk.ijse.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleDTO {
    private String roleId;
    @NotBlank(message = "Role name is required")
    private String roleName;
    private String privileges;


}
