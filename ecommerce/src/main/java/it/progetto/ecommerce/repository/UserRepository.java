package it.progetto.ecommerce.repository;


import it.progetto.ecommerce.model.entities.CarrelloProductEntity;
import it.progetto.ecommerce.model.entities.UserEntity;
import it.progetto.ecommerce.model.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    UserEntity findFirstByEmail(String email);
    UserEntity findByRole(UserRole userRole);
    UserEntity getUserEntityById(Long id);

    @Modifying @Transactional
    @Query("UPDATE UserEntity u SET u.lastSearch = :lastSearch WHERE u.id = :id")
    void updateLastSearchById(@Param("id") Long id, @Param("lastSearch") String lastSearch);
}
