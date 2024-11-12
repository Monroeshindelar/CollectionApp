package com.mshindelar.collection.model.collection;

import com.mshindelar.collection.model.card.CardFranchise;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CollectionSettings implements Serializable {
    @Enumerated(EnumType.STRING)
    private CardFranchise franchise;
    @Enumerated(EnumType.STRING)
    private CollectionPrivacy privacy;

    public static CollectionSettings withDefaults(CardFranchise franchise) {
        return new CollectionSettings(franchise, CollectionPrivacy.FRIENDS_ONLY);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CollectionSettings that = (CollectionSettings) o;
        return franchise == that.franchise && privacy == that.privacy;
    }

    @Override
    public int hashCode() {
        return Objects.hash(franchise, privacy);
    }
}
