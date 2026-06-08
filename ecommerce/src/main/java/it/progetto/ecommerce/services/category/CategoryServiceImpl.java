package it.progetto.ecommerce.services.category;

import it.progetto.ecommerce.model.dto.CategoryDTO;
import it.progetto.ecommerce.model.entities.CategoryEntity;
import it.progetto.ecommerce.model.mapper.CategoryMapper;
import it.progetto.ecommerce.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryDTO> getAllCategoriesDTO() {
        List<CategoryEntity> categorie = categoryRepository.findAll();
        List<CategoryDTO> ret = new LinkedList<>();
        for(CategoryEntity category : categorie){
            ret.add(categoryMapper.toDto(category));
        }
        return ret;
    }

    @Override
    public boolean hasCategoryWithName(String name) {
        return categoryRepository.findByName(name) != null;
    }

    @Override
    public CategoryEntity createCategory(CategoryDTO categoryDTO) {
        CategoryEntity category = new CategoryEntity();
        category.setName(categoryDTO.getName());
        category.setDescription(categoryDTO.getDescription());
        return categoryRepository.save(category);
    }



}
