package com.mshindelar.collection.model.collection;

import com.mshindelar.collection.dto.collection.CollectionCardDto;
import com.mshindelar.collection.dto.collection.CollectionDto;
import com.mshindelar.collection.model.Account;
import com.mshindelar.collection.model.DaoConverter;
import jakarta.persistence.*;
import lombok.*;
import org.modelmapper.ModelMapper;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Collection implements Serializable, DaoConverter<CollectionDto> {
    @Id
    @GeneratedValue
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    private Account owner;
    private String name;
    @Embedded
    private CollectionSettings settings;
    @OneToMany(mappedBy = "collection", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CollectionCard> items;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Collection that = (Collection) o;
        return id.equals(that.id) &&
                owner.getId().equals(that.owner.getId()) &&
                name.equals(that.name) &&
                settings.equals(that.settings);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, owner.getId(), name, settings);
    }

    @Override
    public CollectionDto convertToDto(ModelMapper modelMapper) {
        CollectionDto dto = new CollectionDto();
        dto.setId(this.getId());
        dto.setName(this.getName());
        dto.setOwnerId(this.getOwner().getId());
        dto.setCollectionSettings(this.getSettings());
        List<CollectionCardDto> itemDtos = this.getItems().stream()
                .map(iDao -> iDao.convertToDto(modelMapper))
                .toList();
        dto.setCollectionItems(itemDtos);
        return dto;
    }

    public static class Builder {
        private Account owner;
        private String name;
        private CollectionSettings settings;

        public Builder withOwner(Account owner) {
            this.owner = owner;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withSettings(CollectionSettings settings) {
            this.settings = settings;
            return this;
        }

        public Collection build() {
            if (this.owner == null || this.name == null || this.settings == null) {
                throw new IllegalStateException("Missing one or more required properties");
            }
            return new Collection(null, this.owner, this.name, this.settings, null);
        }
    }
}
