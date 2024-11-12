package com.mshindelar.collection.controller;

import com.mshindelar.collection.dto.collection.CollectionCardDto;
import com.mshindelar.collection.dto.collection.CollectionDto;
import com.mshindelar.collection.model.card.Condition;
import com.mshindelar.collection.model.card.Edition;
import com.mshindelar.collection.model.card.Print;
import com.mshindelar.collection.model.collection.Collection;
import com.mshindelar.collection.model.collection.CollectionCard;
import com.mshindelar.collection.model.collection.request.CardFilterChain;
import com.mshindelar.collection.model.collection.request.CollectionCardRequestItem;
import com.mshindelar.collection.model.collection.request.CollectionOperation;
import com.mshindelar.collection.model.collection.request.FilterType;
import com.mshindelar.collection.db.filter.FilterOperator;
import com.mshindelar.collection.db.filter.card.CardFilter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;


public class CollectionControllerTest extends BaseContollerTest {
    private static String CSV =
            """    
                    name,setCode,setRarityCode,condition,edition,quantity,grade
                    Mermail Abyssmegalo,ABYR-EN020,(ScR),NEAR_MINT,FIRST,2,
                    Mermail Abyssleed,CBLZ-EN034,(ScR),NEAR_MINT,UNLIMITED,1,
                    Mermail Abyssteus,BLLR-EN051,(ScR),NEAR_MINT,FIRST,3,
                    Mermail Abysspike,AP05-EN007,(SR),NEAR_MINT,UNLIMITED,2,
                    Mermail Abyssgaios,ABYR-EN046,(UtR),NEAR_MINT,FIRST,1,
                    INVALID CARD,IVLD-EN000,(Scr),NEAR_MINT,LIMITED,1""";

    @Autowired
    private CollectionController collectionController;

    @Test
    public void testGetCollection() throws Exception {
        collectionService.modifyCollectionItem(testCollection.getId(), "ABYR-EN020", "(ScR)",
                Condition.NEAR_MINT, Edition.FIRST, -1, 3, CollectionOperation.ADD);
        testCollection = collectionService.getCollection(testCollection.getId());

        CollectionDto actual = collectionController.getCollection(testCollection.getId());
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(testCollection.convertToDto(modelMapper), actual);
    }

