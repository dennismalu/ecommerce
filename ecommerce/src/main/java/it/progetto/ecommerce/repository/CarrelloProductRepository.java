package it.progetto.ecommerce.repository;

import it.progetto.ecommerce.model.entities.CarrelloProductEntity;
import it.progetto.ecommerce.model.entities.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarrelloProductRepository extends JpaRepository<CarrelloProductEntity, Long> {

    CarrelloProductEntity findFirstById(Long id);
    //List<CarrelloProductEntity> getAllByUser_Id(Long id);
    List<CarrelloProductEntity> getAllByUser_IdOrderByProduct_Name(Long id);

}
