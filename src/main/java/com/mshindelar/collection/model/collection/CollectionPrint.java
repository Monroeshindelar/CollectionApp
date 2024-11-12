package com.mshindelar.collection.model.collection;

import com.mshindelar.collection.dto.collection.CollectionPrintDto;
import com.mshindelar.collection.dto.collection.CollectionPrintOccurrenceDto;
import com.mshindelar.collection.model.DaoConverter;
import com.mshindelar.collection.model.card.Print;
import jakarta.persistence.*;
import lombok.*;
import org.modelmapper.ModelMapper;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
//@Table(name = "collection_print")
public class CollectionPrint implements Serializable, DaoConverter<CollectionPrintDto> {
    @Id
    @GeneratedValue
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    private CollectionCard collectionCard;
    @ManyToOne(fetch = FetchType.LAZY)
    private Print print;
    @OneToMany(mappedBy = "collectionPrint", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CollectionPrintOccurrence> occurrences;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CollectionPrint that = (CollectionPrint) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public CollectionPrintDto convertToDto(ModelMapper modelMapper) {
        CollectionPrintDto dto = new CollectionPrintDto();
        dto.setPrint(this.getPrint().convertToDto(modelMapper));
        List<CollectionPrintOccurrenceDto> occurrenceDtos = this.getOccurrences().stream()
                .map(oDao -> oDao.convertToDto(modelMapper))
                .toList();
        dto.setOccurrences(occurrenceDtos);
        return dto;
    }

    public static class Builder {
        private CollectionCard card;
        private Print print;
        private List<CollectionPrintOccurrence> occurrences = new LinkedList<>();

        public Builder withCollectionCard(CollectionCard collectionCard) {
            this.card = collectionCard;
            return this;
        }

        public Builder withPrint(Print print) {
            this.print = print;
            return this;
        }

        public CollectionPrint build() {
            if (this.card == null || this.print == null) throw new IllegalStateException();

            return new CollectionPrint(null, this.card, this.print, this.occurrences);
        }
    }
}