    @Test
    public void testGetCollection_DoesNotExist() throws Exception {
        Throwable t = null;

        try { collectionController.getCollection(UUID.randomUUID()); }
        catch (Exception e) { t = e; }

        Assertions.assertNotNull(t);
        Assertions.assertTrue(t instanceof ResponseStatusException);

        ResponseStatusException rse = (ResponseStatusException) t;

        Assertions.assertEquals(rse.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    public void testAddItemsToCollection() throws Exception {
        Collection c = collectionService.getCollection(testCollection.getId());
        Assertions.assertNotNull(c);
        Assertions.assertTrue(c.getItems().isEmpty());

        Print megalo = cardService.getPrint("ABYR-EN020", "(ScR)");
        Print leedCBLZ = cardService.getPrint("CBLZ-EN034", "(ScR)");
        Print leedSHVA = cardService.getPrint("SHVA-EN038", "(SR)");


        List<CollectionCardRequestItem> items = List.of(
                new CollectionCardRequestItem(megalo.getId(), megalo.getSetRarityCode(), Condition.NEAR_MINT, Edition.FIRST, 3, -1),
                new CollectionCardRequestItem(leedCBLZ.getId(), leedCBLZ.getSetRarityCode(), Condition.NEAR_MINT, Edition.FIRST, 3, -1),
                new CollectionCardRequestItem(leedSHVA.getId(), leedSHVA.getSetRarityCode(), Condition.NEAR_MINT, Edition.FIRST, 1, -1));

        collectionController.addItemsToCollection(testCollection.getId(), items, false);
        c = collectionService.getCollection(testCollection.getId());

        Assertions.assertNotNull(c);
        Assertions.assertEquals(2, c.getItems().size());
        CollectionCard megaloCard = collectionService.getCollectionCard(testCollection.getId(), megalo.getCard());
        Assertions.assertNotNull(megaloCard);
        Assertions.assertEquals(1, megaloCard.getPrints().size());
        CollectionCard leedCard = collectionService.getCollectionCard(testCollection.getId(), leedCBLZ.getCard());
        Assertions.assertNotNull(leedCard);
        Assertions.assertEquals(2, leedCard.getPrints().size());
    }

    @Test
    public void testAddItemsToCollection_PrintDoesNotExist() throws Exception {
        List<CollectionCardRequestItem> items = List.of(
          new CollectionCardRequestItem("IVLD-EN000", "(ScR)", Condition.NEAR_MINT, Edition.FIRST, 5, -1));

        Throwable t = null;
        try { collectionController.addItemsToCollection(testCollection.getId(), items, false); }
        catch (Exception e) { t = e; }

        Assertions.assertNotNull(t);
        Assertions.assertTrue(t instanceof ResponseStatusException);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, ((ResponseStatusException) t).getStatusCode());
    }

    @Test
    public void testAddItemsToCollection_PrintDoesNotExist_IgnoreFailure() throws Exception {
        Print megalo = cardService.getPrint("ABYR-EN020", "(ScR)");

        List<CollectionCardRequestItem> items = List.of(
                new CollectionCardRequestItem("IVLD-EN000", "(ScR)", Condition.NEAR_MINT, Edition.FIRST, 5, -1),
                new CollectionCardRequestItem(megalo.getId(), megalo.getSetRarityCode(), Condition.NEAR_MINT, Edition.FIRST, 3, -1));

        collectionController.addItemsToCollection(testCollection.getId(), items, true);

        Collection c = collectionService.getCollection(testCollection.getId());
        Assertions.assertNotNull(c);
        Assertions.assertEquals(1, c.getItems().size());

        CollectionCard card = c.getItems().getFirst();
        Assertions.assertEquals(card.getCard().getId(), megalo.getCard().getId());
    }

    @Test
    public void testAddItemsToCollection_CollectionDoesNotExist() throws Exception {
        List<CollectionCardRequestItem> items = List.of(
                new CollectionCardRequestItem("ABYR-EN020", "(ScR)", Condition.NEAR_MINT, Edition.FIRST, 5, -1));

        Throwable t = null;
        try { collectionController.addItemsToCollection(UUID.randomUUID(), items, false); }
        catch (Exception e) { t = e; }

        Assertions.assertNotNull(t);
        Assertions.assertTrue(t instanceof ResponseStatusException);
        Assertions.assertEquals(HttpStatus.NOT_FOUND, ((ResponseStatusException) t).getStatusCode());
    }

    @Test
    public void testAddItemsToCollection_CSV_IgnoreFailure() throws Exception {
        MultipartFile f = new MockMultipartFile("content.csv", CSV.getBytes(StandardCharsets.UTF_8));

        collectionController.importItemsFromCsv(testCollection.getId(), f, true);

        Collection c = collectionService.getCollection(testCollection.getId());
        Assertions.assertNotNull(c);
        Assertions.assertEquals(5, c.getItems().size());
    }

    @Test
    public void testAddItemsToCollection_CSV() throws Exception {
        MultipartFile f = new MockMultipartFile("content.csv", CSV.getBytes(StandardCharsets.UTF_8));

        Throwable t = null;
        try { collectionController.importItemsFromCsv(testCollection.getId(), f, false); }
        catch (Exception e) { t = e; }

        Assertions.assertNotNull(t);
        Assertions.assertTrue(t instanceof ResponseStatusException);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, ((ResponseStatusException) t).getStatusCode());
    }

