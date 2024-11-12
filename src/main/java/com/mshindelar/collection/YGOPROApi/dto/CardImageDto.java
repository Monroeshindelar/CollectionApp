package com.mshindelar.collection.YGOPROApi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CardImageDto implements Serializable {
    private int id;
    @JsonProperty("image_url")
    private String imageUrl;
    @JsonProperty("image_url_small")
    private String smallUrl;
    @JsonProperty("image_url_cropped")
    private String croppedUrl;
}
