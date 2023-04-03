package com.company.model;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Preview {
    private Integer id;
    private Integer price;
    private String image;
    private String name;
    private String isDeleted = "FALSE";
    private Integer product_category_id;
    private Integer game_category_id;

    public Preview(String image) {
        this.image = image;
    }
}