    @Test
    public void testFilterItems() throws Exception {
        testAddItemsToCollection_CSV_IgnoreFailure();

        CardFilterChain outerChain = new CardFilterChain();
        outerChain.setFilterType(FilterType.OR);

        CardFilterChain innerChain1 = new CardFilterChain();
        innerChain1.setFilterType(FilterType.AND);
        CardFilterChain innerInnerChain1 = new CardFilterChain();
        innerInnerChain1.setFilterType(FilterType.SIMPLE);
        innerInnerChain1.setFilter(new CardFilter(CardFilter.CardField.LEVEL, FilterOperator.EQUAL_TO, 7));
        CardFilterChain innerInnerChain2 = new CardFilterChain();
        innerInnerChain2.setFilterType(FilterType.SIMPLE);
        innerInnerChain2.setFilter(new CardFilter(CardFilter.CardField.TYPE, FilterOperator.NOT_EQUAL, "XYZ Monster"));
        innerChain1.setChain(List.of(innerInnerChain1, innerInnerChain2));

        CardFilterChain innerChain2 = new CardFilterChain();
        innerChain2.setFilterType(FilterType.AND);

        CardFilterChain inner1InnerChain1 = new CardFilterChain();
        inner1InnerChain1.setFilterType(FilterType.SIMPLE);
        inner1InnerChain1.setFilter(new CardFilter(CardFilter.CardField.LEVEL, FilterOperator.LT, 7));

        CardFilterChain inner1InnerChain2 = new CardFilterChain();
        inner1InnerChain2.setFilterType(FilterType.SIMPLE);
        inner1InnerChain2.setFilter(new CardFilter(CardFilter.CardField.RACE, FilterOperator.EQUAL_TO, "Fish"));

        innerChain2.setChain(List.of(inner1InnerChain1, inner1InnerChain2));

        outerChain.setChain(List.of(innerChain1, innerChain2));

        List<CollectionCardDto> cards = this.collectionController.filterCollectionItems(testCollection.getId(), outerChain);

        Assertions.assertNotNull(cards);
        Assertions.assertEquals(4, cards.size());

    }

    @Test
    public void testFilterItems_ImproperlyFormattedFilter() {
        CardFilterChain outerChain = new CardFilterChain();
        outerChain.setFilterType(FilterType.OR);

        CardFilterChain innerChain1 = new CardFilterChain();
        innerChain1.setFilterType(FilterType.AND);
        CardFilterChain innerInnerChain1 = new CardFilterChain();
        innerInnerChain1.setFilterType(FilterType.SIMPLE);
        innerInnerChain1.setFilter(new CardFilter(CardFilter.CardField.LEVEL, FilterOperator.EQUAL_TO, 7));
        CardFilterChain innerInnerChain2 = new CardFilterChain();
        innerInnerChain2.setFilterType(FilterType.SIMPLE);
        innerInnerChain2.setFilter(new CardFilter(CardFilter.CardField.TYPE, FilterOperator.NOT_EQUAL, "XYZ Monster"));
        innerChain1.setChain(List.of(innerInnerChain1, innerInnerChain2));

        CardFilterChain innerChain2 = new CardFilterChain();
        innerChain2.setFilterType(FilterType.AND);

        CardFilterChain inner1InnerChain1 = new CardFilterChain();
        inner1InnerChain1.setFilterType(FilterType.SIMPLE);
        inner1InnerChain1.setFilter(new CardFilter(CardFilter.CardField.LEVEL, FilterOperator.LT, 7));

        innerChain2.setChain(List.of(inner1InnerChain1));

        outerChain.setChain(List.of(innerChain1, innerChain2));

        Throwable t = null;

        try { this.collectionController.filterCollectionItems(testCollection.getId(), outerChain); }
        catch (Exception e) { t = e; }

        Assertions.assertNotNull(t);
        Assertions.assertTrue(t instanceof ResponseStatusException);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, ((ResponseStatusException) t).getStatusCode());

        CardFilterChain chain2 = new CardFilterChain();
        chain2.setFilterType(FilterType.SIMPLE);

        try { this.collectionController.filterCollectionItems(testCollection.getId(), chain2); }
        catch (Exception e) { t = e; }

        Assertions.assertNotNull(t);
        Assertions.assertTrue(t instanceof ResponseStatusException);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, ((ResponseStatusException) t).getStatusCode());


    }
}
