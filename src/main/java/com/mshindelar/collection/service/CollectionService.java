package com.mshindelar.collection.service;

import com.mshindelar.collection.YGOPROApi.YGOPROApiClient;
import com.mshindelar.collection.db.filter.AbstractFilter;
import com.mshindelar.collection.exception.InvalidCollectionItemException;
import com.mshindelar.collection.exception.NoSuchAccountException;
import com.mshindelar.collection.exception.NoSuchCardException;
import com.mshindelar.collection.exception.NoSuchCollectionException;
import com.mshindelar.collection.model.Account;
import com.mshindelar.collection.model.card.Card;
import com.mshindelar.collection.model.card.Condition;
import com.mshindelar.collection.model.card.Edition;
import com.mshindelar.collection.model.card.Print;
import com.mshindelar.collection.model.collection.*;
import com.mshindelar.collection.model.collection.request.CollectionOperation;
import com.mshindelar.collection.db.filter.collection.CollectionCardSpecification;
import com.mshindelar.collection.repository.CollectionCardRepository;
import com.mshindelar.collection.repository.CollectionPrintOccurrenceRepository;
import com.mshindelar.collection.repository.CollectionPrintRepository;
import com.mshindelar.collection.repository.CollectionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CollectionService {
    private static Logger LOGGER = LoggerFactory.getLogger(CollectionService.class);

    @Autowired
    private YGOPROApiClient ygoproApiClient;
    @Autowired
    private CollectionRepository collectionRepository;
    @Autowired
    private CollectionCardRepository collectionCardRepository;
    @Autowired
    private CollectionPrintRepository collectionPrintRepository;
    @Autowired
    private CollectionPrintOccurrenceRepository collectionPrintOccurrenceRepository;
    @Autowired
    private AccountService accountService;
    @Autowired
    private CardService cardService;

    public Collection createNewCollection(String name, CollectionSettings collectionSettings, Account owner) {
        Collection collection = new Collection.Builder()
                .withOwner(owner)
                .withName(name)
                .withSettings(collectionSettings)
                .build();

        return this.saveCollection(collection);
    }

    public Collection getCollection(UUID id) throws NoSuchCollectionException {
        return this.collectionRepository.findById(id)
                .orElseThrow(() -> new NoSuchCollectionException(String.format("No such collection with id %s", id)));
    }

    public Collection getCollection(String name, UUID ownerId) throws NoSuchCollectionException, NoSuchAccountException {
        return this.collectionRepository.findByNameAndOwner(name, accountService.getAccountMustExist(ownerId))
                .orElseThrow(() ->
                        new NoSuchCollectionException(String.format("No such collection with name %s", name)));
    }

    public CollectionCard getCollectionCard(UUID collectionId, Card card) throws NoSuchCollectionException {
        Collection collection = this.getCollection(collectionId);
        return this.getCollectionCard(collection, card);
    }

    public boolean updateItemsInCollection(List<CollectionPrintOccurrence> itemsToAdd)
            throws NoSuchCollectionException {
        this.collectionPrintOccurrenceRepository.saveAll(itemsToAdd);
        return true;
    }
    public CollectionPrintOccurrence modifyCollectionItem(UUID collectionId, String printId, String setRarityCode,
                                                          Condition condition, Edition edition, int grade, int quantity,
                                                          CollectionOperation operation)
            throws NoSuchCardException, NoSuchCollectionException, InvalidCollectionItemException {
        return this.modifyCollectionItem(collectionId,
                printId,
                setRarityCode,
                condition,
                edition,
                grade,
                quantity,
                operation,
                false);
    }

    public CollectionPrintOccurrence modifyCollectionItem(UUID collecitonId, String printId, String setRarityCode,
                                                          Condition condition, Edition edition, int grade, int quantity,
                                                          CollectionOperation operation, boolean ignoreFailure)
            throws NoSuchCardException, NoSuchCollectionException, InvalidCollectionItemException {
        validateGrade(condition, grade);
        CollectionPrintOccurrence occurrence = null;
        try {
            Print p = cardService.getPrint(printId, setRarityCode);
            if (operation.equals(CollectionOperation.ADD)) {
                occurrence = this.getOrCreateCollectionPrintOccurrence(
                        collecitonId, p, condition, edition);
            } else if (operation.equals(CollectionOperation.REMOVE)) {
                occurrence = this.getCollectionPrintOccurrence(collecitonId, p, condition,
                        edition);
            }

            int adjustedTotal = occurrence.getQuantity();
            if (operation.equals(CollectionOperation.REMOVE)) {
                adjustedTotal = adjustedTotal - quantity;
                if (adjustedTotal < 1) {
                    this.deleteOccurrenceFromCollection(occurrence);
                    return null;
                }
            } else if (operation.equals(CollectionOperation.ADD)) {
                adjustedTotal = adjustedTotal + quantity;
            }

            occurrence.setQuantity(adjustedTotal);
        } catch (NoSuchCollectionException | NoSuchCardException e) {
            LOGGER.warn("Error constructing occurrence", e);
            if (!ignoreFailure) {
                throw e;
            }
        }

        return occurrence;
    }

    public CollectionPrintOccurrence getCollectionPrintOccurrence(UUID collectionId, Print print, Condition condition,
                                                                  Edition edition) throws NoSuchCollectionException {
        CollectionPrint collectionPrint = this.getCollectionPrint(collectionId, print);
        return this.getCollectionPrintOccurrence(collectionPrint, condition, edition);
    }

    public CollectionPrintOccurrence getCollectionPrintOccurrence(CollectionPrint collectionPrint, Condition condition,
                                                                  Edition edition) throws NoSuchCollectionException {
        CollectionPrintOccurrence.CollectionPrintOccurrenceId id = new CollectionPrintOccurrence
                .CollectionPrintOccurrenceId(collectionPrint, condition, edition);

        return this.collectionPrintOccurrenceRepository.findById(id).orElseThrow(() ->
                new NoSuchCollectionException(""));
    }

    public CollectionPrintOccurrence getOrCreateCollectionPrintOccurrence(UUID collectionId, Print print,
                                                                          Condition condition, Edition edition)
            throws NoSuchCollectionException {
        Collection collection = this.getCollection(collectionId);
        return this.getOrCreateCollectionPrintOccurrence(collection, print, condition, edition);
    }

    public Collection saveCollection(Collection collection) { return this.collectionRepository.save(collection); }

    public List<CollectionCard> getCollectionCardsByFilter(AbstractFilter filter) {
        return collectionCardRepository.findAll(new CollectionCardSpecification(filter));
    }

    public void getCollectionPrintsByFilter(AbstractFilter filter) {
    }

    private CollectionCard getCollectionCard(Collection collection, Card card) throws NoSuchCollectionException {
        return this.collectionCardRepository.findByCollectionAndCard(collection, card)
                .orElseThrow(() ->
                        new NoSuchCollectionException(String.format("Collection %s does not have card %s",
                                collection.getId(), card.getId())));
    }

    private CollectionCard getOrCreateCollectionCard(Collection collection, Card card) throws NoSuchCollectionException {
        return this.getOrCreateCollectionCard(collection, card, false);
    }

    private CollectionCard getOrCreateCollectionCard(Collection collection, Card card, boolean save) throws NoSuchCollectionException {
        CollectionCard collectionCard;

        try {
            collectionCard = this.getCollectionCard(collection, card);
        } catch (NoSuchCollectionException e) {
            LOGGER.info("No such card with id {} in collection {}, adding it...",
                    card.getId(), collection.getId());
            collectionCard = new CollectionCard.Builder()
                    .withCollection(collection)
                    .withCard(card)
                    .build();

            if (save) this.collectionCardRepository.save(collectionCard);
        }

        return collectionCard;
    }

    private CollectionPrint getCollectionPrint(UUID collectionId, Print print) throws NoSuchCollectionException {
        CollectionCard collectionCard = this.getCollectionCard(collectionId, print.getCard());
        return this.getCollectionPrint(collectionCard, print);
    }

    private CollectionPrint getCollectionPrint(CollectionCard collectionCard, Print print)
            throws NoSuchCollectionException {
        return this.collectionPrintRepository.findByCollectionCardAndPrint(collectionCard, print)
                .orElseThrow(() -> new NoSuchCollectionException(
                        String.format("Collection %s does not have card %s with print %s:%s",
                                collectionCard.getCollection().getId(),
                                print.getCard().getId(),
                                print.getId(),
                                print.getSetRarityCode())));
    }

    private CollectionPrint getOrCreateCollectionPrint(Collection collection, Print print)
            throws NoSuchCollectionException {
        return this.getOrCreateCollectionPrint(collection, print, false);
    }

    private CollectionPrint getOrCreateCollectionPrint(Collection collection, Print print, boolean save)
            throws NoSuchCollectionException {
        CollectionCard collectionCard = this.getOrCreateCollectionCard(collection, print.getCard(), true);
        CollectionPrint collectionPrint;

        try {
            collectionPrint = getCollectionPrint(collectionCard, print);
        } catch (NoSuchCollectionException e) {
            LOGGER.info("No such print {}:{} for card {} in collection {}, adding it...",
                    print.getId(), print.getSetRarityCode(), print.getCard().getId(), collection.getId());
            collectionPrint = new CollectionPrint.Builder()
                    .withCollectionCard(collectionCard)
                    .withPrint(print)
                    .build();

            if (save) this.collectionPrintRepository.save(collectionPrint);
        }

        return collectionPrint;
    }

    private CollectionPrintOccurrence getOrCreateCollectionPrintOccurrence(Collection collection, Print print,
                                                                          Condition condition, Edition edition)
            throws NoSuchCollectionException {
        return this.getOrCreateCollectionPrintOccurrence(collection, print, condition, edition, false);
    }

    private CollectionPrintOccurrence getOrCreateCollectionPrintOccurrence(Collection collection, Print print,
                                                                           Condition condition, Edition edition,
                                                                           boolean save)
            throws NoSuchCollectionException {
        CollectionPrint collectionPrint = getOrCreateCollectionPrint(collection, print, true);
        CollectionPrintOccurrence occurrence;

        try {
            occurrence = this.getCollectionPrintOccurrence(collectionPrint, condition, edition);
        } catch (NoSuchCollectionException e) {
            LOGGER.info("No such occurrence for {}:{}:{}:{} in collection {}, adding it...",
                    print.getId(), print.getSetRarityCode(), condition, edition, collection.getId());
            occurrence = new CollectionPrintOccurrence.Builder()
                    .withCollectionPrint(collectionPrint)
                    .withCondition(condition)
                    .withEdition(edition)
                    .build();

            if (save) this.collectionPrintOccurrenceRepository.save(occurrence);
        }

        return occurrence;
    }

    private void validateGrade(Condition condition, int grade) throws InvalidCollectionItemException {
        if (condition.equals(Condition.GRADE) && grade < 1) {
            throw new InvalidCollectionItemException("Cannot set condition to grade without providing a grade value");
        } else if (!condition.equals(Condition.GRADE) && grade > 0) {
            throw new InvalidCollectionItemException("Cannot set grade value if condition is not grade");
        }
    }

    private boolean deleteOccurrenceFromCollection(CollectionPrintOccurrence occurrence) {
        if (occurrence.getCollectionPrint().getOccurrences().size() == 1) {
            CollectionPrint collectionPrint = occurrence.getCollectionPrint();
            if (collectionPrint.getCollectionCard().getPrints().size() == 1) {
                LOGGER.info("Removing card {} for collection {} as it no longer as any prints",
                        collectionPrint.getCollectionCard().getCard().getId(),
                        collectionPrint.getCollectionCard().getCollection().getId());
                this.collectionCardRepository.delete(collectionPrint.getCollectionCard());
            } else {
                LOGGER.info("Removing print {}:{} for card {} in collection {} as it no longer has any occurrences",
                        collectionPrint.getPrint().getId(), collectionPrint.getPrint().getSetRarityCode(),
                        collectionPrint.getCollectionCard().getCard().getId(),
                        collectionPrint.getCollectionCard().getCollection().getId());
                this.collectionPrintRepository.delete(collectionPrint);
            }
        } else {
            LOGGER.info("Removing occurrence {}:{}:{}:{} for card {} in collection {}",
                    occurrence.getCollectionPrint().getPrint().getId(),
                    occurrence.getCollectionPrint().getPrint().getSetRarityCode(),
                    occurrence.getCondition(), occurrence.getEdition(),
                    occurrence.getCollectionPrint().getCollectionCard().getCard().getId(),
                    occurrence.getCollectionPrint().getCollectionCard().getCollection().getId());
            this.collectionPrintOccurrenceRepository.delete(occurrence);
        }

        return true;
    }
}
