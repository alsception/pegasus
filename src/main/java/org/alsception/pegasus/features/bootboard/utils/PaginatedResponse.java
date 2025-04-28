package org.alsception.pegasus.features.bootboard.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class PaginatedResponse<T> {
    private List<T> content;
    private long totalItems;
    private int totalPages;
    private int currentPage;
}
