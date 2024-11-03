package com.mshindelar.collection.controller;

import com.mshindelar.collection.dto.collection.CollectionDto;
import com.mshindelar.collection.exception.NoSuchAccountException;
import com.mshindelar.collection.exception.NoSuchCardException;
import com.mshindelar.collection.exception.NoSuchCollectionException;
import com.mshindelar.collection.model.card.Print;
import com.mshindelar.collection.model.collection.Collection;
import com.mshindelar.collection.model.collection.CollectionPrintOccurrence;
import com.mshindelar.collection.model.collection.request.CollectionCardRequestItem;
import com.mshindelar.collection.model.collection.request.CollectionOperation;
import com.mshindelar.collection.service.AccountService;
import com.mshindelar.collection.service.CardService;
import com.mshindelar.collection.service.CollectionService;
import com.mshindelar.collection.util.CsvUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/collection")
public class CollectionController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CollectionController.class);
    private static final String USER_HEADER = "COLLECTION-USER";
    @Autowired
    private CollectionService collectionService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private CardService cardService;
    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/{id}")
    public CollectionDto getCollection(@PathVariable UUID id)
            throws NoSuchAccountException, NoSuchCollectionException {
        //Do some validation

        Collection collection = this.collectionService.getCollection(id);
        return collection.convertToDto(modelMapper);
    }

    @PutMapping("/{id}/items")
    public void addItemsToCollection(@PathVariable UUID id,
                                     @RequestBody List<CollectionCardRequestItem> items,
                                     @RequestParam(required = false, defaultValue = "false") boolean ignoreFailure)
            throws NoSuchCollectionException {
        List<CollectionPrintOccurrence> occurrences = getOccurrences(id, items, CollectionOperation.ADD, ignoreFailure);
        this.collectionService.updateItemsInCollection(occurrences);
    }

    @DeleteMapping("/{id}/items")
    public void removeItemsFromCollection(@PathVariable UUID id,
                                          @RequestBody List<CollectionCardRequestItem> items,
                                          @RequestParam(required = false, defaultValue = "false") boolean ignoreFailure)
            throws NoSuchCollectionException {
        List<CollectionPrintOccurrence> occurrences = getOccurrences(id, items, CollectionOperation.REMOVE,
                ignoreFailure);
        this.collectionService.updateItemsInCollection(occurrences);
    }

    @PutMapping(value = "/{id}/items/file", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public void importItemsFromCsv(@PathVariable UUID id,
                                   @RequestParam("file") MultipartFile csv,
                                   @RequestParam(required = false, defaultValue = "false") boolean ignoreFailure)
            throws IOException,
            NoSuchCollectionException {
        List<CollectionCardRequestItem> requestItems = CsvUtils.read(CollectionCardRequestItem.class,
                csv.getInputStream());
        this.addItemsToCollection(id, requestItems, ignoreFailure);
    }

    @DeleteMapping(value = "/{id}/items/file", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public void deleteItemsFromCsv(@PathVariable UUID id,
                                   @RequestParam("file") MultipartFile csv,
                                   @RequestParam(required = false, defaultValue = "false") boolean ignoreFailure)
            throws IOException, NoSuchCollectionException {
        List<CollectionCardRequestItem> requestItems = CsvUtils.read(CollectionCardRequestItem.class,
                csv.getInputStream());
        this.removeItemsFromCollection(id, requestItems, ignoreFailure);
    }

    private List<CollectionPrintOccurrence> getOccurrences(UUID id, List<CollectionCardRequestItem> items,
                                                           CollectionOperation operation, boolean ignoreFailure) {
        List<CollectionPrintOccurrence> occurrences = items.stream()
                .map(i -> {
                    try {
                        return this.collectionService.modifyCollectionItem(id,
                                i.getSetCode(),
                                i.getSetRarityCode(),
                                i.getCondition(),
                                i.getEdition(),
                                i.getGrade(),
                                i.getQuantity(),
                                operation,
                                ignoreFailure);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();

        if (ignoreFailure) occurrences = occurrences.stream().filter(Objects::nonNull).toList();

        return occurrences;
    }
}