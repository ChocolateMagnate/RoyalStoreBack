package com.royal.models.products.search;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import static org.springframework.data.mongodb.core.query.Criteria.where;


@Data
public class ElectronicProductSearchFilter {
    private String model;
    private Integer lowerPriceBond;
    private Integer upperPriceBond;
    private Integer memory;

    public Query getSearchQuery() {
        Criteria criteria = getCriteria();
        return new Query(criteria);
    }

    @NotNull
    protected Criteria getCriteria() {
        var criteria = new Criteria();
        if (memory != null) criteria.and("memory").lte(memory);
        if (model != null) criteria.and("model").is(model);
        if (lowerPriceBond != null && upperPriceBond != null)
            criteria.and("price").gte(lowerPriceBond).lte(upperPriceBond);
        else if (lowerPriceBond != null) criteria.and("price").gte(lowerPriceBond);
        else if (upperPriceBond != null) criteria.and("price").lte(upperPriceBond);

        return criteria;
    }
}
