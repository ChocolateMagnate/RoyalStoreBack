package com.royal.products.domain.requests;

import com.royal.products.domain.characteristics.Characteristic;
import com.royal.products.domain.characteristics.CharacteristicsSet;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;


@Data
public class SearchElectronicProductRequest {
    private String id;
    private String model;
    private Integer lowerPriceBond;
    private Integer upperPriceBond;
    private Integer memory;
    private CharacteristicsSet characteristics = new CharacteristicsSet();

    public void addCharacteristic(Characteristic<?> characteristic) {
        this.characteristics.add(characteristic);
    }

    public void removeCharacteristic(Characteristic<?> characteristic) {
        this.characteristics.remove(characteristic);
    }

    public Query getSearchQuery() {
        Criteria criteria = getCriteria();
        return new Query(criteria);
    }

    @NotNull
    protected Criteria getCriteria() {
        var criteria = new Criteria();
        if (id != null) criteria.and("id").is(id);
        if (memory != null) criteria.and("memory").lte(memory);
        if (model != null) criteria.and("model").is(model);
        // We use all() instead of is() because the latter generates a weird query where it uses
        // a "$java" key followed by object converted to string, instead of matching it as an array.
        if (characteristics != null) criteria.and("characteristics").all(characteristics);
        if (lowerPriceBond != null && upperPriceBond != null)
            criteria.and("price").gte(lowerPriceBond).lte(upperPriceBond);
        else if (lowerPriceBond != null) criteria.and("price").gte(lowerPriceBond);
        else if (upperPriceBond != null) criteria.and("price").lte(upperPriceBond);

        return criteria;
    }
}
