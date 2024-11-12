package com.mshindelar.collection.repository;

import com.mshindelar.collection.model.card.Print;
import com.mshindelar.collection.model.collection.CollectionCard;
import com.mshindelar.collection.model.collection.CollectionPrint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface CollectionPrintRepository extends JpaRepository<CollectionPrint, UUID> {
    Optional<CollectionPrint> findByCollectionCardAndPrint(CollectionCard collectionCard, Print print);
}
