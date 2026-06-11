package it.progetto.ecommerce.model.dto.pagedResponses;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class PageEntityResponseDTO<T> {
    private List<T> list;
    private long totalPages;
    private long totalElements;
}

