package com.siemens.internship.controller;

import com.siemens.internship.exception.ItemInsertionException;
import com.siemens.internship.service.ItemService;
import com.siemens.internship.model.Item;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * The ItemController class is a REST controller that provides endpoints
 * to perform CRUD operations on Item entities. It also includes a
 * method to asynchronously process items.
 */
@RestController
@RequestMapping("/api/items")
public class ItemController {

    @Autowired
    private ItemService itemService;

    /**
     * Retrieves all items from the database.
     *
     * @return a ResponseEntity containing a list of all items with an HTTP status of OK
     */
    @GetMapping
    public ResponseEntity<List<Item>> getAllItems() {
        return new ResponseEntity<>(itemService.findAll(), HttpStatus.OK);
    }

    /**
     * Creates a new item and saves it to the database.
     *
     * @param item the Item object to be created, validated using the @Valid annotation
     * @param result the BindingResult object containing validation errors, if any
     * @return a ResponseEntity containing the created Item object with an HTTP status of CREATED
     *         if successful, or null with an HTTP status of BAD_REQUEST if validation errors exist
     */
    @PostMapping
    public ResponseEntity<Item> createItem(@Valid @RequestBody Item item, BindingResult result) {
        if (result.hasErrors()) {
            throw new ItemInsertionException(result);
        }
        return new ResponseEntity<>(itemService.save(item), HttpStatus.CREATED);
    }

    /**
     * Retrieves an item by its unique identifier.
     *
     * @param id the unique identifier of the item to be retrieved
     * @return a ResponseEntity containing the retrieved Item object with an HTTP status of OK
     *         if the item is found, or an HTTP status of NOT_FOUND if the item does not exist
     */
    @GetMapping("/{id}")
    public ResponseEntity<Item> getItemById(@PathVariable Long id) {
        return itemService.findById(id)
                .map(item -> new ResponseEntity<>(item, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Updates an existing item with the provided details.
     * If the item with the specified ID exists, it updates the item and returns the updated item.
     * If the item does not exist, it returns a NOT_FOUND status.
     *
     * @param id   the unique identifier of the item to be updated
     * @param item the Item object containing the updated details
     * @return a ResponseEntity containing the updated Item object with an HTTP status of OK
     *         if the update is successful, or an HTTP status of NOT_FOUND if the item does not exist
     */
    @PutMapping("/{id}")
    public ResponseEntity<Item> updateItem(@PathVariable Long id, @RequestBody Item item) {
        Optional<Item> existingItem = itemService.findById(id);
        if (existingItem.isPresent()) {
            item.setId(id);
            return new ResponseEntity<>(itemService.save(item), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Deletes an item identified by its unique ID. It does it silently for security purposes.
     * The caller should not be informed if the information was successful or not.
     *
     * @param id the unique identifier of the item to be deleted
     * @return a ResponseEntity with an HTTP status of NO_CONTENT if the deletion is successful
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        itemService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Asynchronously processes a list of items by invoking the corresponding service method.
     * Returns a response indicating that the processing has been accepted.
     *
     * @return a ResponseEntity containing a list of processed items and the HTTP status ACCEPTED
     */
    @GetMapping("/process")
    public ResponseEntity<List<Item>> processItems() {
        return new ResponseEntity<>(itemService.processItemsAsync(), HttpStatus.ACCEPTED);
    }
}
