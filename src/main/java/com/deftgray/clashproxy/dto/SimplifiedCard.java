package com.deftgray.clashproxy.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SimplifiedCard {
    private String name;
    private Integer level;
    private boolean isEvolved;
    private boolean isHero;
    private Integer elixirCost;
}
