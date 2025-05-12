package com.siemens.internship.service;

import com.siemens.internship.model.Item;
import com.siemens.internship.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Vector;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ItemService {
    @Autowired
    private ItemRepository itemRepository;
    private static ExecutorService executor = Executors.newFixedThreadPool(10);
    private Vector<Item> processedItems = new Vector<>();
    private AtomicInteger processedCount = new AtomicInteger(0);


    /**
     * Retrieves all items from the repository.
     *
     * @return a list of all items found in the repository
     */
    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    /**
     * Retrieves an item by its unique identifier.
     *
     * @param id the unique identifier of the item to be retrieved
     * @return an Optional containing the Item if it exists, or an empty Optional if the item is not found
     */
    public Optional<Item> findById(Long id) {
        return itemRepository.findById(id);
    }

    /**
     * Saves the given item to the repository.
     *
     * @param item the Item object to be saved
     * @return the saved Item object
     */
    public Item save(Item item) {
        return itemRepository.save(item);
    }

    /**
     * Deletes an item from the repository by its unique identifier.
     *
     * @param id the unique identifier of the item to be deleted
     */
    public void deleteById(Long id) {
        itemRepository.deleteById(id);
    }


    /**
     * Your Tasks
     * Identify all concurrency and asynchronous programming issues in the code
     * Fix the implementation to ensure:
     * All items are properly processed before the CompletableFuture completes
     * Thread safety for all shared state
     * Proper error handling and propagation
     * Efficient use of system resources
     * Correct use of Spring's @Async annotation
     * Add appropriate comments explaining your changes and why they fix the issues
     * Write a brief explanation of what was wrong with the original implementation
     *
     * Hints
     * Consider how CompletableFuture composition can help coordinate multiple async operations
     * Think about appropriate thread-safe collections
     * Examine how errors are handled and propagated
     * Consider the interaction between Spring's @Async and CompletableFuture
     */
    @Async
    public List<Item> processItemsAsync() {

        List<Long> itemIds = itemRepository.findAllIds();

        for (Long id : itemIds) {
            CompletableFuture.runAsync(() -> {
                try {
                    Thread.sleep(100);

                    Item item = itemRepository.findById(id).orElse(null);
                    if (item == null) {
                        return;
                    }

                    processedCount.incrementAndGet(); //

                    item.setStatus("PROCESSED");
                    itemRepository.save(item);
                    processedItems.add(item);
                } catch (InterruptedException e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }, executor);
        }

        return processedItems;
    }

}

