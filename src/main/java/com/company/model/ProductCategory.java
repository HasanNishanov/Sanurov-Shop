package com.company.model;


import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ProductCategory {
    private Integer id;
    private String name;
    private String isDeleted = "FALSE";

    public ProductCategory(String name) {
        this.name = name;
    }
}
