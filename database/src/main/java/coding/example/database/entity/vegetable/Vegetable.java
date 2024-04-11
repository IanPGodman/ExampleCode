package coding.example.database.entity.vegetable;

import jakarta.persistence.*;
import jakarta.persistence.Entity;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Vegetable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "veg_id", nullable = false)
    int veg_id;

    @Column(name = "display_name")
    String name;

    String image_name;
}
