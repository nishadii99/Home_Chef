package lk.ijse.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileDataDTO {
    private String firstName;
    private String lastName;
    private MultipartFile image;
    private String contact;
    private String address;
}
