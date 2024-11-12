package com.mshindelar.collection.controller;

import com.mshindelar.collection.dto.card.CardDto;
import com.mshindelar.collection.exception.InvalidFilterException;
import com.mshindelar.collection.model.collection.request.CardFilterChain;
import com.mshindelar.collection.service.CardService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cards")
public class CardController {
    @Autowired
    private CardService cardService;
    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/filter")
    public List<CardDto> getFilteredCards(@RequestBody CardFilterChain filterChain) {
        try {
            return this.cardService.getCards(filterChain.toFilter()).stream().map(card ->
                    card.convertToDto(modelMapper)).toList();
        } catch (InvalidFilterException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Improperly formatted card filter", e);
        }
    }

    @GetMapping("/ygo/type")
    public List<String> getUniqueTypes() {
        return this.cardService.getUniqueTypes();
    }

    @GetMapping("/ygo/race")
    public List<String> getUniqueRaces() {
        return this.cardService.getUniqueTypes();
    }

    @GetMapping("/ygo/archetype")
    public List<String> getUniqueArchetypes() {
        return this.cardService.getUniqueTypes();
    }

    @GetMapping("ygo/allUnique")
    public Map<String, List<String>> getUnique() {
        Map<String, List<String>> map = new HashMap<>();
        map.put("Type", this.cardService.getUniqueTypes());
        map.put("Race", this.cardService.getUniqueTypes());
        map.put("Archetype", this.cardService.getUniqueArchetypes());
        return map;
    }
}
