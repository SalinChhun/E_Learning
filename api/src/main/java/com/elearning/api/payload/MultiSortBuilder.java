package com.elearning.api.payload;


import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MultiSortBuilder {
    private final List<Sort.Order> orders;

    public MultiSortBuilder() {
        this.orders = new ArrayList<>();
    }

    private String getProperty(String property){
        return switch (property){
            default -> property;
        };
    }

    public final MultiSortBuilder with(String sortDirection) {

        Pattern pattern = Pattern.compile("(\\w+?)(:|<|>)(\\w+?),");
        Matcher matcher = pattern.matcher(sortDirection + ",");
        while (matcher.find()) {
//            Old_Version
//            orders.add(new Order(Sort.Direction.fromString(matcher.group(3)), matcher.group(1)));

//            New_Version
            String sortField = matcher.group(1);
            Sort.Direction direction = Sort.Direction.fromString(matcher.group(3));
            // Check if the current sort field is tax_code1
            if ("tax_code1".equals(sortField)) {
                // If sorting by tax_code1, also sort by tax_cd and id_doc_no
                orders.add(new Sort.Order(direction, getProperty("tax_cd")));
                orders.add(new Sort.Order(direction, getProperty("id_doc_no")));
            }else{
                orders.add(new Sort.Order(Sort.Direction.fromString(matcher.group(3)), matcher.group(1)));
            }
        }

        return this;
    }

    public List<Sort.Order> build() {
        if (orders.size() == 0)
            return Collections.emptyList();

        return orders;
    }
}