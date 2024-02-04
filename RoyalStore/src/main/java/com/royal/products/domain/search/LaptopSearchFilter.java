package com.royal.products.domain.search;

import com.royal.products.domain.enumerations.DesktopBrand;
import com.royal.products.domain.enumerations.DesktopOS;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;


@Getter
@Setter
@EqualsAndHashCode
public class LaptopSearchFilter {
    private Integer lowerPriceBond;
    private Integer upperPriceBond;
    private Integer memory;
    private DesktopBrand brand;
    private DesktopOS os;
    private String model;

    public Query getSearchQuery() {
        Criteria criteria = getCriteria();
        return new Query(criteria);
    }

    protected Criteria getCriteria() {
        var criteria = new Criteria();
        if (os != null) criteria.and("os").is(os);
        if (brand != null) criteria.and("brand").is(brand);
        if (memory != null) criteria.and("memory").lte(memory);
        if (model != null) criteria.and("model").is(model);
        if (lowerPriceBond != null && upperPriceBond != null)
            criteria.and("price").gte(lowerPriceBond).lte(upperPriceBond);
        else if (lowerPriceBond != null) criteria.and("price").gte(lowerPriceBond);
        else if (upperPriceBond != null) criteria.and("price").lte(upperPriceBond);

        return criteria;
    }
}
