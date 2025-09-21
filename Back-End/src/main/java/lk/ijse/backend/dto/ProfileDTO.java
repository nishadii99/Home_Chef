package lk.ijse.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class ProfileDTO {
    @Email(message = "invalid format")
    private String email;
    private String image;
//    @Size(min = 2,max = 30,message = "Name min length is 2  ")
//    private String name;
    @Size(min = 5,max = 30,message = "Name min length is 5  ")
    private String address;
    @Pattern(regexp = "^[0-9]{10}$",message = "Phone number must be 10 digits")
    private String contact;
    private String firstName;
    private String lastName;
    private LocalDate joinDate;

}
