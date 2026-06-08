package it.progetto.ecommerce.repository;

import it.progetto.ecommerce.model.entities.CategoryEntity;
import it.progetto.ecommerce.model.entities.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    ProductEntity findFirstById(long id);
    ProductEntity findByName(String name);

    // Tutti i prodotti attivi (disabled = false), paginati
    Page<ProductEntity> findByDisabled(Boolean disabled, Pageable pageable);

    // Prodotti attivi in una specifica categoria, paginati e ordinati
    // --> pageable contiene il tipo di ordinamento (Sort sortConfig)
    Page<ProductEntity> findByCategory_IdAndDisabled(Long categoryId, Boolean disabled, Pageable pageable);

    //IgnoreCase --> JPA esegue i confronti ignorando le maiuscole e minuscole: la ricerca non sarà case-sensitive
    Page<ProductEntity> findAllByNameContainingIgnoreCase(String name, Pageable pageable);
    Page<ProductEntity> findAllByNameContainingIgnoreCaseAndCategory_Id(String name, Long categoryId, Pageable pageable);
    Page<ProductEntity> findAllByNameContainingIgnoreCaseAndDisabled(String name, Boolean disabled, Pageable pageable);
    Page<ProductEntity> findAllByNameContainingIgnoreCaseAndCategory_IdAndDisabled(String name, Long categoryId, Boolean disabled, Pageable pageable);

//    @Modifying
//    @Transactional
//    @Query("UPDATE ProductEntity p SET p.disabled = :disabled WHERE p.id = :id")
//    void setDisabledById(@Param("id") Long id, @Param("disabled") Boolean disabled);

}
