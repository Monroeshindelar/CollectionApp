package com.mshindelar.collection.YGOPROApi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MiscCardInfoDto {
    @JsonProperty(value = "tcg_date")
    private Date tcgDate;
    @JsonProperty(value = "ocg_date")
    private Date ocgDate;
    @JsonProperty(value = "has_effect")
    private boolean hasEffect;
}
