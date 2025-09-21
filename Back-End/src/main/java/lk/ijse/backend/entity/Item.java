package lk.ijse.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class Item implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int itemCode;
    private String itemName;
    private String description;
    private int quantity;
    private double price;
    private String location;
    private String sourceImage;
    @ManyToOne
    private Categories category;
    @ManyToOne
    private User user;
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    private List<ReviewsRatings> reviewsRatings;
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderDetails> orderDetails;

}
