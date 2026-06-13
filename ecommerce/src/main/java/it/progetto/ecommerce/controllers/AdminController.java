package it.progetto.ecommerce.controllers;

import it.progetto.ecommerce.model.dto.CategoryDTO;
import it.progetto.ecommerce.model.dto.ProductDTO;
import it.progetto.ecommerce.model.dto.pagedResponses.PageEntityResponseDTO;
import it.progetto.ecommerce.model.entities.CategoryEntity;
import it.progetto.ecommerce.model.entities.ProductEntity;
import it.progetto.ecommerce.model.exceptions.CustomException;
import it.progetto.ecommerce.model.mapper.CategoryMapper;
import it.progetto.ecommerce.model.mapper.ProductMapper;
import it.progetto.ecommerce.repository.CategoryRepository;
import it.progetto.ecommerce.services.category.CategoryService;
import it.progetto.ecommerce.services.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAuthority('ADMIN')")   //gli endpoint possono essere richiamati solo con ruolo ADMIN
@RequiredArgsConstructor
public class AdminController  {

    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;
    private final ProductService productService;
    private final ProductMapper productMapper;

    @PostMapping("/category")
    public ResponseEntity<?> createCategory(@RequestBody CategoryDTO categoryDTO) {
        try {
            CategoryEntity createdCategory = categoryService.createCategory(categoryDTO);
            CategoryDTO createdCategoryDTO = categoryMapper.toDto(createdCategory);
            return new ResponseEntity<>(createdCategoryDTO, HttpStatus.CREATED); //stato 201
        } catch (CustomException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatusCode.valueOf(e.getStatusCode()));
        }
    }

    //SOLO prodotti disabilitati
    @GetMapping("/products-disabled-filtered-sortered")
    public ResponseEntity<?> getAllProductsDisabledFilteredSortered(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam int sort,
            @RequestParam long category
    ) {
//        sort -->  0: Nome (A-Z)
//                  1: Nome (Z-A)
//                  2: Prezzo (Crescente)
//                  3: Prezzo (Decrescente)
//
//        category --> -1: tutte le categorie (all)

        // Validazione
        if (page < 1 || size < 1 || size > 100 || sort < 0 || sort > 3 || category < -1) {
            return new ResponseEntity<>("Errore nei parametri!", HttpStatus.BAD_REQUEST); //stato 400
        }
        // page-1 --> il numero di pagina (page) parte da 0 in Spring Data JPA (non da 1)
        // disabled=true --> facciamo la GET sui prodotti DISABILITATI (non visibili ai clienti)
        PageEntityResponseDTO<ProductDTO> ret = productService.getAllProductsDTOFilteredSortered(true, page-1, size, sort, category);
        if(ret.getList() == null || ret.getList().isEmpty()){
            return new ResponseEntity<>(new PageEntityResponseDTO<ProductDTO>(
                    new LinkedList<>(), 0, 0), HttpStatus.OK); //stato 200
        }
        else{
            return new ResponseEntity<>(ret, HttpStatus.OK); //stato 200
        }
    }

    //prodotti abilitati o tutti (abilitati e disabilitati insieme)
    @GetMapping("/search-products-with-disabled-filtered-sortered/{name}")
    public ResponseEntity<?> searchProductsProductsFilteredSortered(
            @PathVariable String name,
            @RequestParam int withDisabled,
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam int sort,
            @RequestParam long category
    ) {
//        sort -->  0: Nome (A-Z)
//                  1: Nome (Z-A)
//                  2: Prezzo (Crescente)
//                  3: Prezzo (Decrescente)
//
//        category --> -1: tutte le categorie (all)

        // Validazione
        if (name == null || name.isEmpty() || withDisabled < 0 || withDisabled > 1 || page < 1 || size < 1 || size > 100 || sort < 0 || sort > 3 || category < -1) {
            return new ResponseEntity<>("Errore nei parametri!", HttpStatus.BAD_REQUEST); //stato 400
        }

        boolean disabled = withDisabled == 1;
        //false=0  ---  true=1

        // page-1 --> il numero di pagina (page) parte da 0 in Spring Data JPA (non da 1)
        // disabled=false --> facciamo la GET sui prodotti NON DISABILITATI (visibili ai clienti)
        PageEntityResponseDTO<ProductDTO> ret = productService.searchProductsByNameFilteredSortered(disabled, page-1, size, sort, category, name);
        if(ret.getList() == null || ret.getList().isEmpty()){
            return new ResponseEntity<>(new PageEntityResponseDTO<ProductDTO>(
                    new LinkedList<>(), 0, 0), HttpStatus.OK); //stato 200
        }
        else{
            return new ResponseEntity<>(ret, HttpStatus.OK); //stato 200
        }
    }


    @PostMapping("/product")
    public ResponseEntity<?> createProduct(@RequestBody ProductDTO productDTO) {
        try {
            ProductEntity createdProduct = productService.createProduct(productDTO);
            ProductDTO createdProductDTO = productMapper.toDto(createdProduct);
            return new ResponseEntity<>(createdProductDTO, HttpStatus.CREATED); //stato 201
        } catch(CustomException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatusCode.valueOf(e.getStatusCode()));
        }
    }

    @PutMapping("/product")
    public ResponseEntity<?> updateProduct(@RequestBody ProductDTO productDTO) {
        try {
            ProductEntity updatedProduct = productService.updateProduct(productDTO);
            ProductDTO updatedProductDTO = productMapper.toDto(updatedProduct);
            return new ResponseEntity<>(updatedProductDTO, HttpStatus.CREATED); //stato 201
        } catch (CustomException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatusCode.valueOf(e.getStatusCode()));
        }

    }

    @PutMapping("/product/{id}/disable")
    public ResponseEntity<?> disableProduct(@PathVariable Long id) {
        try {
            productService.disableProduct(id);
            return new ResponseEntity<>("", HttpStatus.OK); //stato 200
        } catch (CustomException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatusCode.valueOf(e.getStatusCode()));
        }
    }

    @PutMapping("/product/{id}/enable")
    public ResponseEntity<?> enableProduct(@PathVariable Long id) {
        try {
            productService.enableProduct(id);
            return new ResponseEntity<>("", HttpStatus.OK); //stato 200
        } catch (CustomException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatusCode.valueOf(e.getStatusCode()));
        }
    }

    @DeleteMapping("/product/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return new ResponseEntity<>("", HttpStatus.OK); //stato 200
        } catch (CustomException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatusCode.valueOf(e.getStatusCode()));
        }
    }

}
