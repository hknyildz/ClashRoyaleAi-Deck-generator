package com.deftgray.clashproxy.dto;

import lombok.Data;

import java.util.List;

@Data
public class ClashApıResponse {

    private String tag;
    private String name;
    private String expLevel;
    private String trophies;
    private List<CardDto> cards;

}
