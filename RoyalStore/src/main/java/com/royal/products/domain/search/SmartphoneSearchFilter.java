package com.royal.products.domain.search;

import com.royal.products.domain.enumerations.MobileBrand;
import com.royal.products.domain.enumerations.MobileOS;
import lombok.Getter;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

@Getter
public class SmartphoneSearchFilter {
    private Integer lowerPriceBond;
    private Integer upperPriceBond;
    private Integer memory;
    private MobileBrand brand;
    private MobileOS os;
    private String model;

    public Query getSearchQuery() {
        var criteria = new Criteria();
        String pattern = String.format("^.*%s.*$", model);
        if (model != null) criteria.and("model").regex(pattern);
        if (os != null) criteria.and("os").is(os);
        if (brand != null) criteria.and("brand").is(brand);
        if (memory != null) criteria.and("memory").lte(memory);
        if (lowerPriceBond != null && upperPriceBond != null)
            criteria.and("price").gte(lowerPriceBond).lte(upperPriceBond);
        else if (lowerPriceBond != null) criteria.and("price").gte(lowerPriceBond);
        else if (upperPriceBond != null) criteria.and("price").lte(upperPriceBond);

        return new Query(criteria);
    }

    public String toString() {
        return model + " " + os + " " + upperPriceBond + "-" + lowerPriceBond + "$ "
                + memory + "MB";
    }
}
