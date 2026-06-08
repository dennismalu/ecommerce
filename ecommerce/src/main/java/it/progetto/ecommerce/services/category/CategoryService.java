package it.progetto.ecommerce.services.category;

import it.progetto.ecommerce.model.dto.CategoryDTO;
import it.progetto.ecommerce.model.entities.CategoryEntity;

import java.util.List;

public interface CategoryService {
    List<CategoryDTO> getAllCategoriesDTO();
    boolean hasCategoryWithName(String name);
    CategoryEntity createCategory(CategoryDTO categoryDTO);
}
