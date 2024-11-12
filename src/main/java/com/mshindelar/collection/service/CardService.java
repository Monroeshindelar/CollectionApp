package com.mshindelar.collection.service;

import com.mshindelar.collection.YGOPROApi.YGOPROApiClient;
import com.mshindelar.collection.YGOPROApi.dto.MiscCardInfoDto;
import com.mshindelar.collection.YGOPROApi.dto.YGOCardDto;
import com.mshindelar.collection.exception.NoSuchCardException;
import com.mshindelar.collection.model.card.Card;
import com.mshindelar.collection.model.card.Print;
import com.mshindelar.collection.model.card.ygo.YGOCard;
import com.mshindelar.collection.db.filter.AbstractFilter;
import com.mshindelar.collection.db.filter.card.CardSpecification;
import com.mshindelar.collection.repository.CardRepository;
import com.mshindelar.collection.repository.PrintRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CardService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CardService.class);
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private PrintRepository printRepository;
    @Autowired
    private YGOPROApiClient ygoproApiClient;

//    @Scheduled(cron = "0 * * * * *")
    @Scheduled(cron = "0 0 23 * * SUN")
    @Async
    public void refreshDatabase() throws IOException {
        LOGGER.info("Refreshing database with cards from YGOPRODeck");
        List<YGOCardDto> cards = this.ygoproApiClient.getCards();
        this.saveCards(cards);
        LOGGER.info("Database successfully refreshed with {} cards",  cards.size());
    }

    public List<String> getUniqueTypes() {
        return this.cardRepository.findDistinctTypeAsc();
    }

    public List<String> getUniqueRaces() {
        return this.cardRepository.findUniqueRaceAsc();
    }

    public List<String> getUniqueArchetypes() {
        return this.cardRepository.findUniqueArchetypeAsc();
    }

    public Card getCard(String id) {
        return cardRepository.findById(id).orElse(null);
    }

    public List<Card> getCards(AbstractFilter filter) {
        return cardRepository.findAll(new CardSpecification(filter), Sort.by("name"));
    }

    public Print getPrint(String setCode, String rarityCode) throws NoSuchCardException {
        return printRepository.findById(new Print.PrintId(setCode, rarityCode))
                .orElseThrow(() ->
                    new NoSuchCardException(String.format("Print with set %s and rarity %s does not exist",
                        setCode, rarityCode)));
    }

    public void saveCards(List<YGOCardDto> cards) {
        List<YGOCard> cardss = cards.stream()
                .map(this::fromDto)
                .collect(Collectors.toList());

        cardRepository.saveAll(cardss);
    }

    private YGOCard fromDto(YGOCardDto dto) {
        YGOCard card = modelMapper.map(dto, YGOCard.class);

        if (dto.getCardSets() != null) {
            List<Print> prints = dto.getCardSets().stream()
                    .map(x -> new Print(x.getSetCode(), x.getSetRarityCode(), card, new LinkedList<>()))
                    .toList();

            card.setPrints(prints);
        }

        MiscCardInfoDto misc = dto.getMiscInfo().getFirst();
        card.setTcgRelease(misc.getTcgDate());
        card.setOcgRelease(misc.getOcgDate());
        card.setHasEffect(misc.isHasEffect());

        return card;
    }
}
