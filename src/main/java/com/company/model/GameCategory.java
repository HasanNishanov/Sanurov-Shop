package com.company.model;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class GameCategory {
    private Integer id;
    private String name;
    private Integer product_category_id;
    private String isDeleted = "FALSE";
}
