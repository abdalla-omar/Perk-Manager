package com.example.perkmanager.query;

import com.example.perkmanager.enumerations.ProductType;
import jakarta.validation.constraints.NotNull;

public class GetPerksByProductQuery {
    @NotNull(message = "Product type is required")
    private ProductType product;

    public GetPerksByProductQuery() {}

    public GetPerksByProductQuery(ProductType product) {
        this.product = product;
    }

    public ProductType getProduct() {
        return product;
    }

    public void setProduct(ProductType product) {
        this.product = product;
    }
}

