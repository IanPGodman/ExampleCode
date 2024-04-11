package coding.example.database.entity.vegetable;

import org.springframework.data.jpa.repository.JpaRepository;


public interface VegetableRepository extends JpaRepository<Vegetable, Long> {
    void deleteByName(String name);
}