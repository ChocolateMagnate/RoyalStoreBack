package com.royal.models.products.search;

import com.royal.models.products.enumerations.DesktopBrand;
import com.royal.models.products.enumerations.DesktopOS;
import lombok.Getter;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;


@Getter
public class LaptopSearchFilter {
    private Integer lowerPriceBond;
    private Integer upperPriceBond;
    private Integer lowerMemoryBond;
    private Integer upperMemoryBond;
    private DesktopBrand brand;
    private DesktopOS os;
    private String model;

    public Query getSearchQuery() {
        Criteria criteria = getCriteria();
        return new Query(criteria);
    }

    protected Criteria getCriteria() {
        var criteria = new Criteria();
        if (lowerPriceBond != null) criteria.and("price").gte(lowerMemoryBond);
        if (upperPriceBond != null) criteria.and("price").lte(upperMemoryBond);
        if (lowerMemoryBond != null) criteria.and("memory").gte(lowerMemoryBond);
        if (upperMemoryBond != null) criteria.and("memory").lte(upperMemoryBond);
        if (brand != null) criteria.and("brand").is(brand);
        if (os != null) criteria.and("os").is(os);
        if (model != null) criteria.and("model").is(model);
        return criteria;
    }
}
