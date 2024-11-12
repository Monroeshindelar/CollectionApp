package com.mshindelar.collection.model.card;

import com.mshindelar.collection.dto.card.PrintDto;
import com.mshindelar.collection.model.DaoConverter;
import com.mshindelar.collection.model.collection.CollectionPrint;
import jakarta.persistence.*;
import lombok.*;
import org.modelmapper.ModelMapper;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "print")
@IdClass(Print.PrintId.class)
public class Print implements Serializable, DaoConverter<PrintDto> {
    @Id
    private String id;
    @Id
    @Column(name = "set_rarity_code")
    private String setRarityCode;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private Card card;

    @OneToMany(mappedBy = "print", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CollectionPrint> collectionPrints;

    @Override
    public PrintDto convertToDto(ModelMapper modelMapper) { return modelMapper.map(this, PrintDto.class); }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PrintId implements Serializable {
        private String id;
        private String setRarityCode;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PrintId printId = (PrintId) o;
            return id.equals(printId.id) && setRarityCode.equals(printId.setRarityCode);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, setRarityCode);
        }
    }
}
