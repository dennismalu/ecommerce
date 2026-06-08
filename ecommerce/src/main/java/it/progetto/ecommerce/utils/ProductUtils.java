package it.progetto.ecommerce.utils;

import org.springframework.data.domain.Sort;

public class ProductUtils {

    public static Sort setSortConfig(int sort) {
        Sort sortConfig;

        switch (sort) {
//            case 0:
//                sortConfig = Sort.by("name").ascending();
//                break;
            case 1:
                sortConfig = Sort.by("name").descending(); //1: Nome (Z-A)
                break;
            case 2:
                sortConfig = Sort.by("price").ascending(); //2: Prezzo (Crescente)
                break;
            case 3:
                sortConfig = Sort.by("price").descending(); //3: Prezzo (Decrescente)
                break;
            default:
                sortConfig = Sort.by("name").ascending(); //0: Nome (A-Z)
                //in caso di 0 o di altro valore ordino per nome crescente
        }

        return sortConfig;
    }

}

