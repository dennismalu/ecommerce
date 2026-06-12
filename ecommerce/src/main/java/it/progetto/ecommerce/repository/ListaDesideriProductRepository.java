package it.progetto.ecommerce.repository;

import it.progetto.ecommerce.model.entities.ListaDesideriProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ListaDesideriProductRepository extends JpaRepository<ListaDesideriProductEntity, Long> {

    ListaDesideriProductEntity findFirstById(Long id);
    List<ListaDesideriProductEntity> getAllByUser_IdOrderByProduct_Name(Long id);

}
