package com.mshindelar.collection.repository;

import com.mshindelar.collection.model.card.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface CardRepository extends JpaRepository<Card, String>, JpaSpecificationExecutor<Card> {
    @Query("SELECT DISTINCT c.type FROM Card c ORDER BY c.type ASC")
    List<String> findDistinctTypeAsc();

    @Query("SELECT DISTINCT c.race FROM Card c ORDER BY c.race ASC")
    List<String> findUniqueRaceAsc();

    @Query("SELECT DISTINCT c.archetype FROM Card c ORDER BY c.archetype ASC")
    List<String> findUniqueArchetypeAsc();
}
