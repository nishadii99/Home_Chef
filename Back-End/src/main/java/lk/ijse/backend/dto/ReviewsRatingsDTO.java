package lk.ijse.backend.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class ReviewsRatingsDTO {
    private Long reviewId;
    @Size(max = 1000)
    private String review;
    @Size(min = 1,max = 5)
    private int rating;
    @Column(updatable = false)
    @CreationTimestamp
    private Timestamp date;
    private UserDTO username;
    private ItemDTO itemCode;

}
