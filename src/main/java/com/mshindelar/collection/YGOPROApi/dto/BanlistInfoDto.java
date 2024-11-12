package com.mshindelar.collection.YGOPROApi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BanlistInfoDto {
    @JsonProperty(value = "ban_tcg")
    private String banTCG;
    @JsonProperty(value = "ban_ocg")
    private String banOCG;
}
