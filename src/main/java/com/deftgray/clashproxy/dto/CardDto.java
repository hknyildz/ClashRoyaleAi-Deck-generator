package com.deftgray.clashproxy.dto;

import lombok.Data;

@Data
public class CardDto {

    private String name;
    private Long id;
    private Integer level;
    private Integer maxLevel;
    private Integer evolutionLevel;
    private Integer maxEvolutionLevel;
    private String rarity;
    private Integer count;
    private Integer elixirCost;
    private IconUrls iconUrls;

    @Data
    public static class IconUrls {
        private String medium;
        private String evolutionMedium;
        private String heroMedium;
    }
}
