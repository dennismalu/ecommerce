package it.progetto.ecommerce.controllers;

import it.progetto.ecommerce.model.dto.CategoryDTO;
import it.progetto.ecommerce.model.dto.ProductDTO;
import it.progetto.ecommerce.model.dto.pagedResponses.PageEntityResponseDTO;
import it.progetto.ecommerce.model.exceptions.CustomException;
import it.progetto.ecommerce.model.mapper.CategoryMapper;
import it.progetto.ecommerce.model.mapper.ProductMapper;
import it.progetto.ecommerce.repository.CategoryRepository;
import it.progetto.ecommerce.services.category.CategoryService;
import it.progetto.ecommerce.services.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicController {
    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;
    private final ProductService productService;
    private final ProductMapper productMapper;

    @GetMapping("/categories")
    public ResponseEntity<?> getAllCategories() {
        List<CategoryDTO> categories = categoryService.getAllCategoriesDTO();
        if(categories == null || categories.isEmpty()){
            return new ResponseEntity<>(new LinkedList<>(), HttpStatus.OK); //stato 200
        }
        else{
            return new ResponseEntity<>(categories, HttpStatus.OK); //stato 200
        }
    }

    @GetMapping("/products")
    public ResponseEntity<?> getAllProducts() {
        List<ProductDTO> products = productService.getAllProductsDTO();
        if(products == null || products.isEmpty()){
            return new ResponseEntity<>(new LinkedList<>(), HttpStatus.OK); //stato 200
        }
        else{
            return new ResponseEntity<>(products, HttpStatus.OK); //stato 200
        }
    }

    //SOLO prodotti abilitati
    @GetMapping("/products-filtered-sortered")
    public ResponseEntity<?> getAllProductsFilteredSortered(
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
        // disabled=false --> facciamo la GET sui prodotti NON DISABILITATI (visibili ai clienti)
        PageEntityResponseDTO<ProductDTO> ret = productService.getAllProductsDTOFilteredSortered(false, page-1, size, sort, category);
        if(ret.getList() == null || ret.getList().isEmpty()){
            return new ResponseEntity<>(new PageEntityResponseDTO<ProductDTO>(
                    new LinkedList<>(), 0, 0), HttpStatus.OK); //stato 200
        }
        else{
            return new ResponseEntity<>(ret, HttpStatus.OK); //stato 200
        }
    }

    //SOLO prodotti abilitati
    @GetMapping("/search-products-filtered-sortered/{name}")
    public ResponseEntity<?> searchProductsProductsFilteredSortered(
            @PathVariable String name,
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
        if (name == null || name.isEmpty() || page < 1 || size < 1 || size > 100 || sort < 0 || sort > 3 || category < -1) {
            return new ResponseEntity<>("Errore nei parametri!", HttpStatus.BAD_REQUEST); //stato 400
        }
        // page-1 --> il numero di pagina (page) parte da 0 in Spring Data JPA (non da 1)
        // disabled=false --> facciamo la GET sui prodotti NON DISABILITATI (visibili ai clienti)
        PageEntityResponseDTO<ProductDTO> ret = productService.searchProductsByNameFilteredSortered(false, page-1, size, sort, category, name);
        if(ret.getList() == null || ret.getList().isEmpty()){
            return new ResponseEntity<>(new PageEntityResponseDTO<ProductDTO>(
                    new LinkedList<>(), 0, 0), HttpStatus.OK); //stato 200
        }
        else{
            return new ResponseEntity<>(ret, HttpStatus.OK); //stato 200
        }
    }

    @GetMapping("/product/{id}")
    public ResponseEntity<?> getProduct(@PathVariable Long id) {
        try {
            ProductDTO productDTO = productService.getProduct(id);
            return new ResponseEntity<>(productDTO, HttpStatus.OK); //stato 200
        } catch (CustomException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.valueOf(e.getStatusCode()));
        }
    }

}
