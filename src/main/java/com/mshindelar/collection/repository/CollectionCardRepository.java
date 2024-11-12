package com.mshindelar.collection.repository;

import com.mshindelar.collection.model.card.Card;
import com.mshindelar.collection.model.collection.Collection;
import com.mshindelar.collection.model.collection.CollectionCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface CollectionCardRepository extends JpaRepository<CollectionCard, UUID>,
        JpaSpecificationExecutor<CollectionCard> {
    Optional<CollectionCard> findByCollectionAndCard(Collection collection, Card card);
}
