package it.progetto.ecommerce.repository;


import it.progetto.ecommerce.model.entities.CarrelloProductEntity;
import it.progetto.ecommerce.model.entities.UserEntity;
import it.progetto.ecommerce.model.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    UserEntity findFirstByEmail(String email);
    UserEntity findByRole(UserRole userRole);

}
