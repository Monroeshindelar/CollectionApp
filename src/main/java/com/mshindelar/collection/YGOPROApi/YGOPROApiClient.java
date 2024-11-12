package com.mshindelar.collection.YGOPROApi;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mshindelar.collection.YGOPROApi.dto.YGOCardDto;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class YGOPROApiClient {
    private static final String BASE_URL = "https://db.ygoprodeck.com/api/v7/cardinfo.php?misc=yes";

    @Autowired
    private HttpClient client;
    @Autowired
    private ObjectMapper mapper;

    public List<YGOCardDto> getCards() throws IOException {
        HttpGet request = new HttpGet(BASE_URL);
        HttpResponse response = client.execute(request);
        String json = EntityUtils.toString(response.getEntity());
        JsonNode node = mapper.readTree(json);
        json = node.get("data").toString();

        return mapper.readValue(json, new TypeReference<List<YGOCardDto>>() {});
    }
}
