package com.mshindelar.collection.model.collection;

import com.mshindelar.collection.dto.collection.CollectionPrintOccurrenceDto;
import com.mshindelar.collection.model.DaoConverter;
import com.mshindelar.collection.model.card.Condition;
import com.mshindelar.collection.model.card.Edition;
import jakarta.persistence.*;
import lombok.*;
import org.modelmapper.ModelMapper;

import java.io.Serializable;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "collection_print_occurrence")
@IdClass(CollectionPrintOccurrence.CollectionPrintOccurrenceId.class)
public class CollectionPrintOccurrence implements Serializable, DaoConverter<CollectionPrintOccurrenceDto> {
    @Id
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private CollectionPrint collectionPrint;
    @Id
    @Enumerated(EnumType.STRING)
    private Condition condition;
    @Id
    @Enumerated(EnumType.STRING)
    private Edition edition;
    private int quantity;
    private int grade;

    @Override
    public CollectionPrintOccurrenceDto convertToDto(ModelMapper modelMapper) {
        return modelMapper.map(this, CollectionPrintOccurrenceDto.class);
    }

    public static class Builder {
        private CollectionPrint collectionPrint;
        private Condition condition = Condition.LIGHT_PLAY;
        private Edition edition = Edition.UNLIMITED;
        private int quantity = 0;
        private int grade = -1;

        public Builder withCollectionPrint(CollectionPrint collectionPrint) {
            this.collectionPrint = collectionPrint;
            return this;
        }

        public Builder withCondition(Condition condition) {
            this.condition = condition;
            return this;
        }

        public Builder withEdition(Edition edition) {
            this.edition = edition;
            return this;
        }

        public Builder withQuantity(int quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder withGrade(int grade) {
            this.grade = grade;
            return this;
        }

        public CollectionPrintOccurrence build() {
            if (this.condition.equals(Condition.GRADE) && this.grade == -1) {
                throw new IllegalStateException();
            }

            if (!this.condition.equals(Condition.GRADE) && grade > 0) {
                throw new IllegalStateException();
            }

            return new CollectionPrintOccurrence(collectionPrint, condition, edition, quantity, grade);
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CollectionPrintOccurrenceId {
        private CollectionPrint collectionPrint;
        private Condition condition;
        private Edition edition;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CollectionPrintOccurrenceId that = (CollectionPrintOccurrenceId) o;
            return collectionPrint.equals(that.collectionPrint) && condition == that.condition && edition == that.edition;
        }

        @Override
        public int hashCode() {
            return Objects.hash(collectionPrint, condition, edition);
        }
    }
}
