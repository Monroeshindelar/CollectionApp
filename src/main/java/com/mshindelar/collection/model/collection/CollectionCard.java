package com.mshindelar.collection.model.collection;

import com.mshindelar.collection.dto.collection.CollectionCardDto;
import com.mshindelar.collection.dto.collection.CollectionPrintDto;
import com.mshindelar.collection.model.DaoConverter;
import com.mshindelar.collection.model.card.Card;
import jakarta.persistence.*;
import lombok.*;
import org.modelmapper.ModelMapper;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "collection_card")
public class CollectionCard implements Serializable, DaoConverter<CollectionCardDto> {
    @Id
    @GeneratedValue
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    private Collection collection;
    @ManyToOne(fetch = FetchType.LAZY)
    private Card card;
    @OneToMany(mappedBy = "collectionCard", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CollectionPrint> prints;

    @Override
    public CollectionCardDto convertToDto(ModelMapper modelMapper) {
        CollectionCardDto collectionCardDto = new CollectionCardDto();
        collectionCardDto.setCard(this.getCard().convertToDto(modelMapper));
        List<CollectionPrintDto> printDtos = this.getPrints().stream()
                .map(cpDao -> cpDao.convertToDto(modelMapper))
                .toList();
        collectionCardDto.setPrints(printDtos);
        return collectionCardDto;
    }

    public static class Builder {
        private Collection collection;
        private Card card;
        private List<CollectionPrint> prints = new LinkedList<>();

        public Builder withCollection(Collection collection) {
            this.collection = collection;
            return this;
        }

        public Builder withCard(Card card) {
            this.card = card;
            return this;
        }

        public CollectionCard build() {
            if (this.collection == null || this.card == null) { throw new IllegalStateException(); }

            return new CollectionCard(null, this.collection, this.card, this.prints);
        }
    }
}
