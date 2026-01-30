package com.vatsalrajgor.eCommerce.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class PaginationProperties {
    @Value("${eCommerce.pageNumber}")
    private int pageNumber;

    @Value("${eCommerce.pageSize}")
    private int pageSize;

    @Value("${eCommerce.sortBy}")
    private String sortBy;

    @Value("${eCommerce.sortOrder}")
    private String sortOrder;
}
