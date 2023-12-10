package com.royal.models.products;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;


public class ElectronicProductSearchFilter {
    private String model;
    private Integer lowerPriceBond;
    private Integer upperPriceBond;
    private Integer lowerMemoryBond;
    private Integer upperMemoryBond;
    private byte[] photo;

    public Query getSearchQuery() {
        Criteria criteria = getCriteria();
        return new Query(criteria);
    }

    @NotNull
    protected Criteria getCriteria() {
        Criteria criteria = new Criteria();
        if (lowerPriceBond != null) criteria.and("price").gte(lowerMemoryBond);
        if (upperPriceBond != null) criteria.and("price").lte(upperMemoryBond);
        if (lowerMemoryBond != null) criteria.and("memory").gte(lowerMemoryBond);
        if (upperMemoryBond != null) criteria.and("memory").lte(upperMemoryBond);
        if (model != null) criteria.and("model").is(model);
        if (photo != null) criteria.and("photo").is(photo);
        return criteria;
    }
}
