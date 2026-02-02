package com.deftgray.clashproxy.model;

import lombok.Data;

@Data
public class Card {

    private String name;
    private Integer level;
    private Boolean evolved;
    private Integer elixirCost;
    private String imageUri;
    private String imageUriEvolved;
    private Boolean isHero;
    private String imageUriHero;
    private String rarity;
    private String type;
    private Long id;

}
