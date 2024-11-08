package com.mshindelar.collection.YGOPROApi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class YGOCardDto implements Serializable {
    private int id;
    private String name;
    private String type;
    private String humanReadableCardType;
    private String frameType;
    @JsonProperty("desc")
    private String description;
    @JsonProperty("atk")
    private int attack;
    @JsonProperty("def")
    private int defense;
    private int level;
    private String race;
    private String archetype;
    private Attribute attribute;
    @JsonProperty("card_sets")
    private List<YGOCardSetDto> cardSets;
    @JsonProperty("card_images")
    private List<CardImageDto> cardImages;
}
