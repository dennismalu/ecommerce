package it.progetto.ecommerce.repository;

import it.progetto.ecommerce.model.entities.CategoryEntity;
import it.progetto.ecommerce.model.entities.UserEntity;
import it.progetto.ecommerce.model.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

    CategoryEntity findFirstById(long id);
    CategoryEntity findByName(String name);

}
