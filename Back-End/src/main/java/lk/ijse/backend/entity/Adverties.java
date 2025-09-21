package lk.ijse.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class Adverties {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int advertiesId;
    @ManyToOne
    private User userId;
    private String itemId;



}
