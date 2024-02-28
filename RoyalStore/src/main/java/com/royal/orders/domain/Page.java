package com.royal.orders.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Page<T> {
    private long total;
    private long index;
    private Set<T> content;
}
