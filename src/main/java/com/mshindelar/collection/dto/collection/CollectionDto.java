package com.mshindelar.collection.dto.collection;

import com.mshindelar.collection.model.collection.CollectionSettings;
import lombok.*;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CollectionDto {
    private UUID id;
    private UUID ownerId;
    private String name;
    private CollectionSettings collectionSettings;
    private List<CollectionCardDto> collectionItems;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CollectionDto that = (CollectionDto) o;
        return id.equals(that.id) &&
                ownerId.equals(that.ownerId) &&
                name.equals(that.name) &&
                collectionSettings.equals(that.collectionSettings) &&
                collectionItems.equals(that.collectionItems);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, ownerId, name, collectionSettings, collectionItems);
    }
}
